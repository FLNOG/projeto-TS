Feature: Person Registration
  As a registration manager
  I want to register valid people
  So that the system remains consistent

  Scenario: Register a person with valid data
    Given there is a person with the following data:
      | name  | Maria Silva           |
      | email | maria.silva@empresa.com |
      | password | senhaSegura123 |
      | cep   | 01001-000           |
    When I request the registration of this person
    Then the registration should be completed with status 201
    And the response should contain the name "Maria Silva"

  Scenario: Reject registration of a person with a duplicate email
    Given a person is already registered with the email "ana.souza@empresa.com"
    And I try to register a new person with the following data:
      | name  | Ana Souza                 |
      | email | ana.souza@empresa.com    |
      | password | outraSenhaSegura456   |
      | cep   | 01310-100                |
    When I submit the duplicate registration request
    Then the system should respond with status 409
    And the response should indicate that the email is already in use

  Scenario: Reject registration of a person with an invalid CEP
    Given there is a new person with the following data:
      | name  | Bruno Lima             |
      | email | bruno.lima@empresa.com |
      | password | senha123            |
      | cep   | 123                    |
    When I request the registration of this person with an invalid CEP
    Then the system should return status 400
    And the response should indicate that the provided CEP is invalid

