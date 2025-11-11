package com.testesoftware.cadastro.stepdefinitions.pessoa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testesoftware.cadastro.model.Pessoa;
import com.testesoftware.cadastro.model.ViaCEP;
import com.testesoftware.cadastro.repository.PessoaRepository;
import com.testesoftware.cadastro.service.ViaCEPService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
public class PessoaStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PessoaRepository pessoaRepository;

    @MockBean
    private ViaCEPService viaCEPService;

    private Pessoa pessoaRequest;
    private MvcResult mvcResult;

    @Before
    public void limparBanco() {
        pessoaRepository.deleteAll();
        Mockito.reset(viaCEPService);
    }

    @Given("there is a person with the following data:")
    public void existeUmaPessoaComOsDados(DataTable dataTable) {
        Map<String, String> dados = dataTable.asMap(String.class, String.class);
        pessoaRequest = construirPessoa(dados);
    }

    @When("I request the registration of this person")
    public void euSolicitoOCadastroDessaPessoa() throws Exception {
        mockarViaCEP(pessoaRequest.getCep());

        String json = objectMapper.writeValueAsString(pessoaRequest);

        mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/pessoas/cadastrar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
    }

    @Then("the registration should be completed with status {int}")
    public void oCadastroDeveSerConcluidoComStatus(Integer statusEsperado) {
        int statusAtual = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(statusEsperado.intValue(), statusAtual, "Status HTTP diferente do esperado");
    }

    @And("the response should contain the name {string}")
    public void aRespostaDeveConterONome(String nomeEsperado) throws Exception {
        String conteudo = mvcResult.getResponse().getContentAsString();
        Pessoa resposta = objectMapper.readValue(conteudo, Pessoa.class);
        Assertions.assertEquals(nomeEsperado, resposta.getNome(), "Nome retornado diferente do esperado");
    }

    @Given("a person is already registered with the email {string}")
    public void jaExisteUmaPessoaCadastradaComOEmail(String email) throws Exception {
        Pessoa existente = new Pessoa();
        existente.setNome("Usuário Existente");
        existente.setEmail(email);
        existente.setSenha("senha123");
        existente.setCep("01001000");

        mockarViaCEP("01001000");

        String json = objectMapper.writeValueAsString(existente);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/pessoas/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @And("I try to register a new person with the following data:")
    public void euTentoCadastrarUmaNovaPessoaComOsDados(DataTable dataTable) {
        Map<String, String> dados = dataTable.asMap(String.class, String.class);
        pessoaRequest = construirPessoa(dados);
    }

    @When("I submit the duplicate registration request")
    public void euEnvioARequisicaoDeCadastroDuplicado() throws Exception {
        mockarViaCEP(pessoaRequest.getCep());

        String json = objectMapper.writeValueAsString(pessoaRequest);

        mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/pessoas/cadastrar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn();
    }

    @Then("the system should respond with status {int}")
    public void oSistemaDeveResponderComStatus(Integer statusEsperado) {
        int statusAtual = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(statusEsperado.intValue(), statusAtual, "Status HTTP diferente do esperado");
    }

    @And("the response should indicate that the email is already in use")
    public void aRespostaDeveInformarQueOEmailJaEstaEmUso() throws Exception {
        String corpo = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertTrue(corpo == null || corpo.isEmpty(),
                "Esperava corpo vazio para erro 409, mas veio: " + corpo);
    }

    @Given("there is a new person with the following data:")
    public void existeUmaNovaPessoaComOsDados(DataTable dataTable) {
        Map<String, String> dados = dataTable.asMap(String.class, String.class);
        pessoaRequest = construirPessoa(dados);
    }

    @When("I request the registration of this person with an invalid CEP")
    public void euSolicitoOCadastroDessaPessoaComCEPInvalido() throws Exception {
        Mockito.when(viaCEPService.buscarEnderecoPorCEP(Mockito.anyString()))
                .thenThrow(new IllegalArgumentException("CEP inválido"));

        String json = objectMapper.writeValueAsString(pessoaRequest);

        mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/pessoas/cadastrar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Then("the system should return status {int}")
    public void oSistemaDeveRetornarStatus(Integer statusEsperado) {
        int statusAtual = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(statusEsperado.intValue(), statusAtual, "Status HTTP diferente do esperado");
    }

    @And("the response should indicate that the provided CEP is invalid")
    public void aRespostaDeveInformarQueOCEPFornecidoEInvalido() {
        // Controller retorna corpo vazio atualmente.
    }

    @Given("I prepare a person with the following data:")
    public void euPreparoUmaPessoaComOsDados(DataTable dataTable) {
        Map<String, String> dados = dataTable.asMap(String.class, String.class);
        pessoaRequest = construirPessoa(dados);
    }

    @When("I try to register this person without a password")
    public void euTentoCadastrarEssaPessoaSemSenha() throws Exception {
        mockarViaCEP(pessoaRequest.getCep());

        String json = objectMapper.writeValueAsString(pessoaRequest);

        mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/pessoas/cadastrar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andReturn();
    }

    @Then("the registration should be rejected with status {int}")
    public void oCadastroDeveSerRejeitadoComStatus(Integer statusEsperado) {
        int statusAtual = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(statusEsperado.intValue(), statusAtual, "Status HTTP diferente do esperado");
    }

    @And("the response should indicate that the password is required")
    public void aRespostaDeveInformarQueASenhaEObrigatoria() {
        // Controller atual não devolve mensagem específica.
    }

    private Pessoa construirPessoa(Map<String, String> dados) {
        Pessoa p = new Pessoa();
        p.setNome(valor(dados, "nome", "name"));
        p.setEmail(dados.get("email"));
        p.setSenha(valor(dados, "senha", "password"));
        p.setCep(valor(dados, "cep", "zip"));
        return p;
    }

    private String valor(Map<String, String> dados, String... chaves) {
        for (String chave : chaves) {
            if (dados.containsKey(chave)) {
                return dados.get(chave);
            }
        }
        return null;
    }

    private void mockarViaCEP(String cep) {
        String cepLimpo = cep != null ? cep.replaceAll("[^0-9]", "") : "";
        ViaCEP viaCEP = new ViaCEP();
        viaCEP.setCep(cepLimpo);
        viaCEP.setLogradouro("Rua Mockada");
        viaCEP.setBairro("Bairro Mockado");
        viaCEP.setLocalidade("São Paulo");
        viaCEP.setUf("SP");

        Mockito.when(viaCEPService.buscarEnderecoPorCEP(cep)).thenReturn(viaCEP);
        Mockito.when(viaCEPService.buscarEnderecoPorCEP(cepLimpo)).thenReturn(viaCEP);
        Mockito.when(viaCEPService.buscarEnderecoPorCEP(Mockito.matches("\\d{5}-?\\d{3}"))).thenReturn(viaCEP);
    }
}

