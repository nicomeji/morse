Feature: Parse long phrase with spaces

Background:
  * url 'http://localhost:8080/morse'
  * def morseSignal = read('mocks/longSignal.json')
  * def readFile =
  """
  function(args) {
     var readFile = Java.type('morse.utils.ReadFileUtil');
     return readFile.read(args);
  }
  """

@ignore
Scenario:
  Given path '/decode'
  And request morseSignal
  When method POST
  Then status 200
  And match response == readFile('src/it/java/morse/decode/mocks/longMessage.txt')
