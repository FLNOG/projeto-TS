Feature: Product Management
  As an inventory manager
  I want to manage products
  So that the catalog stays accurate

  Scenario: Register a product with valid data
    Given there is a product with the following data:
      | name  | Notebook Gamer |
      | price | 4999.90         |
    When I request the registration of this product
    Then the product registration should return status 201
    And the response should contain the product name "Notebook Gamer"

  Scenario: List all registered products
    Given the following products are already registered:
      | name          | price   |
      | Monitor 27"   | 1899.00 |
      | Teclado Mec   | 399.90  |
    When I request the list of products
    Then the response should return status 200
    And the response should contain 2 products
    And the list should include a product named "Monitor 27\""

  Scenario: Update an existing product price
    Given a product exists with the following data:
      | name  | Headset Wireless |
      | price | 799.90           |
    When I update the product price to 649.90
    Then the update should return status 200
    And the product price should be 649.90

  Scenario: Delete an existing product
    Given a product is available with the following data:
      | name  | Mouse Gamer |
      | price | 299.90      |
    When I delete this product
    Then the deletion should return status 204
    And the product should no longer exist in the repository


