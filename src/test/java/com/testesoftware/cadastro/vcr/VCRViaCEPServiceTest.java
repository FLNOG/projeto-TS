package com.testesoftware.cadastro.vcr;

import com.testesoftware.cadastro.model.ViaCEP;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class VCRViaCEPServiceTest {

    private VCRService vcrService;
    private VCRViaCEPService vcrViaCEPService;

    @BeforeEach
    void setUp() {
        vcrService = new VCRService();
        vcrViaCEPService = new VCRViaCEPService(vcrService);
    }

    @Test
    void shouldRecordInteractionAndPersistCassetteFile() throws IOException {
        String cassetteName = "viacep_test_record";

        Path cassettePath = Paths.get("src/test/resources/vcr_cassettes/" + cassetteName + ".json");
        if (Files.exists(cassettePath)) {
            Files.delete(cassettePath);
        }

        ViaCEP resp = vcrViaCEPService.buscarEnderecoPorCEP("01001000", cassetteName, true);

        assertNotNull(resp);
        assertNotNull(resp.getLogradouro());
        assertTrue(vcrService.cassetteExists(cassetteName));

        VCRRecording recording = vcrService.loadCassette(cassetteName);
        assertFalse(recording.getInteractions().isEmpty());
    }


    @Test
    void shouldCreateCassetteFileWhenRecording() {
        String cassetteName = "viacep_file_create";

        Path path = Paths.get("src/test/resources/vcr_cassettes/" + cassetteName + ".json");
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {}

        vcrViaCEPService.buscarEnderecoPorCEP("01001000", cassetteName, true);

        assertTrue(Files.exists(path), "O arquivo cassette deve existir.");
    }

    @Test
    void shouldUseExistingCassetteForPlayback() throws IOException {
        String cassetteName = "viacep_test_playback";

        ViaCEP resp = vcrViaCEPService.buscarEnderecoPorCEP("01001000", cassetteName, true);
        assertNotNull(resp);
        assertTrue(vcrService.cassetteExists(cassetteName));

        ViaCEP playbackResp = vcrViaCEPService.buscarEnderecoPorCEP("01001000", cassetteName, false);

        assertNotNull(playbackResp);

        assertEquals(
                resp.getCep().replace("-", ""),
                playbackResp.getCep().replace("-", ""),
                "O CEP deve ser igual a resposta gravada e a reproduzida."
        );

        assertEquals(resp.getLogradouro(), playbackResp.getLogradouro(),
                "O Logradouro deve ser igual a resposta gravada e a reproduzida."
        );
    }

    @Test
    void shouldThrowExceptionWhenCassetteIsNotFoundInPlaybackMode() {
        String cassetteName = "inexistent_cassette_xyz";

        assertThrows(RuntimeException.class, () ->
                vcrViaCEPService.buscarEnderecoPorCEP("01001000", cassetteName, false)
        );
    }

    @Test
    void shouldThrowExceptionWhenPlaybackIsAttemptedWithoutExistingCassette() {
        String cassetteName = "missing_playback";

        Path cassettePath = Paths.get("src/test/resources/vcr_cassettes/" + cassetteName + ".json");
        try {
            Files.deleteIfExists(cassettePath);
        } catch (IOException ignored) {}

        assertThrows(RuntimeException.class, () ->
                vcrViaCEPService.buscarEnderecoPorCEP("01001000", cassetteName, false)
        );
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForInvalidCep() {
        assertThrows(IllegalArgumentException.class, () ->
                vcrViaCEPService.buscarEnderecoPorCEP("123", "cass_invalid", true)
        );
    }

    @Test
    void shouldNotThrowExceptionWhenCheckingForCassetteExistence() {
        assertDoesNotThrow(() -> {
            vcrService.cassetteExists("teste");
        });
    }
}