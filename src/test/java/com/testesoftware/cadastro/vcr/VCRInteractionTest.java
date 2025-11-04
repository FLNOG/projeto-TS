package com.testesoftware.cadastro.vcr;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VCRInteractionTest {

    @Test
    void testDefaultConstructorInitializesTimestamp() {
        VCRInteraction interaction = new VCRInteraction();

        assertNotNull(interaction.getTimestamp());
        assertNull(interaction.getMethod());
        assertNull(interaction.getUrl());
        assertNull(interaction.getRequestBody());
        assertNull(interaction.getResponseBody());
        assertEquals(0, interaction.getResponseStatus());
    }

    @Test
    void testParameterizedConstructor() {
        String method = "GET";
        String url = "https://example.com";
        String requestBody = "";
        int status = 200;
        String responseBody = "OK";

        VCRInteraction interaction = new VCRInteraction(method, url, requestBody, status, responseBody);

        assertEquals(method, interaction.getMethod());
        assertEquals(url, interaction.getUrl());
        assertEquals(requestBody, interaction.getRequestBody());
        assertEquals(status, interaction.getResponseStatus());
        assertEquals(responseBody, interaction.getResponseBody());
        assertNotNull(interaction.getTimestamp());
    }

    @Test
    void testGettersAndSetters() {
        VCRInteraction interaction = new VCRInteraction();

        interaction.setMethod("POST");
        interaction.setUrl("https://example.com/test");
        interaction.setRequestBody("body");
        interaction.setResponseStatus(201);
        interaction.setResponseBody("CREATED");

        LocalDateTime now = LocalDateTime.now();
        interaction.setTimestamp(now);

        assertEquals("POST", interaction.getMethod());
        assertEquals("https://example.com/test", interaction.getUrl());
        assertEquals("body", interaction.getRequestBody());
        assertEquals(201, interaction.getResponseStatus());
        assertEquals("CREATED", interaction.getResponseBody());
        assertEquals(now, interaction.getTimestamp());
    }
}