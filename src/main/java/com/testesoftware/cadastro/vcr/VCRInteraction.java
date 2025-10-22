package com.testesoftware.cadastro.vcr;

import java.time.LocalDateTime;

public class VCRInteraction {
    private String method;
    private String url;
    private String requestBody;
    private int responseStatus;
    private String responseBody;
    private LocalDateTime timestamp;

    public VCRInteraction() {
        this.timestamp = LocalDateTime.now();
    }

    public VCRInteraction(String method, String url, String requestBody, int responseStatus, String responseBody) {
        this();
        this.method = method;
        this.url = url;
        this.requestBody = requestBody;
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}