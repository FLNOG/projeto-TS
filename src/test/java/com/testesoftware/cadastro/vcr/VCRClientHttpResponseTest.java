package com.testesoftware.cadastro.vcr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VCRClientHttpResponseTest {

    private ClientHttpResponse originalResponseMock;
    private VCRClientHttpResponse vcrResponse;
    private final String recordedBody = "Resposta gravada";

    @BeforeEach
    void setup() {
        originalResponseMock = mock(ClientHttpResponse.class);
        vcrResponse = new VCRClientHttpResponse(originalResponseMock, recordedBody);
    }

    @Test
    void testGetStatusCode() throws IOException {
        HttpStatusCode code = HttpStatusCode.valueOf(200);
        when(originalResponseMock.getStatusCode()).thenReturn(code);

        assertEquals(code, vcrResponse.getStatusCode());
    }

    @Test
    void testGetRawStatusCode() throws IOException {
        when(originalResponseMock.getRawStatusCode()).thenReturn(201);

        assertEquals(201, vcrResponse.getRawStatusCode());
    }

    @Test
    void testGetStatusText() throws IOException {
        when(originalResponseMock.getStatusText()).thenReturn("OK");

        assertEquals("OK", vcrResponse.getStatusText());
    }

    @Test
    void testGetHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        when(originalResponseMock.getHeaders()).thenReturn(headers);

        assertEquals(headers, vcrResponse.getHeaders());
        assertEquals("application/json", vcrResponse.getHeaders().getFirst("Content-Type"));
    }

    @Test
    void testGetBody() throws IOException {
        InputStream stream = vcrResponse.getBody();
        byte[] content = stream.readAllBytes();

        assertEquals(recordedBody, new String(content));
    }

    @Test
    void testCloseDelegatesToOriginal() throws IOException {
        vcrResponse.close();
        verify(originalResponseMock, times(1)).close();
    }
}