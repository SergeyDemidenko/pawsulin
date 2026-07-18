Feature: Pet management

  Scenario: Create a pet for an authenticated owner
    Given an authenticated pet owner
    And a valid pet creation request
    And pet service returns the created pet
    When the client creates a pet
    Then the response status should be 201
    And the pet response should contain name "Fluffy"
