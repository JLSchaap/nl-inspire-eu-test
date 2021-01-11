@Service=Download @Protocol=ATOM
Feature:  check url for download

  Background:
    * def metadata = read('metadata.Service.json')
    * print metadata

  @Test=downloadExists
  Scenario: check service url
    * print "testing url:" + metadata.url
    Given url metadata.url
    When method HEAD
    Then status 200
  
 # @Test=downloadHeaderCheck
 # Scenario:  test download header
 #   Given url metadata.url
 #   When method HEAD
 #   Then status 200
 #   And match responseHeaders['Content-Length'][0] == '#notnull'
 #   And match responseHeaders['Content-Type'][0] == 'text/xml'
 #   * def filesize =  responseHeaders['Content-Length'][0]
 #   * assert filesize > 1500

