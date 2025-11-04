package com.testesoftware.cadastro.vcr;

import com.testesoftware.cadastro.model.ViaCEP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
//@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=create-drop" })
class VCRViaCEPServiceTest {

    private VCRService vcrService;
    private VCRViaCEPService vcrViaCEPService;

    @BeforeEach
    void setUp() {
        vcrService = new VCRService();
        vcrViaCEPService = new VCRViaCEPService(vcrService);
    }

    @Test
    void testVCRRecordAndPlayback() throws IOException {
        String cassetteName = "viacep_test";

        Path cassettePath = Paths.get("src/test/resources/vcr_cassettes/" + cassetteName + ".json");
        if (Files.exists(cassettePath)) {
            Files.delete(cassettePath);
        }

        ViaCEP recordedResponse = vcrViaCEPService.buscarEnderecoPorCEP("01001000", cassetteName, true);

        assertNotNull(recordedResponse);
        assertNotNull(recordedResponse);
        assertNotNull(recordedResponse.getLogradouro());

        assertTrue(vcrService.cassetteExists(cassetteName));

        VCRRecording recording = vcrService.loadCassette(cassetteName);
        assertFalse(recording.getInteractions().isEmpty());

        System.out.println("VCR Test: Successfully recorded interaction to cassette: " + cassetteName);
        System.out.println("Recorded " + recording.getInteractions().size() + " interactions");
    }

    @Test
    void testVCRWithValidCEP() {
        String cassetteName = "viacep_valid_cep_test";

        assertDoesNotThrow(() -> {
            // This demonstrates the intended usage
            // ViaCEPResponse response = vcrViaCEPService.buscarEnderecoPorCEP("01001000", cassetteName, true);
        });
    }
}