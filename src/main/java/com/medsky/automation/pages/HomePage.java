package com.medsky.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HomePage extends BasePage{
    private static final Logger logger = LoggerFactory.getLogger(HomePage.class);

    @FindBy(css = "div.inventory_list")
    private WebElement productListing;

    @FindBy(css = "div.inventory_item")
    private List<WebElement> products;

    @FindBy(css = "div[data-test=inventory-item-name]")
    private List<WebElement> productHeadings;

    @FindBy(css = "div[data-test=inventory-item-price]")
    private List<WebElement> productPrices;

    @FindBy(css = "button.btn_inventory")
    private List<WebElement> addToCart;

    @FindBy(css = "div.bm-burger-button")
    private WebElement hamburgerMenu;

    @FindBy(css = "#logout_sidebar_link")
    private WebElement logoutBtn;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public boolean isProductListingDisplayed() {
        return isElementDisplayed(productListing);
    }

    public int getTotalProducts(){
        return products.size();
    }

    public boolean isProductHeadingDisplayed(int index) {
        try{
            WebElement product = productHeadings.get(index);
            return isElementDisplayed(product);
        } catch (IndexOutOfBoundsException e){
            logger.warn("Invalid product index: {}", index);
            return false;
        }
    }

    public String getProductHeadingText(int index){
        try{
            return productHeadings.get(index).getText();
        } catch (IndexOutOfBoundsException e){
            logger.warn("Invalid product index: {}", index);
            return null;
        }
    }

    public boolean isProductPriceDisplayed(int index) {
        try{
            WebElement productPrice = productPrices.get(index);
            return isElementDisplayed(productPrice);
        } catch (IndexOutOfBoundsException e){
            logger.warn("Invalid product index: {}", index);
            return false;
        }
    }

    public String getProductPriceText(int index){
        try{
            return productPrices.get(index).getText();
        } catch (IndexOutOfBoundsException e){
            logger.warn("Invalid product index: {}", index);
            return null;
        }
    }

    public boolean isAddToCartDisplayed(int index) {
        try{
            WebElement addToCartBtn = addToCart.get(index);
            return isElementDisplayed(addToCartBtn);
        } catch (IndexOutOfBoundsException e){
            logger.warn("Invalid product index: {}", index);
            return false;
        }
    }


    public boolean isHamburgerMenuDisplayed() {
        return isElementDisplayed(hamburgerMenu);
    }

    public HomePage clickHamburgerMenu() {
        clickElement(hamburgerMenu);
        return this;
    }

    public boolean isLogoutBtnDisplayed() {
        return isElementDisplayed(logoutBtn);
    }

    public LoginPage clickLogoutBtn() {
        clickElement(logoutBtn);
        return new LoginPage(driver);
    }
}
