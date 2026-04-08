Feature: Track and Field Calculator App

  Testscenario 1: Calculate points with all fields valid (decathlon)
  Testscenario 2: Calculate points with all fields valid (heptathlon)


  Scenario Outline: Calculate points valid fields
    Given I am using "<browser>" as a browser
    And I am on page "<page>"
    When I calculate points "<competitorName>" "<multiEvent>" "<event>" "<result>"
    Then I get the score "<points>"

    Examples:
      | browser | page       | competitorName | multiEvent | event    | result | points |
      | chrome  | calculator | Robin          | decathlon  | longJump | 430    | 255    |
      | edge    | calculator | Robin          | decathlon  | longJump | 430    | 255    |
      | firefox | calculator | Robin          | decathlon  | longJump | 430    | 255    |

  Scenario Outline: Calculate multiple valid competitors
    Given I am using "<browser>" as a browser
    And I am on page "<page>"
    When I calculate points "<competitorName>" "<multiEvent>" "<event>" "<result>" for "<amount>" competitors
    Then I get the score "<points>"

  Examples:
    | browser | page       | competitorName | multiEvent | event    | result | amount | points |
    | chrome  | calculator | Ann            | decathlon  | longJump | 430    | 3      | 255    |
    | edge    | calculator | Ann            | decathlon  | longJump | 430    | 3      | 255    |
    | firefox | calculator | Ann            | decathlon  | longJump | 430    | 3      | 255    |

