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

public class LogoutTests extends BaseTest {

    @Test
    public void TC_Logout_01() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        Credentials creds = SmartCredentialsManager.getCredentials(UserType.STANDARD);
        HomePage homePage = loginPage.login(creds.getUsername(), creds.getPassword());
        Assert.assertTrue(homePage.isHamburgerMenuDisplayed(), "Hamburger menu is not displayed");
        homePage.clickHamburgerMenu();
        Assert.assertTrue(homePage.isLogoutBtnDisplayed(), "Logout button not visible");
        loginPage = homePage.clickLogoutBtn();
        Assert.assertTrue(loginPage.isLoginPageLoaded(), "Logout failed");
    }

    @Test
    public void TC_Logout_02() {
        Assert.fail();
    }

}
