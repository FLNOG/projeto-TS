package com.testesoftware.cadastro;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest
@Testcontainers
@ActiveProfiles("docker")
/*
Foi Criado um Properties do proprio doker para test container
ate ser definido melhor com o Time.
 */
public abstract class BaseIntegrationTest {

    private static final String POSTGRES_IMAGE = "postgres:16-alpine";
    private static final String POSTGRES_DATABASE = "testdb";
    private static final String POSTGRES_USERNAME = "test";
    private static final String POSTGRES_PASSWORD = "test";

    @Container
    @SuppressWarnings("resource") // Testcontainers gerencia o ciclo de vida automaticamente
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer< >("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(false); 
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }


}

