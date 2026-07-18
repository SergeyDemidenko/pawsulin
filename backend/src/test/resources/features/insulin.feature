Feature: Insulin logging

  Scenario: Create an insulin log for a pet
    Given an authenticated pet owner
    And a valid insulin log request for pet 1
    And insulin service returns the created log
    When the client creates an insulin log for pet 1
    Then the response status should be 201
    And the insulin response should contain type "Lantus"
