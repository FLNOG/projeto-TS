package com.testesoftware.cadastro.stepdefinitions.produto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testesoftware.cadastro.model.Produto;
import com.testesoftware.cadastro.repository.ProdutoRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
public class ProdutoStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepository produtoRepository;

    private Produto produtoRequest;
    private Produto produtoPersistido;
    private MvcResult mvcResult;
    private List<Produto> listaResposta = new ArrayList<>();

    @Before
    public void limparRepositorio() {
        produtoRepository.deleteAll();
        produtoRequest = null;
        produtoPersistido = null;
        mvcResult = null;
        listaResposta.clear();
    }

    @Given("there is a product with the following data:")
    public void thereIsAProductWithTheFollowingData(DataTable dataTable) {
        Map<String, String> dados = dataTable.asMap(String.class, String.class);
        produtoRequest = construirProduto(dados);
    }

    @When("I request the registration of this product")
    public void iRequestTheRegistrationOfThisProduct() throws Exception {
        String json = objectMapper.writeValueAsString(produtoRequest);

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/produtos/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isCreated())
         .andReturn();
    }

    @Then("the product registration should return status {int}")
    public void theProductRegistrationShouldReturnStatus(Integer statusEsperado) {
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(statusEsperado);
    }

    @And("the response should contain the product name {string}")
    public void theResponseShouldContainTheProductName(String nomeEsperado) throws Exception {
        Produto resposta = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Produto.class);
        Assertions.assertThat(resposta.getNome()).isEqualTo(nomeEsperado);
    }

    @Given("the following products are already registered:")
    public void theFollowingProductsAreAlreadyRegistered(DataTable dataTable) {
        List<Map<String, String>> linhas = dataTable.asMaps(String.class, String.class);
        linhas.forEach(linha -> produtoRepository.save(construirProduto(linha)));
    }

    @When("I request the list of products")
    public void iRequestTheListOfProducts() throws Exception {
        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/produtos/listar")
        ).andExpect(MockMvcResultMatchers.status().isOk())
         .andReturn();

        String corpo = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        listaResposta = objectMapper.readValue(corpo, new TypeReference<List<Produto>>() {});
    }

    @Then("the response should return status {int}")
    public void theResponseShouldReturnStatus(Integer statusEsperado) {
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(statusEsperado);
    }

    @And("the response should contain {int} products")
    public void theResponseShouldContainProducts(Integer quantidadeEsperada) {
        Assertions.assertThat(listaResposta).hasSize(quantidadeEsperada);
    }

    @And("the list should include a product named {string}")
    public void theListShouldIncludeAProductNamed(String nomeEsperado) {
        Assertions.assertThat(listaResposta)
                .anyMatch(produto -> nomeEsperado.equals(produto.getNome()));
    }

    @Given("a product exists with the following data:")
    public void aProductExistsWithTheFollowingData(DataTable dataTable) {
        produtoPersistido = produtoRepository.save(construirProduto(dataTable.asMap(String.class, String.class)));
    }

    @When("I update the product price to {double}")
    public void iUpdateTheProductPriceTo(Double novoPreco) throws Exception {
        produtoPersistido.setPreco(novoPreco);
        String json = objectMapper.writeValueAsString(produtoPersistido);

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/produtos/atualizar/{id}", produtoPersistido.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isOk())
         .andReturn();
    }

    @Then("the update should return status {int}")
    public void theUpdateShouldReturnStatus(Integer statusEsperado) {
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(statusEsperado);
    }

    @And("the product price should be {double}")
    public void theProductPriceShouldBe(Double precoEsperado) {
        Optional<Produto> atualizado = produtoRepository.findById(produtoPersistido.getId());
        Assertions.assertThat(atualizado).isPresent();
        Assertions.assertThat(atualizado.get().getPreco()).isEqualTo(precoEsperado);
    }

    @Given("a product is available with the following data:")
    public void aProductIsAvailableWithTheFollowingData(DataTable dataTable) {
        produtoPersistido = produtoRepository.save(construirProduto(dataTable.asMap(String.class, String.class)));
    }

    @When("I delete this product")
    public void iDeleteThisProduct() throws Exception {
        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/produtos/deletar/{id}", produtoPersistido.getId())
        ).andExpect(MockMvcResultMatchers.status().isNoContent())
         .andReturn();
    }

    @Then("the deletion should return status {int}")
    public void theDeletionShouldReturnStatus(Integer statusEsperado) {
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(statusEsperado);
    }

    @And("the product should no longer exist in the repository")
    public void theProductShouldNoLongerExistInTheRepository() {
        Assertions.assertThat(produtoRepository.findById(produtoPersistido.getId())).isNotPresent();
    }

    private Produto construirProduto(Map<String, String> dados) {
        String nome = dados.getOrDefault("nome", dados.getOrDefault("name", null));
        String precoStr = dados.getOrDefault("preco", dados.getOrDefault("price", null));
        Double preco = null;
        if (precoStr != null && !precoStr.isBlank()) {
            preco = Double.valueOf(precoStr.replace(",", "."));
        }
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setPreco(preco);
        return produto;
    }
}

