Feature:  check other url

  Background:
    * def metadata = read('metadata.Service.json')
    * print metadata

  Scenario: check service url


    * print "testing url:" + metadata.url

    Given url metadata.url
    When method HEAD
    Then status 200