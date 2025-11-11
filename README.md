# 🧪 Projeto Testes de Software — Cadastro de Usuários e Produtos
Curso: Analise e Desenvolvimento de Sistemas - Turma: 4NC

* Intergrantes:
* Felipe Nogueira Silva
* Pedro Henrique Carneichuk Rosa 
* Ranielly Evellyn Cunha
* Stefany Caroline Ferreira Sampaio

---

Este projeto faz parte da disciplina **Testes de Software**, com foco em boas práticas, automação e integração contínua.

* Aplicação **backend em Java 17 com Spring Boot 3** para cadastro de usuários e produtos.
* Exposição de API REST completa para operações CRUD (Create, Read, Update, Delete).
* Persistência de dados em PostgreSQL.
* Integração com API externa de validação (por exemplo, consulta de CEP), com gravação e reprodução de respostas via VHS (VCR Java).
* Testes automatizados cobrindo fluxos principais, exceções e integrações.

---

## 📄 Documentação UML 

# Diagrama de classe
![Diagrama_classes](img/Diagrama_classes.jpeg)

# Diagrama de sequência do fluxo de cadastro
![Diagrama_sequencia](img/Diagrama_sequencia.png)

---

## 🚀 Tecnologias e Bibliotecas

- Java 21, Spring Boot 3, Spring Data JPA, Spring Web.
- PostgreSQL → produção e teste.
- Testcontainers → isolamento de ambiente de teste.
- VHS (VCR Java) → gravação e reprodução de chamadas externas.
- JUnit 5, AssertJ → testes automatizados.
- Lombok e MapStruct → simplificação de código e mapeamento.

---

## ⚙️ Configuração do Projeto
- JDK 17
- Maven

### 1️⃣ Clonar o repositório
```bash
git clone https://github.com/FLNOG/projeto-TS.git
cd projeto-TS
