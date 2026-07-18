Feature: Glucose tracking

  Scenario: Create a glucose reading for a pet
    Given an authenticated pet owner
    And a valid glucose reading request for pet 1
    And glucose service returns the created reading
    When the client creates a glucose reading for pet 1
    Then the response status should be 201
    And the glucose response should contain level "NORMAL"
