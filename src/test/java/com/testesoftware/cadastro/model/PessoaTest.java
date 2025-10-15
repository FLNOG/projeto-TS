package com.testesoftware.cadastro.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PessoaTest {

    @Test
    public void testNewPessoa(){
        Pessoa pessoa = new Pessoa("Felipe Nogueira", "felipe@email.com","1234567890");
        assertEquals("Felipe Nogueira", pessoa.getNome());
        assertEquals("felipe@email.com", pessoa.getEmail());
        assertEquals("1234567890", pessoa.getSenha());
    }
}