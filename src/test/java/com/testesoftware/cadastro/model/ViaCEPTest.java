package com.testesoftware.cadastro.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ViaCEPTest {

    @Test
    void testGettersAndSetters() {

        ViaCEP viaCEP = new ViaCEP();

        viaCEP.setCep("07124-610");
        viaCEP.setLogradouro("Rua Andorinha");
        viaCEP.setComplemento("Casa");
        viaCEP.setBairro("Jardim Valéria");
        viaCEP.setLocalidade("Guarulhos");
        viaCEP.setUf("SP");
        viaCEP.setIbge("3518800");
        viaCEP.setGia("3360");
        viaCEP.setDdd("11");
        viaCEP.setSiafi("6477");

        assertEquals("07124-610", viaCEP.getCep());
        assertEquals("Rua Andorinha", viaCEP.getLogradouro());
        assertEquals("Casa", viaCEP.getComplemento());
        assertEquals("Jardim Valéria", viaCEP.getBairro());
        assertEquals("Guarulhos", viaCEP.getLocalidade());
        assertEquals("SP", viaCEP.getUf());
        assertEquals("3518800", viaCEP.getIbge());
        assertEquals("3360", viaCEP.getGia());
        assertEquals("11", viaCEP.getDdd());
        assertEquals("6477", viaCEP.getSiafi());
    }
}
