@Service=View @Protocol=WMS
Feature:  check url for view service

  Background:
    * def metadata = read('metadata.Service.json')
    * print metadata

  @Test=viewServiceExists
  Scenario: check view service url exists
    * print "testing url:" + metadata.url
    Given url metadata.url
    When method HEAD
    Then status 200

#
#  @Test=viewCheckHeaderXML
#  Scenario: check view service url has correct header
#    Given url metadata.url
#    When method HEAD
#    Then status 200
#    And match responseHeaders['Content-Type'][0] == 'text/xml; charset=UTF-8'

