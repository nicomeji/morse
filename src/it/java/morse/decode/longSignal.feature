Feature: Parse long phrase with spaces

Background:
  * url 'http://localhost:8080/morse'
  * def readFile =
  """
  function(args) {
     var readFile = Java.type('morse.utils.ReadFileUtil');
     return readFile.read(args);
  }
  """

Scenario:
  Given path '/decode'
  And request read('mocks/longSignal.json')
  When method POST
  Then status 200
  And match response == readFile('src/it/java/morse/decode/mocks/longMessage.txt')

Scenario:
  Given path '/decode'
  And request read('mocks/longSignal2.json')
  When method POST
  Then status 200
  And match response == readFile('src/it/java/morse/decode/mocks/longMessage2.txt')
