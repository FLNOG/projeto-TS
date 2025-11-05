package com.testesoftware.cadastro.service;

import com.testesoftware.cadastro.BaseIntegrationTest;
import com.testesoftware.cadastro.exception.EmailAlreadyExistsException;
import com.testesoftware.cadastro.model.Pessoa;
import com.testesoftware.cadastro.repository.PessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Integração - PessoaService com Testcontainers")
class PessoaServiceTest extends BaseIntegrationTest {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PessoaService pessoaService;

    private Pessoa pessoa;

    @BeforeEach
    void setUp() {
        pessoaRepository.deleteAll(); // Limpa dados antes de cada teste
        pessoa = new Pessoa("João Silva", "joao@email.com", "senha123", "01001000");
    }

    @Test
    @DisplayName("Deve cadastrar pessoa com sucesso no banco de dados")
    @Transactional
    void deveCadastrarPessoaComSucesso() {
        // Act
        Pessoa pessoaSalva = pessoaService.cadastraPessoa(pessoa);

        // Assert
        assertNotNull(pessoaSalva);
        assertTrue(pessoaSalva.getId() > 0);
        assertEquals("João Silva", pessoaSalva.getNome());
        assertEquals("joao@email.com", pessoaSalva.getEmail());
        assertEquals("01001000", pessoaSalva.getCep());

        // Verifica se foi persistido no banco
        Optional<Pessoa> pessoaNoBanco = pessoaRepository.findById(pessoaSalva.getId());
        assertTrue(pessoaNoBanco.isPresent());
        assertEquals("João Silva", pessoaNoBanco.get().getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe no banco de dados")
    @Transactional
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // Arrange - Salva primeira pessoa
        pessoaService.cadastraPessoa(pessoa);
        Pessoa pessoaComEmailDuplicado = new Pessoa("Outro Nome", "joao@email.com", "senha789", "02020000");

        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            pessoaService.cadastraPessoa(pessoaComEmailDuplicado);
        });

        assertEquals("E-mail já cadastrado: joao@email.com", exception.getMessage());

        // Verifica que apenas uma pessoa foi salva
        assertEquals(1, pessoaRepository.count());
        List<Pessoa> pessoas = pessoaRepository.findAll();
        assertEquals(1, pessoas.size());
        assertEquals("joao@email.com", pessoas.get(0).getEmail());
    }

    @Test
    @DisplayName("Deve listar todas as pessoas do banco de dados")
    @Transactional
    void deveListarTodasAsPessoas() {
        // Arrange
        Pessoa pessoa1 = new Pessoa("Maria", "maria@email.com", "senha1", "01001000");
        Pessoa pessoa2 = new Pessoa("Pedro", "pedro@email.com", "senha2", "01310100");
        pessoaService.cadastraPessoa(pessoa1);
        pessoaService.cadastraPessoa(pessoa2);

        // Act
        List<Pessoa> resultado = pessoaService.listarPessoas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(p -> p.getEmail().equals("maria@email.com")));
        assertTrue(resultado.stream().anyMatch(p -> p.getEmail().equals("pedro@email.com")));
    }

    @Test
    @DisplayName("Deve buscar pessoa por ID quando existe no banco de dados")
    @Transactional
    void deveBuscarPessoaPorIdQuandoExiste() {
        // Arrange
        Pessoa pessoaSalva = pessoaService.cadastraPessoa(pessoa);
        int id = pessoaSalva.getId();

        // Act
        Optional<Pessoa> resultado = pessoaService.buscarPorId(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("João Silva", resultado.get().getNome());
        assertEquals("joao@email.com", resultado.get().getEmail());
        assertEquals(id, resultado.get().getId());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando ID não existe no banco")
    @Transactional
    void deveRetornarOptionalVazioQuandoIdNaoExiste() {
        // Act
        Optional<Pessoa> resultado = pessoaService.buscarPorId(99999);

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar pessoa no banco de dados")
    @Transactional
    void deveAtualizarPessoaComSucesso() {
        // Arrange
        Pessoa pessoaSalva = pessoaService.cadastraPessoa(pessoa);
        pessoaSalva.setNome("João Silva Atualizado");
        pessoaSalva.setCep("01310100");

        // Act
        Pessoa pessoaAtualizada = pessoaService.atualizarPessoa(pessoaSalva);

        // Assert
        assertNotNull(pessoaAtualizada);
        assertEquals(pessoaSalva.getId(), pessoaAtualizada.getId());
        assertEquals("João Silva Atualizado", pessoaAtualizada.getNome());
        assertEquals("01310100", pessoaAtualizada.getCep());

        // Verifica no banco
        Optional<Pessoa> pessoaNoBanco = pessoaRepository.findById(pessoaAtualizada.getId());
        assertTrue(pessoaNoBanco.isPresent());
        assertEquals("João Silva Atualizado", pessoaNoBanco.get().getNome());
    }

    @Test
    @DisplayName("Deve deletar pessoa do banco de dados")
    @Transactional
    void deveDeletarPessoaComSucesso() {
        // Arrange
        Pessoa pessoaSalva = pessoaService.cadastraPessoa(pessoa);
        int id = pessoaSalva.getId();

        // Act
        assertDoesNotThrow(() -> {
            pessoaService.deletarPessoa(id);
        });

        // Assert
        Optional<Pessoa> pessoaDeletada = pessoaRepository.findById(id);
        assertTrue(pessoaDeletada.isEmpty());
        assertEquals(0, pessoaRepository.count());
    }

    @Test
    @DisplayName("Deve cadastrar pessoa com CEP e validar persistência")
    @Transactional
    void deveCadastrarPessoaComCep() {
        // Arrange
        Pessoa pessoaComCep = new Pessoa("Ana", "ana@email.com", "senha", "01001000");
        pessoaComCep.setLogradouro("Praça da Sé");
        pessoaComCep.setBairro("Sé");
        pessoaComCep.setLocalidade("São Paulo");
        pessoaComCep.setUf("SP");

        // Act
        Pessoa pessoaSalva = pessoaService.cadastraPessoa(pessoaComCep);

        // Assert
        assertNotNull(pessoaSalva);
        assertTrue(pessoaSalva.getId() > 0);
        assertEquals("Ana", pessoaSalva.getNome());
        assertEquals("01001000", pessoaSalva.getCep());
        assertEquals("Praça da Sé", pessoaSalva.getLogradouro());
        assertEquals("São Paulo", pessoaSalva.getLocalidade());
        assertEquals("SP", pessoaSalva.getUf());

        // Verifica no banco
        Optional<Pessoa> pessoaNoBanco = pessoaRepository.findById(pessoaSalva.getId());
        assertTrue(pessoaNoBanco.isPresent());
        assertEquals("São Paulo", pessoaNoBanco.get().getLocalidade());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há pessoas no banco")
    @Transactional
    void deveRetornarListaVaziaQuandoNaoHaPessoas() {
        // Act
        List<Pessoa> resultado = pessoaService.listarPessoas();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        assertEquals(0, pessoaRepository.count());
    }
}
