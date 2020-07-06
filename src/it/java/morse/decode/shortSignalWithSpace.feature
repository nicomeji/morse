Feature: Parse word "soda stereo"

Background:
  * url 'http://localhost:8080/morse'
  * def morseSignal = read('mocks/shortSignalWithSpace.json')

@ignore
Scenario:
  Given path '/decode'
  And request morseSignal
  When method POST
  Then status 200
  And match response == "soda stereo"
