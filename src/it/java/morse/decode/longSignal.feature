Feature: Create promocode

Background:
  * url 'http://localhost:8080/morse'
  * def morseMessage = read('mocks/longMessage.txt')
  * def morseSignal = read('mocks/longSignal.json')

@ignore
Scenario:
  Given path '/decode'
  And request morseSignal
  When method POST
  Then status 200
  And match response == morseMessage.Replace(Environment.NewLine, " ")
