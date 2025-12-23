package com.medsky.automation.tests.dashboard;

import com.medsky.automation.core.DriverManager;
import com.medsky.automation.enums.UserType;
import com.medsky.automation.managers.SmartCredentialsManager;
import com.medsky.automation.models.Credentials;
import com.medsky.automation.pages.HomePage;
import com.medsky.automation.pages.LoginPage;
import com.medsky.automation.tests.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductListingTests extends BaseTest {

    @Test
    public void TC_ProductListing_01() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        Credentials creds = SmartCredentialsManager.getCredentials(UserType.STANDARD);
        HomePage homePage = loginPage.login(creds.getUsername(), creds.getPassword());
        Assert.assertTrue(homePage.isHamburgerMenuDisplayed(), "Login failed");
        Assert.assertTrue(homePage.isProductListingDisplayed(), "Product listing is not displayed");
    }

    @Test
    public void TC_ProductListing_02(){
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        Credentials creds = SmartCredentialsManager.getCredentials(UserType.STANDARD);
        HomePage homePage = loginPage.login(creds.getUsername(), creds.getPassword());

        Assert.assertTrue(homePage.isHamburgerMenuDisplayed(), "Login failed");
        Assert.assertTrue(homePage.isProductHeadingDisplayed(1), "Product heading is missing");
        Assert.assertTrue(homePage.isProductPriceDisplayed(1), "Product price is missing");
        Assert.assertTrue(homePage.isAddToCartDisplayed(1), "Add to cart button is missing");
    }
}
