# Checklist – Projeto Cadastro de Usuários e Produtos

### To Do
- [x] Testes unitários PessoaService (`JUnit 5 + Mockito`)
- [x] Testes unitários ProdutoService (`JUnit 5 + Mockito`)
- [ ] Testes de integração (Testcontainers com PostgreSQL)
- [ ] Testes de API REST (MockMvc ou RestAssured)
- [ ] Testes de integração com API externa (VHS / VCR Java)
- [ ] Configuração GitHub Actions:
    - [ ] Build Maven (`mvn clean verify`)
    - [ ] Execução de testes unitários e integração
    - [ ] Relatório de cobertura (JaCoCo)
    - [ ] Gatilho em push e pull request
- [ ] Documentação UML:
    - [ ] Diagrama de classes
    - [ ] Diagrama de sequência do fluxo de cadastro

### In Progress
- [ ] Integração com API externa (consulta CEP) — planejamento

### Done
- [x] Projeto inicializado (Spring Boot 3, Java 21, Maven)
- [x] Pacotes criados: `model`, `repository`, `service`, `controller`, `exception`
- [x] Models criadas (`Pessoa`, `Produto`) com Lombok
- [x] Repositórios criados (`PessoaRepository`, `ProdutoRepository`)
- [x] Services implementados (`PessoaService`, `ProdutoService`)
- [x] Controllers implementados (`PessoaController`, `ProdutoController`)
- [x] Exception `EmailAlreadyExistsException` criada e integrada
- [x] Tabelas criadas no Supabase: `pessoas`, `produtos`
- [x] `application.properties` configurado para conexão PostgreSQL
- [x] README com setup e comandos de build/execução
- [x] Commits seguindo Conventional Commits
