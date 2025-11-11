package com.testesoftware.cadastro.cucumber;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/produto.feature")
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "com.testesoftware.cadastro.stepdefinitions.produto"
)
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty, html:target/cucumber-reports/produto.html, json:target/cucumber-reports/produto.json"
)
public class ProdutoCucumberTest {
}

