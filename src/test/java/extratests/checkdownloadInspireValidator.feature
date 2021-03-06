@Ignore
@ServiceDownload @Protocol=ATOM
@Test=downloadInspireValidation

Feature: Service test Inspire validator

  Background:
    * url 'https://inspire.ec.europa.eu/validator/v2/'
    * def metadata = read('metadata.Service.json')
    * print metadata



  Scenario Outline: <label> Inspire testsuite id: <testsuite>
    * def resultmessage = karate.info.scenarioName + " " + karate.tagValues
    * def testRunRequest =
      """
      {
        "label": "<label>",
        "executableTestSuiteIds": [
          "<testsuite>"
        ],
        "arguments": {},
        "testObject": {
          "resources": {
            "serviceEndpoint": "<serviceEndpoint>"
          }
        }
      }
      """

    * replace testRunRequest.serviceEndpoint = metadata.url
    * print testRunRequest
    * json inspireload = testRunRequest


    Given path 'TestRuns'
    And request inspireload
    When method post
    Then assert responseStatus == 200 || responseStatus == 201
    * print response.EtfItemCollection.testRuns.TestRun.id
    * print response.EtfItemCollection.testRuns.TestRun.status
    * print response.EtfItemCollection.testRuns.TestRun.label
    * print response.EtfItemCollection.ref
    * print response.EtfItemCollection.testRuns.TestRun.logPath

    * def statuspath = "TestRuns/" + response.EtfItemCollection.testRuns.TestRun.id
    * def progresspath = "TestRuns/" + response.EtfItemCollection.testRuns.TestRun.id + "/progress"
    * print 'statuspath', statuspath
    * print 'progresspath ', progresspath

    #Given path  statuspath
    #When method HEAD
    # And retry until responseStatus == 204
    #* print response

    Given path  progresspath
    When method GET
    And retry until response.val == response.max
    * print response

    Given path  progresspath
    When method GET

    * print response

    Given path  statuspath
    When method GET

    * print response.EtfItemCollection.testRuns.TestRun.id
    * print response.EtfItemCollection.testRuns.TestRun.status
    * def status = response.EtfItemCollection.testRuns.TestRun.status
    * print status
    * print response.EtfItemCollection.testRuns.TestRun.label
    * print response.EtfItemCollection.ref
    * def ref = response.EtfItemCollection.ref
    * print ref

    * print response.EtfItemCollection.testRuns.TestRun.logPath

    # save response
    *  def embedUrl =
      """ function(url, hyperlinkText)
      { var html = '<a href=\"' + url + '\" >' + hyperlinkText + '</a>';
      karate.embed(html,'text/html'); }
      """

    * def time = java.lang.System.currentTimeMillis()
    * def jsonPath = time + '<label>.json'
    * def responsecontent = response
    * karate.write(responsecontent, jsonPath)
    * def a = embedUrl (ref.substring(0, ref.length - 5) + '.html' ,  status )
    * def a = embedUrl ("../../" + jsonPath ,  status )

    # lets compare content

    * def json = get[0] response.EtfItemCollection.referencedItems.testTaskResults
    * def jsonfailedStep = $json.TestTaskResult.testModuleResults.TestModuleResult[*].testCaseResults.TestCaseResult[*].testStepResults.TestStepResult[?(@.status=='FAILED')]
    * def jsonFailedStepmessages = $jsonfailedStep[*].messages.message.ref
    # * print jsonFailedStepmessages
    * def jsonfailedAssert = $jsonfailedStep[*].testAssertionResults.TestAssertionResult[?(@.status=='FAILED')]
    * def jsonfailedMessages = $jsonfailedAssert[*].messages.message
    * def tmpref = $jsonfailedMessages[*].ref
    * def refs = karate.append( jsonFailedStepmessages, tmpref )


    #* def expectedresult = <ETFexpected>
    * def Collections = Java.type('java.util.Collections')
    * copy sortedrefs = refs
    * Collections.sort(sortedrefs)
    #* copy sortedexpectedrefs = $expectedresult[*].ref
    #* Collections.sort(sortedexpectedrefs)
    * print refs
    * print sortedrefs
    #* print sortedexpectedrefs
    #* match sortedrefs == sortedexpectedrefs

    #* print jsonfailedMessages
    #* match jsonfailedMessages == expectedresult


    * def tempdir = java.lang.System.getProperty('user.dir')
    * def separator = java.lang.System.getProperty("file.separator")
    * def mystorage = Java.type('storage.DataStorage')
    * def db = new mystorage
    * def LocalDateTime = Java.type('java.time.LocalDateTime')
    * eval db.writeln('- Test: '+ karate.info.scenarioName+ '\n  Time: '+ LocalDateTime.now() +'\n  title: ' + metadata.title  +'\n  url: ' + metadata.url + '\n  Errors: ' +  sortedrefs  , db.outputdir()+ separator +  'Inspirevalidator.yaml')
    * def db = db.setfeature(karate.info.featureFileName)
    * def outfile = db.outputdir() + separator +  'Inspirevalidator.csv'
    * db.storeInspireResults(metadata.serviceIdentifierCode, resultmessage, metadata.title, metadata.url, sortedrefs)


    Examples:
      | testsuite                               | label                                                 | ETFexpected                                              |
      | EID11571c92-3940-4f42-a6cd-5e2b1c6f4d93 | Conformance Class Download Service - Pre-defined Atom | read('classpath:InspireTest/ETFexpected/atomerror.json') |
