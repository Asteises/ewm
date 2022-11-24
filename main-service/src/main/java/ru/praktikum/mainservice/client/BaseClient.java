package ru.praktikum.mainservice.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {

    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    public <T> ResponseEntity<Object> post(String path, T body) {

        return makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

    public <T> ResponseEntity<Object> get(String patch, @Nullable Map<String, Object> parameters) {

        return makeAndSendRequest(HttpMethod.GET, patch, parameters, null);
    }

    public Integer getInteger(String patch, @Nullable Map<String, Object> parameters) {

        return makeAndSendRequestInteger(HttpMethod.GET, patch, parameters).getBody();
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable T body) {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> ewmServerResponse;

        try {
            if (parameters != null) {
                ewmServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                ewmServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(ewmServerResponse);
    }

    private ResponseEntity<Integer> makeAndSendRequestInteger(HttpMethod method,
                                                          String path,
                                                          @Nullable Map<String, Object> parameters) {

        HttpEntity<?> requestEntity = new HttpEntity<>(null, defaultHeaders());

        ResponseEntity<Integer> ewmServerResponse;

        try {
            if (parameters != null) {
                ewmServerResponse = rest.exchange(path, method, requestEntity, Integer.class, parameters);
            } else {
                ewmServerResponse = rest.exchange(path, method, requestEntity, Integer.class);
            }
        } catch (HttpStatusCodeException e) {
            //return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
            return null;
        }
        return ewmServerResponse;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
