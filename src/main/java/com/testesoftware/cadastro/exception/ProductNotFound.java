package com.testesoftware.cadastro.exception;

public class ProductNotFound extends RuntimeException {
    public ProductNotFound(int id) {
        super("Produto: não encontrado com id: " + id);
    }
}