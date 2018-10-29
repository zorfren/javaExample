Feature: API Gist

  Scenario: Create and delete gist tests
    When create gist
    Then gist is succesfully created
    When delete gist
    Then gist is succesfully deleted