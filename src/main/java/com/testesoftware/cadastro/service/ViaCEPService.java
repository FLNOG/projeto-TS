package com.testesoftware.cadastro.service;

import com.testesoftware.cadastro.model.ViaCEP;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ViaCEPService {

    private final RestTemplate restTemplate;

    public ViaCEPService() {
        this.restTemplate = new RestTemplate();
    }

    public ViaCEP buscarEnderecoPorCEP(String cep) {
        String cepFormatado = cep.replaceAll("[^0-9]", "");

        if (cepFormatado.length() != 8) {
            throw new IllegalArgumentException("CEP inválido: deve conter 8 dígitos");
        }

        String url = "https://viacep.com.br/ws/" + cepFormatado + "/json/";

        try {
            ViaCEP response = restTemplate.getForObject(url, ViaCEP.class);

            if (response != null) {
                response.setCep(cepFormatado);
            }

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar CEP na API ViaCEP: " + e.getMessage(), e);
        }
    }

    public ViaCEP viaCEPResponseError404(String cep) {
        String cepFormatado = cep.replaceAll("[^0-9]", "");

        if (cepFormatado.length() != 8) {
            throw new IllegalArgumentException("CEP inválido: deve conter 8 dígitos");
        }

        String url = "https://viacep.com.br/ws/" + cepFormatado + "/jso/";

        try {
            ViaCEP response = restTemplate.getForObject(url, ViaCEP.class);

            if (response != null) {
                response.setCep(cepFormatado);
            }

            return response;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CEP não encontrado");
        }
    }
}