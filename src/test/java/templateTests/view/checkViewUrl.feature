@Service=view
Feature:  check url for view service

  Background:
    * def metadata = read('metadata.Service.json')
    * print metadata

  Scenario: check view service url


    * print "testing url:" + metadata.url

    Given url metadata.url
    When method HEAD
    Then status 200
    # get capabilities type = xml 
    And match responseHeaders['Content-Type'][0] == 'text/xml'

