package com.medsky.automation.pages;

import com.medsky.automation.config.ConfigReader;
import com.medsky.automation.utils.WaitUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasePage {
    protected WebDriver driver;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected WaitUtils waitUtils;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        waitUtils = new WaitUtils(driver);
        PageFactory.initElements(driver, this);

        logger.info("Page Initialized: {}", this.getClass().getSimpleName());
    }

    protected String getPageTitle() {
        String title = driver.getTitle();
        logger.debug("Page title retrieved: '{}'", title);
        return title;
    }

    protected String getCurrentUrl(){
        String url = driver.getCurrentUrl();
        logger.debug("Current URL: {}", url);
        return url;
    }

    protected String getElementText(WebElement element) {
        logger.debug("Getting text from element: {}", element);
        String text = element.getText();
        logger.debug("Text retrieved from element {}: '{}'", element, text);
        return text;
    }

    protected void navigateTo(String url) {
        logger.info("Navigating to URL: '{}'", url);
        driver.get(url);
        logger.debug("Navigation completed successfully");
    }

    protected void typeText(WebElement element, String text) {
        logger.debug("Typing text '{}' into element: {}", text, element);
        element.clear();
        element.sendKeys(text);
        logger.debug("Text entered into element: {}", element);
    }

    protected void clickElement(WebElement element) {
        logger.debug("Clicking element: {}", element);
        waitUtils.waitForElementToBeClickable(element).click();
        logger.debug("Clicked element: {}", element);
    }

    protected boolean isElementDisplayed(WebElement element) {
        try {
            boolean displayed = waitUtils.waitForElementToBeVisible(element).isDisplayed();
            logger.debug("Element {} is displayed: {}", element, displayed);
            return displayed;
        } catch (Exception e) {
            logger.debug("Element {} is not displayed", element);
            return false;
        }
    }

    protected void refreshPage() {
        logger.info("Refreshing current page");
        driver.navigate().refresh();
    }
}
