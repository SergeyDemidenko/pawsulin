Feature: Authentication

  Scenario: Register a new user
    Given a valid registration request
    And auth service returns a registered user
    When the client registers
    Then the response status should be 201
    And the auth response should contain user email "test@example.com"

  Scenario: Login with valid credentials
    Given a valid login request
    And auth service returns auth tokens
    When the client logs in
    Then the response status should be 200
    And the login response should contain access token "accessToken"
