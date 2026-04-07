Feature:Test of Track and Field Calculator Web-UI
  Testscenario 1: calculate points when correct inputs in all the fields (Decathlon)
  Testscenario 2: calculate points when correct inputs in all the fields (Heptathlon)
  Testscenario 1: calculate points when incorrect input in result field (Decathlon)
  Testscenario 2: calculate points when incorrect inputs in result field (Heptathlon)
  Testscenario 2: add competitor to standings when name is empty
  Testscenario 3: create user when password and confirmation password don't match
  Testscenario 4: create user when terms and condition box is not checked


  Scenario Outline: Calculate point valid competitor
    Given I am using "<browser>" as a browser
    And I am at page "<page>"
    When I calculate points "<name>" "<multiEventType>" "<event>" "<result>"
    Then I get points "<points>"

    Examples:
      |browser|page|name|multiEventType|event|result|points|
      |chrome |calculator|Chris|deca|100m    |15    |182   |
      |edge |calculator|Chris|deca|100m    |15    |182   |
      |firefox |calculator|Chris|deca|100m    |15    |182   |

