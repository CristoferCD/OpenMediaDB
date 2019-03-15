Feature: Read show info

  Background:
    * url baseUrl
    * def login = call read('classpath:login.feature')
    * configure headers = { Authorization: '#(login.token)' }

  Scenario: List all shows
  Given path 'shows'
  When method GET
  Then status 200


#Scenario: Register a test user
#  Given url 'http://localhost:8080/signup'
#  And request { name: 'test', password: 'test' }
#  When method POST
#  Then status 200
