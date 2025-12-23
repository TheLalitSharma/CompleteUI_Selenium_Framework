package com.medsky.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage{

    @FindBy(css = ".login_logo")
    private WebElement loginLogo;

    @FindBy(id = "user-name")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(css = "input[type='submit']")
    private WebElement loginButton;

    @FindBy(css = "div.bm-burger-button")
    private WebElement hamburgerButton;

    @FindBy(css = "h3[data-test='error']")
    private WebElement errorMessage;

    @FindBy(css = "div.login-box")
    private WebElement loginBox;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage enterUsername(String username) {
        logger.debug("Typing username on login page");
        typeText(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        logger.debug("Typing password on login page");
        typeText(passwordField, password);
        return this;
    }

    public LoginPage clickLoginButton() {
        logger.debug("Clicking on login button");
        clickElement(loginButton);
        return this;
    }

    public HomePage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();

        try {
            if(isElementDisplayed(hamburgerButton)) {
                logger.info("Login successful for user: {}", username);
                return new HomePage(driver);
            } else if(isErrorMessageDisplayed()){
                logger.warn("Login failed for user: {}", username);
                return null;
            }
        } catch (Exception e) {
            logger.error("Login process failed for user: {} \n {}", username, e);
        }

        return null;
    }

    public boolean isLoginPageLoaded() {
        logger.debug("Checking if login page is loaded properly");
        return isElementDisplayed(loginButton);
    }

    public boolean isErrorMessageDisplayed() {
        logger.debug("Checking if error message is displayed on login page");
        return isElementDisplayed(errorMessage);
    }
}
