@author
Feature: Company Management

  Background:
    Given The application is available locally with a random port
    And The database is available

  Scenario: List no company at first
    When I fetch companies at "/api/companies"
    Then I should get status 200 and 0 entries