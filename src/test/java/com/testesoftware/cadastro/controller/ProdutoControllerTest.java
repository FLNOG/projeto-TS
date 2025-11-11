package com.testesoftware.cadastro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testesoftware.cadastro.model.Produto;
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
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Produto novoProduto(String nome, double preco) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setPreco(preco);
        return produto;
    }

    @Test
    @DisplayName("POST /api/produtos/cadastrar - deve cadastrar produto com sucesso")
    void cadastrarProduto_sucesso() throws Exception {
        Produto produto = novoProduto("Fralda Tena Confort", 29.90);

        mockMvc.perform(post("/api/produtos/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Fralda Tena Confort"))
                .andExpect(jsonPath("$.preco").value(29.90));
    }

    @Test
    @DisplayName("GET /api/produtos/listar - deve retornar lista de produtos")
    void listarProdutos() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/produtos/listar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/produtos/buscar/{id} - deve retornar produto existente")
    void buscarProduto_existente() throws Exception {
        Produto produto = novoProduto("Tena Pants", 59.90);
        String json = objectMapper.writeValueAsString(produto);

        String response = mockMvc.perform(post("/api/produtos/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Produto produtoSalvo = objectMapper.readValue(response, Produto.class);
        int id = produtoSalvo.getId();

        mockMvc.perform(get("/api/produtos/buscar/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Tena Pants"));
    }

    @Test
    @DisplayName("DELETE /api/produtos/deletar/{id} - deve deletar produto existente")
    void deletarProduto_existente() throws Exception {
        // Arrange
        Produto produto = novoProduto("Tena Slip", 39.90);
        String response = mockMvc.perform(post("/api/produtos/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Produto produtoSalvo = objectMapper.readValue(response, Produto.class);
        int id = produtoSalvo.getId();

        mockMvc.perform(delete("/api/produtos/deletar/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
