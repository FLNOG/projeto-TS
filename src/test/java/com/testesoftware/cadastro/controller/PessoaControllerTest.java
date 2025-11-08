package com.testesoftware.cadastro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testesoftware.cadastro.model.Pessoa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PessoaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Pessoa novaPessoa(String nome, String email, String cep) {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(nome);
        pessoa.setEmail(email);
        pessoa.setCep(cep);
        return pessoa;
    }

    @Test
    @DisplayName("POST /api/pessoas/cadastrar - deve cadastrar pessoa com sucesso")
    void cadastrarPessoa_sucesso() throws Exception {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Maria Oliveira");
        pessoa.setEmail("maria@example.com");
        pessoa.setSenha("123456");
        pessoa.setCep("01001000");

        mockMvc.perform(post("/api/pessoas/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pessoa)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria Oliveira"))
                .andExpect(jsonPath("$.email").value("maria@example.com"))
                .andExpect(jsonPath("$.logradouro").exists());
    }

    @Test
    @DisplayName("POST /api/pessoas/cadastrar - deve retornar erro se email já existir")
    void cadastrarPessoa_emailDuplicado() throws Exception {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setEmail("joao@example.com");
        pessoa.setSenha("123456");
        pessoa.setCep("01001000");

        mockMvc.perform(post("/api/pessoas/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pessoa)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/pessoas/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pessoa)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /api/pessoas/listar - deve retornar lista de pessoas")
    void listarPessoas() throws Exception {
        mockMvc.perform(get("/api/pessoas/listar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

}
