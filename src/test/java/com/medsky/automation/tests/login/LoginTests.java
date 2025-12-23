package com.medsky.automation.tests.login;

import com.medsky.automation.core.DriverManager;
import com.medsky.automation.enums.UserType;
import com.medsky.automation.managers.SmartCredentialsManager;
import com.medsky.automation.models.Credentials;
import com.medsky.automation.pages.HomePage;
import com.medsky.automation.pages.LoginPage;
import com.medsky.automation.tests.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    @Test
    public void TC_Login_01() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        Assert.assertTrue(loginPage.isLoginPageLoaded(), "Login page is not loaded");
    }

    @Test
    public void TC_Login_02() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        Credentials creds = SmartCredentialsManager.getCredentials(UserType.STANDARD);
        HomePage homePage = loginPage.login(creds.getUsername(), creds.getPassword());
        Assert.assertNotNull(homePage, "Login failure");
    }

    @Test
    public void TC_Login_03() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        loginPage
                .enterUsername("")
                .enterPassword("")
                .clickLoginButton();
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Login error message not displayed");
    }
}
