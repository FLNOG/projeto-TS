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

    @Test
    public void testGetSet(){
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Felipe Nogueira");
        pessoa.setEmail("felipe@email.com");
        pessoa.setSenha("1234567890");

        assertEquals("Felipe Nogueira", pessoa.getNome());
        assertEquals("felipe@email.com",  pessoa.getEmail());
        assertEquals("1234567890", pessoa.getSenha());
    }

    @Test
    public void testNewPessoaWithCep(){
        Pessoa pessoa = new Pessoa("Felipe Nogueira", "felipe@email.com", "1234567890", "01001000");
        pessoa.setLogradouro("Praça da Sé");
        pessoa.setBairro("Sé");
        pessoa.setLocalidade("São Paulo");
        pessoa.setUf("SP");

        assertEquals("Felipe Nogueira", pessoa.getNome());
        assertEquals("felipe@email.com", pessoa.getEmail());
        assertEquals("1234567890", pessoa.getSenha());
        assertEquals("01001000", pessoa.getCep());
        assertEquals("Praça da Sé", pessoa.getLogradouro());
        assertEquals("Sé", pessoa.getBairro());
        assertEquals("São Paulo", pessoa.getLocalidade());
        assertEquals("SP", pessoa.getUf());
    }

    @Test
    public void testGetSetAllFields(){
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Felipe Nogueira");
        pessoa.setEmail("felipe@email.com");
        pessoa.setSenha("1234567890");
        pessoa.setCep("01001000");
        pessoa.setLogradouro("Praça da Sé");
        pessoa.setBairro("Sé");
        pessoa.setLocalidade("São Paulo");
        pessoa.setUf("SP");

        assertEquals("Felipe Nogueira", pessoa.getNome());
        assertEquals("felipe@email.com", pessoa.getEmail());
        assertEquals("1234567890", pessoa.getSenha());
        assertEquals("01001000", pessoa.getCep());
        assertEquals("Praça da Sé", pessoa.getLogradouro());
        assertEquals("Sé", pessoa.getBairro());
        assertEquals("São Paulo", pessoa.getLocalidade());
        assertEquals("SP", pessoa.getUf());
    }
}