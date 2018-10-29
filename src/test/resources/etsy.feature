Feature: Etsy feature

  Scenario: New user is registered
    When user is created
    And user login
    Then user is logged in the page

  Scenario: Cart content test
    When user login
    And user search for "sketchbook"
    And user sort by price ascending
    Then items are sorted
    When user add most expensive item
    And user search for "turntable mat"
    And add any item to cart
    Then validate cart content have added items