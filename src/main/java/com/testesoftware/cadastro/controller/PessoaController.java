package com.testesoftware.cadastro.controller;

import com.testesoftware.cadastro.exception.EmailAlreadyExistsException;
import com.testesoftware.cadastro.model.Pessoa;
import com.testesoftware.cadastro.model.ViaCEP;
import com.testesoftware.cadastro.service.PessoaService;
import com.testesoftware.cadastro.service.ViaCEPService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pessoas")
public class PessoaController {

    private final PessoaService pessoaService;
    private final ViaCEPService viaCEPService;

    public PessoaController(PessoaService pessoaService, ViaCEPService viaCEPService) {
        this.pessoaService = pessoaService;
        this.viaCEPService = viaCEPService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Pessoa> cadastrarPessoa(@RequestBody Pessoa pessoa) {
        try {
            ViaCEP endereco = viaCEPService.buscarEnderecoPorCEP(pessoa.getCep());

            pessoa.setLogradouro(endereco.getLogradouro());
            pessoa.setBairro(endereco.getBairro());
            pessoa.setLocalidade(endereco.getLocalidade());
            pessoa.setUf(endereco.getUf());

            Pessoa novaPessoa = pessoaService.cadastraPessoa(pessoa);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaPessoa);

        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Pessoa>> listarPessoas() {
        return ResponseEntity.ok(pessoaService.listarPessoas());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Pessoa> buscarPorId(@PathVariable int id) {
        Optional<Pessoa> pessoa = pessoaService.buscarPorId(id);
        return pessoa.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Pessoa> atualizarPessoa(@PathVariable int id, @RequestBody Pessoa pessoa) {
        pessoa.setId(id);

        if (pessoa.getCep() != null && !pessoa.getCep().isBlank()) {
            try {
                ViaCEP endereco = viaCEPService.buscarEnderecoPorCEP(pessoa.getCep());
                pessoa.setLogradouro(endereco.getLogradouro());
                pessoa.setBairro(endereco.getBairro());
                pessoa.setLocalidade(endereco.getLocalidade());
                pessoa.setUf(endereco.getUf());
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }
        }

        Pessoa pessoaAtualizada = pessoaService.atualizarPessoa(pessoa);
        return ResponseEntity.ok(pessoaAtualizada);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarPessoa(@PathVariable int id) {
        pessoaService.deletarPessoa(id);
        return ResponseEntity.noContent().build();
    }
}