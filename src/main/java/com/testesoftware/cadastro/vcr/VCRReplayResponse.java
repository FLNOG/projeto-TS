package com.testesoftware.cadastro.vcr;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class VCRReplayResponse implements ClientHttpResponse {
    
    private final VCRInteraction interaction;
    
    public VCRReplayResponse(VCRInteraction interaction) {
        this.interaction = interaction;
    }

    @Override
    public HttpStatusCode getStatusCode() throws IOException {
        return HttpStatus.valueOf(interaction.getResponseStatus());
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return interaction.getResponseStatus();
    }

    @Override
    public String getStatusText() throws IOException {
        return HttpStatus.valueOf(interaction.getResponseStatus()).getReasonPhrase();
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return headers;
    }

    @Override
    public InputStream getBody() throws IOException {
        return new ByteArrayInputStream(interaction.getResponseBody().getBytes());
    }

    @Override
    public void close() {
        // No resources to close
    }
}