package com.testesoftware.cadastro.service;

import com.testesoftware.cadastro.model.Produto;
import com.testesoftware.cadastro.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ProdutoService")
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    private ProdutoService produtoService;
    private Produto produto;

    @BeforeEach
    void setUp() {
        produtoService = new ProdutoService(produtoRepository);
        produto = new Produto("Notebook Dell", 2500.00);
    }

    @Test
    @DisplayName("Deve cadastrar produto com sucesso")
    void deveCadastrarProdutoComSucesso() {
        // Arrange
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0);
            p.setId(1); // Simula ID gerado pelo banco
            return p;
        });

        // Act
        Produto produtoSalvo = produtoService.cadastrarProduto(produto);

        // Assert
        assertNotNull(produtoSalvo);
        assertEquals("Notebook Dell", produtoSalvo.getNome());
        assertEquals(2500.00, produtoSalvo.getPreco());
        assertEquals(1, produtoSalvo.getId());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    @DisplayName("Deve listar todos os produtos")
    void deveListarTodosOsProdutos() {
        // Arrange
        List<Produto> produtos = Arrays.asList(
            new Produto("Mouse", 50.00),
            new Produto("Teclado", 150.00)
        );
        when(produtoRepository.findAll()).thenReturn(produtos);

        // Act
        List<Produto> resultado = produtoService.listarProdutos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Mouse", resultado.get(0).getNome());
        assertEquals("Teclado", resultado.get(1).getNome());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar produto por ID quando existe")
    void deveBuscarProdutoPorIdQuandoExiste() {
        // Arrange
        produto.setId(1);
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));

        // Act
        Optional<Produto> resultado = produtoService.buscarPorId(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Notebook Dell", resultado.get().getNome());
        assertEquals(2500.00, resultado.get().getPreco());
        assertEquals(1, resultado.get().getId());
        verify(produtoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando ID não existe")
    void deveRetornarOptionalVazioQuandoIdNaoExiste() {
        // Arrange
        when(produtoRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Produto> resultado = produtoService.buscarPorId(999);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(produtoRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() {
        // Arrange
        produto.setId(1);
        produto.setPreco(2300.00);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        // Act
        Produto produtoAtualizado = produtoService.atualizarProduto(produto);

        // Assert
        assertNotNull(produtoAtualizado);
        assertEquals(2300.00, produtoAtualizado.getPreco());
        assertEquals(1, produtoAtualizado.getId());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void deveDeletarProdutoComSucesso() {
        // Arrange
        doNothing().when(produtoRepository).deleteById(1);

        // Act
        assertDoesNotThrow(() -> {
            produtoService.deletarProduto(1);
        });

        // Assert
        verify(produtoRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há produtos")
    void deveRetornarListaVaziaQuandoNaoHaProdutos() {
        // Arrange
        when(produtoRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Produto> resultado = produtoService.listarProdutos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar nome do produto")
    void deveAtualizarNomeDoProduto() {
        // Arrange
        produto.setId(1);
        produto.setNome("Notebook Dell Atualizado");
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        // Act
        Produto produtoAtualizado = produtoService.atualizarProduto(produto);

        // Assert
        assertEquals("Notebook Dell Atualizado", produtoAtualizado.getNome());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    @DisplayName("Deve cadastrar produto com preço zero")
    void deveCadastrarProdutoComPrecoZero() {
        // Arrange
        Produto produtoGratis = new Produto("Amostra Grátis", 0.00);
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0);
            p.setId(5);
            return p;
        });

        // Act
        Produto produtoSalvo = produtoService.cadastrarProduto(produtoGratis);

        // Assert
        assertEquals(0.00, produtoSalvo.getPreco());
        verify(produtoRepository, times(1)).save(produtoGratis);
    }
}
