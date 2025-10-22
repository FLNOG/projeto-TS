package com.testesoftware.cadastro.vcr;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class VCRClientHttpResponse implements ClientHttpResponse {
    
    private final ClientHttpResponse originalResponse;
    private final String responseBody;
    
    public VCRClientHttpResponse(ClientHttpResponse originalResponse, String responseBody) {
        this.originalResponse = originalResponse;
        this.responseBody = responseBody;
    }

    @Override
    public HttpStatusCode getStatusCode() throws IOException {
        return originalResponse.getStatusCode();
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return originalResponse.getRawStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
        return originalResponse.getStatusText();
    }

    @Override
    public HttpHeaders getHeaders() {
        return originalResponse.getHeaders();
    }

    @Override
    public InputStream getBody() throws IOException {
        return new ByteArrayInputStream(responseBody.getBytes());
    }

    @Override
    public void close() {
        originalResponse.close();
    }
}