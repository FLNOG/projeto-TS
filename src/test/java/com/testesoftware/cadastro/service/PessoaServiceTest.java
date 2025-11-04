package com.testesoftware.cadastro.service;

import com.testesoftware.cadastro.exception.EmailAlreadyExistsException;
import com.testesoftware.cadastro.model.Pessoa;
import com.testesoftware.cadastro.repository.PessoaRepository;
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
@DisplayName("Testes Unitários - PessoaService")
class PessoaServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;

    private PessoaService pessoaService;
    private Pessoa pessoa;

    @BeforeEach
    void setUp() {
        pessoaService = new PessoaService(pessoaRepository);
        pessoa = new Pessoa("João Silva", "joao@email.com", "senha123");
    }

    @Test
    @DisplayName("Deve cadastrar pessoa com sucesso")
    void deveCadastrarPessoaComSucesso() {
        // Arrange
        when(pessoaRepository.existsByEmail(pessoa.getEmail())).thenReturn(false);
        when(pessoaRepository.save(any(Pessoa.class))).thenAnswer(invocation -> {
            Pessoa p = invocation.getArgument(0);
            p.setId(1); // Simula ID gerado pelo banco
            return p;
        });

        // Act
        Pessoa pessoaSalva = pessoaService.cadastraPessoa(pessoa);

        // Assert
        assertNotNull(pessoaSalva);
        assertEquals("João Silva", pessoaSalva.getNome());
        assertEquals("joao@email.com", pessoaSalva.getEmail());
        assertEquals(1, pessoaSalva.getId());
        verify(pessoaRepository, times(1)).existsByEmail(pessoa.getEmail());
        verify(pessoaRepository, times(1)).save(any(Pessoa.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // Arrange
        when(pessoaRepository.existsByEmail(pessoa.getEmail())).thenReturn(true);

        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            pessoaService.cadastraPessoa(pessoa);
        });

        assertEquals("E-mail já cadastrado: joao@email.com", exception.getMessage());
        verify(pessoaRepository, times(1)).existsByEmail(pessoa.getEmail());
        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }

    @Test
    @DisplayName("Deve listar todas as pessoas")
    void deveListarTodasAsPessoas() {
        // Arrange
        List<Pessoa> pessoas = Arrays.asList(
            new Pessoa("Maria", "maria@email.com", "senha1"),
            new Pessoa("Pedro", "pedro@email.com", "senha2")
        );
        when(pessoaRepository.findAll()).thenReturn(pessoas);

        // Act
        List<Pessoa> resultado = pessoaService.listarPessoas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Maria", resultado.get(0).getNome());
        assertEquals("Pedro", resultado.get(1).getNome());
        verify(pessoaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar pessoa por ID quando existe")
    void deveBuscarPessoaPorIdQuandoExiste() {
        // Arrange
        pessoa.setId(1);
        when(pessoaRepository.findById(1)).thenReturn(Optional.of(pessoa));

        // Act
        Optional<Pessoa> resultado = pessoaService.buscarPorId(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("João Silva", resultado.get().getNome());
        assertEquals("joao@email.com", resultado.get().getEmail());
        verify(pessoaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando ID não existe")
    void deveRetornarOptionalVazioQuandoIdNaoExiste() {
        // Arrange
        when(pessoaRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Pessoa> resultado = pessoaService.buscarPorId(999);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(pessoaRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Deve atualizar pessoa com sucesso")
    void deveAtualizarPessoaComSucesso() {
        // Arrange
        pessoa.setId(1);
        pessoa.setNome("João Silva Atualizado");
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

        // Act
        Pessoa pessoaAtualizada = pessoaService.atualizarPessoa(pessoa);

        // Assert
        assertNotNull(pessoaAtualizada);
        assertEquals("João Silva Atualizado", pessoaAtualizada.getNome());
        assertEquals(1, pessoaAtualizada.getId());
        verify(pessoaRepository, times(1)).save(pessoa);
    }

    @Test
    @DisplayName("Deve deletar pessoa com sucesso")
    void deveDeletarPessoaComSucesso() {
        // Arrange
        doNothing().when(pessoaRepository).deleteById(1);

        // Act
        assertDoesNotThrow(() -> {
            pessoaService.deletarPessoa(1);
        });

        // Assert
        verify(pessoaRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Deve cadastrar pessoa com CEP")
    void deveCadastrarPessoaComCep() {
        // Arrange
        Pessoa pessoaComCep = new Pessoa("Ana", "ana@email.com", "senha", "01001000");
        when(pessoaRepository.existsByEmail(pessoaComCep.getEmail())).thenReturn(false);
        when(pessoaRepository.save(any(Pessoa.class))).thenAnswer(invocation -> {
            Pessoa p = invocation.getArgument(0);
            p.setId(2);
            return p;
        });

        // Act
        Pessoa pessoaSalva = pessoaService.cadastraPessoa(pessoaComCep);

        // Assert
        assertNotNull(pessoaSalva);
        assertEquals("Ana", pessoaSalva.getNome());
        assertEquals("01001000", pessoaSalva.getCep());
        assertEquals(2, pessoaSalva.getId());
        verify(pessoaRepository, times(1)).existsByEmail(pessoaComCep.getEmail());
        verify(pessoaRepository, times(1)).save(any(Pessoa.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há pessoas")
    void deveRetornarListaVaziaQuandoNaoHaPessoas() {
        // Arrange
        when(pessoaRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Pessoa> resultado = pessoaService.listarPessoas();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(pessoaRepository, times(1)).findAll();
    }
}


