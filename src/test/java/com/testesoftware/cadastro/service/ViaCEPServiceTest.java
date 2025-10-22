package com.testesoftware.cadastro.service;

import com.testesoftware.cadastro.model.ViaCEP;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=create-drop" })
class ViaCEPServiceTest {

    private ViaCEPService viaCEPService = new ViaCEPService();

    @Test
    void testBuscarEnderecoPorCEP() {
        String cepValido = "01001000";

        ViaCEP response = viaCEPService.buscarEnderecoPorCEP(cepValido);

        assertNotNull(response);
        assertEquals(cepValido, response.getCep());
        assertNotNull(response.getLogradouro());
        assertNotNull(response.getLocalidade());
        assertNotNull(response.getUf());
    }

    @Test
    void testBuscarEnderecoPorCEPInvalido() {
        String cepInvalido = "1234567";

        assertThrows(IllegalArgumentException.class, () -> {
            viaCEPService.buscarEnderecoPorCEP(cepInvalido);
        });
    }

    @Test
    void testBuscarEnderecoPorCEPComMascara() {
        String cepComMascara = "01001-000";

        ViaCEP response = viaCEPService.buscarEnderecoPorCEP(cepComMascara);

        assertNotNull(response);
        assertEquals("01001000", response.getCep());
        assertNotNull(response.getLogradouro());
        assertNotNull(response.getLocalidade());
        assertNotNull(response.getUf());
    }

    @Test
    void testErro404(){
        String cepValido = "07124610";

        assertThrows(ResponseStatusException.class, () -> {
            viaCEPService.viaCEPResponseError404(cepValido);
        });
    }
}