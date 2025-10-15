package com.testesoftware.cadastro.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProdutoTest {

    @Test
    public void testNewProduto() {
        Produto produto = new Produto("Guitarra Yamaha", 1599.99);
        assertEquals("Guitarra Yamaha", produto.getNome());
        assertEquals(1599.99, produto.getPreco());
    }
}