package com.testesoftware.cadastro.service;

import com.testesoftware.cadastro.BaseIntegrationTest;
import com.testesoftware.cadastro.model.Produto;
import com.testesoftware.cadastro.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Integração - ProdutoService com Testcontainers")
class ProdutoServiceTest extends BaseIntegrationTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoService produtoService;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produtoRepository.deleteAll(); 
        produto = new Produto("Notebook Dell", 2500.00);
    }

    @Test
    @DisplayName("Deve cadastrar produto com sucesso no banco de dados")
    @Transactional
    void deveCadastrarProdutoComSucesso() {
        // Act
        Produto produtoSalvo = produtoService.cadastrarProduto(produto);

        // Assert
        assertNotNull(produtoSalvo);
        assertTrue(produtoSalvo.getId() > 0);
        assertEquals("Notebook Dell", produtoSalvo.getNome());
        assertEquals(2500.00, produtoSalvo.getPreco(), 0.01);

        // Verifica se foi persistido no banco
        Optional<Produto> produtoNoBanco = produtoRepository.findById(produtoSalvo.getId());
        assertTrue(produtoNoBanco.isPresent());
        assertEquals("Notebook Dell", produtoNoBanco.get().getNome());
    }

    @Test
    @DisplayName("Deve listar todos os produtos do banco de dados")
    @Transactional
    void deveListarTodosOsProdutos() {
        // Arrange
        Produto produto1 = new Produto("Mouse", 50.00);
        Produto produto2 = new Produto("Teclado", 150.00);
        produtoService.cadastrarProduto(produto1);
        produtoService.cadastrarProduto(produto2);

        // Act
        List<Produto> resultado = produtoService.listarProdutos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(p -> p.getNome().equals("Mouse")));
        assertTrue(resultado.stream().anyMatch(p -> p.getNome().equals("Teclado")));
    }

    @Test
    @DisplayName("Deve buscar produto por ID quando existe no banco de dados")
    @Transactional
    void deveBuscarProdutoPorIdQuandoExiste() {
        // Arrange
        Produto produtoSalvo = produtoService.cadastrarProduto(produto);
        int id = produtoSalvo.getId();

        // Act
        Optional<Produto> resultado = produtoService.buscarPorId(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Notebook Dell", resultado.get().getNome());
        assertEquals(2500.00, resultado.get().getPreco(), 0.01);
        assertEquals(id, resultado.get().getId());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando ID não existe no banco")
    @Transactional
    void deveRetornarOptionalVazioQuandoIdNaoExiste() {
        // Act
        Optional<Produto> resultado = produtoService.buscarPorId(99999);

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar produto no banco de dados")
    @Transactional
    void deveAtualizarProdutoComSucesso() {
        // Arrange
        Produto produtoSalvo = produtoService.cadastrarProduto(produto);
        produtoSalvo.setNome("Notebook Dell Atualizado");
        produtoSalvo.setPreco(2300.00);

        // Act
        Produto produtoAtualizado = produtoService.atualizarProduto(produtoSalvo);

        // Assert
        assertNotNull(produtoAtualizado);
        assertEquals(produtoSalvo.getId(), produtoAtualizado.getId());
        assertEquals("Notebook Dell Atualizado", produtoAtualizado.getNome());
        assertEquals(2300.00, produtoAtualizado.getPreco(), 0.01);

        // Verifica no banco
        Optional<Produto> produtoNoBanco = produtoRepository.findById(produtoAtualizado.getId());
        assertTrue(produtoNoBanco.isPresent());
        assertEquals("Notebook Dell Atualizado", produtoNoBanco.get().getNome());
        assertEquals(2300.00, produtoNoBanco.get().getPreco(), 0.01);
    }

    @Test
    @DisplayName("Deve deletar produto do banco de dados")
    @Transactional
    void deveDeletarProdutoComSucesso() {
        // Arrange
        Produto produtoSalvo = produtoService.cadastrarProduto(produto);
        int id = produtoSalvo.getId();

        // Act
        assertDoesNotThrow(() -> {
            produtoService.deletarProduto(id);
        });

        // Assert
        Optional<Produto> produtoDeletado = produtoRepository.findById(id);
        assertTrue(produtoDeletado.isEmpty());
        assertEquals(0, produtoRepository.count());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há produtos no banco")
    @Transactional
    void deveRetornarListaVaziaQuandoNaoHaProdutos() {
        // Act
        List<Produto> resultado = produtoService.listarProdutos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        assertEquals(0, produtoRepository.count());
    }

    @Test
    @DisplayName("Deve atualizar nome do produto no banco de dados")
    @Transactional
    void deveAtualizarNomeDoProduto() {
        // Arrange
        Produto produtoSalvo = produtoService.cadastrarProduto(produto);
        produtoSalvo.setNome("Notebook Dell Pro");

        // Act
        Produto produtoAtualizado = produtoService.atualizarProduto(produtoSalvo);

        // Assert
        assertEquals("Notebook Dell Pro", produtoAtualizado.getNome());
        assertEquals(2500.00, produtoAtualizado.getPreco(), 0.01);
        assertEquals(produtoSalvo.getId(), produtoAtualizado.getId());

        // Verifica no banco
        Optional<Produto> produtoNoBanco = produtoRepository.findById(produtoAtualizado.getId());
        assertTrue(produtoNoBanco.isPresent());
        assertEquals("Notebook Dell Pro", produtoNoBanco.get().getNome());
    }

    @Test
    @DisplayName("Deve cadastrar produto com preço zero no banco de dados")
    @Transactional
    void deveCadastrarProdutoComPrecoZero() {
        // Arrange
        Produto produtoGratis = new Produto("Amostra Grátis", 0.00);

        // Act
        Produto produtoSalvo = produtoService.cadastrarProduto(produtoGratis);

        // Assert
        assertNotNull(produtoSalvo.getId());
        assertEquals(0.00, produtoSalvo.getPreco(), 0.01);

        // Verifica no banco
        Optional<Produto> produtoNoBanco = produtoRepository.findById(produtoSalvo.getId());
        assertTrue(produtoNoBanco.isPresent());
        assertEquals(0.00, produtoNoBanco.get().getPreco(), 0.01);
    }
}
