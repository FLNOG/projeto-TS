package com.testesoftware.cadastro.repository;

import com.testesoftware.cadastro.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PessoaRepository extends JpaRepository<com.testesoftware.cadastro.model.Pessoa, Integer> {
    boolean existsByEmail(String email);
}