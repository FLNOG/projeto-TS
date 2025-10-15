package com.testesoftware.cadastro.controller;

import com.testesoftware.cadastro.exception.ProductNotFound;
import com.testesoftware.cadastro.model.Produto;
import com.testesoftware.cadastro.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Produto> cadastrarProduto(@RequestBody Produto produto) {
        Produto novoProduto = produtoService.cadastrarProduto(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Produto>> listarProdutos() {
        List<Produto> produtos = produtoService.listarProdutos();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable int id) {
        Optional<Produto> produto = produtoService.buscarPorId(id);
        return produto.map(ResponseEntity::ok)
                .orElseThrow(() -> new ProductNotFound(id));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable int id, @RequestBody Produto produto) {
        if (produtoService.buscarPorId(id).isEmpty()) {
            throw new ProductNotFound(id);
        }
        produto.setId(id);
        Produto produtoAtualizado = produtoService.atualizarProduto(produto);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable int id) {
        if (produtoService.buscarPorId(id).isEmpty()) {
            throw new ProductNotFound(id);
        }
        produtoService.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }
}