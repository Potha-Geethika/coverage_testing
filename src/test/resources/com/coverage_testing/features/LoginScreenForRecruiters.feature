
Feature: LoginScreenForRecruiters
  @feature_user_login
  Background: 
    Given the application is opened

  @verify_get_started_button
  Scenario Outline: Login Screen for Recruiters
    Given the Welcome page is displayed
    When I click on the 'Get Started' button
    And I wait for the page to load
    Then the user should be redirected to the Organization signup screen
    And the 'Continue as Organization' button should be present on the new screen

  Examples:
    | action                     |
    | Open the application       |
    | Observe the Welcome page   |
    | Click on the 'Get Started' button |
    | Wait for the page to load  |
    | Observe the new page       |
    | Verify the presence of the 'Continue as Organization' button |

  @feature_user_login
  @navigate_to_login_page
  Scenario Outline: Login Screen for Recruiters
    When the user clicks on the 'Continue as Organization' button
    And the user waits for the page to load
    Then the user should see the Login page
    And the Email ID field should be present
    And the Password field should be present
    And the 'Login' button should be visible
    And the 'Show/Hide Password' option should be available

  Examples:
    | action                          |
    | Click on the 'Continue as Organization' button |
    | Wait for the page to load      |
    | Observe the new page           |
    | Verify the presence of Email ID and Password fields |
    | Check for the 'Login' button   |
    | Check for the 'Show/Hide Password' option |

@valid_login
Scenario Outline: Login Screen for Recruiters
  Given I enter a valid Email ID "<email>" in the Email ID field
  And I enter a valid Password "<password>" in the Password field
  When I click on the 'Login' button
  Then the user should be redirected to the dashboard
  And user-specific information should be displayed on the dashboard

Examples:
  | email              | password          |
  | krishna@gmail.com  | ValidPassword123  |

  @password_visibility_toggle
  Scenario Outline: Verify 'Show/Hide Password' option toggles visibility
    Given the user enters a valid password in the Password field
    When the user clicks on the 'Show/Hide Password' option
    Then the password should be visible
    When the user clicks on the 'Show/Hide Password' option again
    Then the password should be hidden

    Examples:
      | password            |
      | ValidPassword123!   |

  @feature_user_login
  @valid_login
  Scenario Outline: Login Screen for Recruiters
    Given the user enters a valid Email ID <email> in the Email ID field
    And the user enters a valid Password <password> in the Password field
    Then the 'Login' button should be enabled
    When the user clicks on the 'Show/Hide Password' option
    Then the 'Login' button should remain enabled
    When the user clicks on the 'Login' button
    Then the user should be successfully logged in

  Examples:
    | email              | password            |
    | krishna@gmail.com  | SecurePassword123   |

  @feature_user_login
  @ui-elements-verification
  Scenario Outline: Verify UI elements on Login page for Recruiters
    When the user observes the <element>
    Then the <element> is present and correctly labeled

    Examples:
      | element                     |
      | Email ID field             |
      | Password field              |
      | 'Login' button             |
      | 'Show/Hide Password' option |
      
  @ui-elements-verification
  Scenario: Verify layout and alignment of all elements
    Then all elements are properly aligned and visually appealing

  @feature_user_login
  @navigate_back_to_welcome
  Scenario Outline: Verify that the user can navigate back to the Welcome page from the Login page
    When the user clicks on the browser's back button
    And the user waits for the page to load
    Then the user should observe the new page
    And the 'Get Started' button should be visible
    When the user clicks on the 'Get Started' button
    Then the Welcome page should be displayed

  Examples:
    | username | password |
    |          |          |

  @failed_login_attempt
  Scenario Outline: Login Screen for Recruiters
    When I enter "<email>" in the Email ID field
    And I enter "<password>" in the Password field
    And I click on the 'Login' button
    Then an error message should be displayed
    When I enter "<valid_email>" in the Email ID field
    And I enter "<valid_password>" in the Password field
    And I click on the 'Login' button again
    Then the user should be redirected to the dashboard

    Examples:
      | email         | password       | valid_email          | valid_password    |
      | abc@         | wrongpassword  | krishna@gmail.com    | correctpassword    |
