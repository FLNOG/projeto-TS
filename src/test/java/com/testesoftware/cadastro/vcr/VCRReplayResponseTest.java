package com.testesoftware.cadastro.vcr;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class VCRReplayResponseTest {

    @Test
    void testGetStatusCode() throws Exception {
        VCRInteraction interaction = new VCRInteraction(
                "GET",
                "http://localhost",
                "",
                200,
                "{}"
        );

        VCRReplayResponse response = new VCRReplayResponse(interaction);

        HttpStatusCode statusCode = response.getStatusCode();
        assertEquals(200, statusCode.value());
    }

    @Test
    void testGetRawStatusCode() throws Exception {
        VCRInteraction interaction = new VCRInteraction(
                "GET",
                "http://localhost",
                "",
                201,
                "{}"
        );

        VCRReplayResponse response = new VCRReplayResponse(interaction);

        assertEquals(201, response.getRawStatusCode());
    }

    @Test
    void testGetStatusText() throws Exception {
        VCRInteraction interaction = new VCRInteraction(
                "GET",
                "http://localhost",
                "",
                404,
                "{}"
        );

        VCRReplayResponse response = new VCRReplayResponse(interaction);

        assertEquals("Not Found", response.getStatusText());
    }

    @Test
    void testGetHeaders() {
        VCRInteraction interaction = new VCRInteraction(
                "GET",
                "http://localhost",
                "",
                200,
                "{}"
        );

        VCRReplayResponse response = new VCRReplayResponse(interaction);
        HttpHeaders headers = response.getHeaders();

        assertTrue(headers.containsKey("Content-Type"));
        assertEquals("application/json", headers.getFirst("Content-Type"));
    }

    @Test
    void testGetBody() throws Exception {
        String expectedBody = "{ \"cep\": \"00000000\" }";

        VCRInteraction interaction = new VCRInteraction(
                "GET",
                "http://localhost",
                "",
                200,
                expectedBody
        );

        VCRReplayResponse response = new VCRReplayResponse(interaction);

        String responseBody =
                StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);

        assertEquals(expectedBody, responseBody);
    }

    @Test
    void testCloseDoesNotThrow() {
        VCRInteraction interaction = new VCRInteraction(
                "GET",
                "http://localhost",
                "",
                200,
                "{}"
        );

        VCRReplayResponse response = new VCRReplayResponse(interaction);

        assertDoesNotThrow(response::close);
    }
}