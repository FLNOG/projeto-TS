package com.testesoftware.cadastro.vcr;

import com.testesoftware.cadastro.model.ViaCEP;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VCRViaCEPService {

    private final VCRService vcrService;
    private RestTemplate restTemplate;

    public VCRViaCEPService(VCRService vcrService) {
        this.vcrService = vcrService;
        this.restTemplate = new RestTemplate();
    }

    public ViaCEP buscarEnderecoPorCEP(String cep, String cassetteName, boolean recordMode) {
        String cepFormatado = cep.replaceAll("[^0-9]", "");

        if (cepFormatado.length() != 8) {
            throw new IllegalArgumentException("CEP inválido: deve conter 8 dígitos");
        }

        String url = "https://viacep.com.br/ws/" + cepFormatado + "/json/";

        if (recordMode) {
            VCRInterceptor interceptor = new VCRInterceptor(vcrService, cassetteName, true);
            restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(interceptor);
        } else {
            if (!vcrService.cassetteExists(cassetteName)) {
                throw new RuntimeException("Cassette " + cassetteName + " does not exist. Run in record mode first.");
            }

            try {
                VCRRecording recording = vcrService.loadCassette(cassetteName);

                if (!recording.getInteractions().isEmpty()) {
                    String recordedResponse = recording.getInteractions().get(0).getResponseBody();

                    return parseViaCEPResponse(recordedResponse);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error loading recorded response", e);
            }
        }

        try {
            if (recordMode) {
                ResponseEntity<ViaCEP> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        ViaCEP.class
                );

                ViaCEP result = response.getBody();
                if (result != null) {
                    result.setCep(cepFormatado);
                }

                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar CEP na API ViaCEP: " + e.getMessage(), e);
        }
    }

    private ViaCEP parseViaCEPResponse(String jsonResponse) {
        ViaCEP response = new ViaCEP();

        if (jsonResponse.contains("\"cep\"")) {
            int start = jsonResponse.indexOf("\"cep\":\"") + 7;
            int end = jsonResponse.indexOf("\"", start);
            if (start > 6 && end > start) {
                response.setCep(jsonResponse.substring(start, end));
            }
        }

        if (jsonResponse.contains("\"logradouro\"")) {
            int start = jsonResponse.indexOf("\"logradouro\":\"") + 13;
            int end = jsonResponse.indexOf("\"", start);
            if (start > 12 && end > start) {
                response.setLogradouro(jsonResponse.substring(start, end));
            }
        }

        if (jsonResponse.contains("\"bairro\"")) {
            int start = jsonResponse.indexOf("\"bairro\":\"") + 10;
            int end = jsonResponse.indexOf("\"", start);
            if (start > 9 && end > start) {
                response.setBairro(jsonResponse.substring(start, end));
            }
        }

        if (jsonResponse.contains("\"localidade\"")) {
            int start = jsonResponse.indexOf("\"localidade\":\"") + 14;
            int end = jsonResponse.indexOf("\"", start);
            if (start > 13 && end > start) {
                response.setLocalidade(jsonResponse.substring(start, end));
            }
        }

        if (jsonResponse.contains("\"uf\"")) {
            int start = jsonResponse.indexOf("\"uf\":\"") + 6;
            int end = jsonResponse.indexOf("\"", start);
            if (start > 5 && end > start) {
                response.setUf(jsonResponse.substring(start, end));
            }
        }

        return response;
    }
}