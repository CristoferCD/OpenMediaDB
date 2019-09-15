Feature: Log in and define token

  Background:
    * url baseUrl

  Scenario: Login
    Given path 'login'
    And request { name: 'Test', password: '1234' }
    When method POST
    Then status 200
    * def token = 'Bearer ' + response.token