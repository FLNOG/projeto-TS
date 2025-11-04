package com.testesoftware.cadastro.vcr;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class VCRServiceTest {

    private VCRService vcrService;
    private static final String CASSETTE_DIR = "src/test/resources/vcr_cassettes";

    @BeforeEach
    void setUp() {
        vcrService = new VCRService();
    }

    @AfterEach
    void cleanUp() throws IOException {
        Path dir = Paths.get(CASSETTE_DIR);
        if (Files.exists(dir)) {
            Files.walk(dir)
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    void testSaveAndLoadCassette() throws IOException {
        String cassetteName = "test_save_load";

        VCRInteraction interaction = new VCRInteraction();
        interaction.setMethod("GET");
        interaction.setUrl("https://example.com/test");
        interaction.setRequestBody("");
        interaction.setResponseBody("OK");
        interaction.setResponseStatus(200);

        List<VCRInteraction> interactions = Collections.singletonList(interaction);

        vcrService.saveCassette(cassetteName, interactions);

        assertTrue(vcrService.cassetteExists(cassetteName));

        VCRRecording loaded = vcrService.loadCassette(cassetteName);

        assertNotNull(loaded);
        assertEquals(1, loaded.getInteractions().size());

        VCRInteraction saved = loaded.getInteractions().get(0);

        assertEquals("GET", saved.getMethod());
        assertEquals("https://example.com/test", saved.getUrl());
        assertEquals("OK", saved.getResponseBody());
        assertEquals(200, saved.getResponseStatus());
    }

    @Test
    void testCassetteExistsFalse() {
        assertFalse(vcrService.cassetteExists("nope"));
    }

    @Test
    void testGetInteractionsForUrl() throws IOException {
        String cassetteName = "test_interactions";

        VCRInteraction i1 = new VCRInteraction();
        i1.setMethod("GET");
        i1.setUrl("https://url1");
        i1.setResponseStatus(200);

        VCRInteraction i2 = new VCRInteraction();
        i2.setMethod("POST");
        i2.setUrl("https://url2");
        i2.setResponseStatus(200);

        vcrService.saveCassette(cassetteName, Arrays.asList(i1, i2));

        List<VCRInteraction> filtered =
                vcrService.getInteractionsForUrl(cassetteName, "https://url1", "GET");

        assertEquals(1, filtered.size());
        assertEquals("https://url1", filtered.get(0).getUrl());
        assertEquals("GET", filtered.get(0).getMethod());
    }

    @Test
    void testLoadCassetteWhenNotExistsReturnsEmptyRecording() throws IOException {
        VCRRecording recording = vcrService.loadCassette("not_found");
        assertNotNull(recording);
        assertTrue(recording.getInteractions().isEmpty());
    }
}