package com.testesoftware.cadastro.service;

import com.testesoftware.cadastro.model.Pessoa;
import com.testesoftware.cadastro.repository.PessoaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PessoaService {

    private final PessoaRepository pessoaRepository;

    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    public Pessoa criarPessoa(Pessoa pessoa) {
        if (pessoaRepository.existsByEmail(pessoa.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        return pessoaRepository.save(pessoa);
    }

    public List<Pessoa> listarUsuarios() {
        return pessoaRepository.findAll();
    }

    public Optional<Pessoa> buscarPorId(int id) {
        return pessoaRepository.findById(id);
    }

    public Pessoa atualizarUsuario(Pessoa pessoa) {
        return pessoaRepository.save(pessoa);
    }

    public void deletarUsuario(int id) {
        pessoaRepository.deleteById(id);
    }
}