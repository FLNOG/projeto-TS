package com.testesoftware.cadastro.vcr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VCRInterceptorTest {

    private VCRService mockVcrService;
    private ClientHttpRequestExecution mockExecution;
    private ClientHttpResponse mockResponse;

    private final String cassetteName = "cassetteTest";
    private final String url = "https://api.com/test";
    private final String method = "GET";

    @BeforeEach
    void setup() {
        mockVcrService = mock(VCRService.class);
        mockExecution = mock(ClientHttpRequestExecution.class);
        mockResponse = mock(ClientHttpResponse.class);
    }

    @Test
    void testRecordModeStoresInteractionAndReturnsWrappedResponse() throws IOException {
        VCRInterceptor interceptor = new VCRInterceptor(mockVcrService, cassetteName, true);

        String responseBody = "result-content";
        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);

        // ✅ Mock status code
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        when(mockResponse.getRawStatusCode()).thenReturn(200);

        when(mockResponse.getBody()).thenReturn(new ByteArrayInputStream(responseBytes));
        when(mockExecution.execute(any(), any())).thenReturn(mockResponse);

        when(mockVcrService.loadCassette(cassetteName)).thenReturn(new VCRRecording());

        HttpRequest mockRequest = mock(HttpRequest.class);
        when(mockRequest.getURI()).thenReturn(java.net.URI.create(url));
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);

        ClientHttpResponse result = interceptor.intercept(
                mockRequest,
                "".getBytes(StandardCharsets.UTF_8),
                mockExecution
        );

        assertNotNull(result);
        assertTrue(result instanceof VCRClientHttpResponse);

        ArgumentCaptor<List<VCRInteraction>> interactionCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockVcrService).saveCassette(eq(cassetteName), interactionCaptor.capture());

        List<VCRInteraction> recorded = interactionCaptor.getValue();
        assertEquals(1, recorded.size());
        assertEquals(method, recorded.get(0).getMethod());
        assertEquals(url, recorded.get(0).getUrl());
        assertEquals(responseBody, recorded.get(0).getResponseBody());
    }

    @Test
    void testReplayModeReturnsInteraction() throws IOException {
        VCRInteraction interaction = new VCRInteraction(method, url, "", 200, "cached-response");

        when(mockVcrService.getInteractionsForUrl(cassetteName, url, method))
                .thenReturn(List.of(interaction));

        VCRInterceptor interceptor = new VCRInterceptor(mockVcrService, cassetteName, false);

        HttpRequest mockRequest = mock(HttpRequest.class);
        when(mockRequest.getURI()).thenReturn(java.net.URI.create(url));
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);

        ClientHttpResponse response = interceptor.intercept(
                mockRequest,
                new byte[0],
                mockExecution
        );

        assertNotNull(response);

        String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        assertEquals("cached-response", body);
    }

    @Test
    void testReplayModeThrowsIfNoInteraction() throws IOException {
        when(mockVcrService.getInteractionsForUrl(cassetteName, url, method))
                .thenReturn(List.of());

        VCRInterceptor interceptor = new VCRInterceptor(mockVcrService, cassetteName, false);

        HttpRequest mockRequest = mock(HttpRequest.class);
        when(mockRequest.getURI()).thenReturn(java.net.URI.create(url));
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);

        assertThrows(RuntimeException.class, () ->
                interceptor.intercept(mockRequest, new byte[0], mockExecution)
        );
    }

    @Test
    void testRecordModeThrowsIfSaveCassetteFails() throws IOException {
        VCRInterceptor interceptor = new VCRInterceptor(mockVcrService, cassetteName, true);

        // ✅ Mock status
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        when(mockResponse.getRawStatusCode()).thenReturn(200);

        when(mockResponse.getBody()).thenReturn(new ByteArrayInputStream("body".getBytes()));
        when(mockExecution.execute(any(), any())).thenReturn(mockResponse);

        when(mockVcrService.loadCassette(cassetteName)).thenReturn(new VCRRecording());
        doThrow(new IOException("Fail")).when(mockVcrService).saveCassette(eq(cassetteName), any());

        HttpRequest mockRequest = mock(HttpRequest.class);
        when(mockRequest.getURI()).thenReturn(java.net.URI.create(url));
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);

        assertThrows(RuntimeException.class, () ->
                interceptor.intercept(mockRequest, new byte[0], mockExecution)
        );
    }
}