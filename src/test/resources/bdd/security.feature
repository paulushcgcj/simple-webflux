@author
Feature: Security

  Background:
    Given User "cucumber" exists with password "itsoursecret"

  Scenario: Correct User
    When I call "/login" with user "cucumber" and password "itsoursecret"
    Then I should receive back 200 and check 1 JWT token

  Scenario: Wrong user
    When I call "/login" with user "jello" and password "itsoursecret"
    Then I should receive back 401 and check 0 JWT token

  Scenario: Wrong password only
    When I call "/login" with user "cucumber" and password "approaches"
    Then I should receive back 401 and check 0 JWT token
