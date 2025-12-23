package com.medsky.automation.utils;

import com.medsky.automation.config.ConfigReader;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class WaitUtils {
    private static final Logger logger = LoggerFactory.getLogger(WaitUtils.class);
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final int defaultTimeout;

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.defaultTimeout = ConfigReader.getExplicitWait();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(defaultTimeout));
    }

    public WebElement waitForElementToBeVisible(WebElement element) {
        try {
            logger.debug("Waiting for element to be visible");
            return wait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            logger.error("Element not visible within {} seconds", defaultTimeout, e);
            throw e;
        }
    }

    public WebElement waitForElementToBeVisible(WebElement element, int timeoutSeconds) {
        try {
            logger.debug("Waiting for element to be visible with timeout: {} seconds", timeoutSeconds);
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            return customWait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            logger.error("Element not visible within {} seconds", timeoutSeconds, e);
            throw e;
        }
    }

    public WebElement waitForElementToBeClickable(WebElement element) {
        try {
            logger.debug("Waiting for element to be clickable");
            return wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            logger.error("Element not clickable within {} seconds", defaultTimeout, e);
            throw e;
        }
    }

    /**
     * Wait for element to be clickable with custom timeout
     */
    public WebElement waitForElementToBeClickable(WebElement element, int timeoutSeconds) {
        try {
            logger.debug("Waiting for element to be clickable with timeout: {} seconds", timeoutSeconds);
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            return customWait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            logger.error("Element not clickable within {} seconds", timeoutSeconds, e);
            throw e;
        }
    }

    public boolean waitForTextToBePresentInElement(WebElement element, String text) {
        try {
            logger.debug("Waiting for text '{}' to be present in element", text);
            return wait.until(ExpectedConditions.textToBePresentInElement(element, text));
        } catch (Exception e) {
            logger.error("Text '{}' not present in element within {} seconds", text, defaultTimeout, e);
            return false;
        }
    }

    public boolean waitForElementToBeInvisible(WebElement element) {
        try {
            logger.debug("Waiting for element to become invisible");
            return wait.until(ExpectedConditions.invisibilityOf(element));
        } catch (Exception e) {
            logger.error("Element still visible after {} seconds", defaultTimeout, e);
            return false;
        }
    }

    public void sleep(int seconds) {
        try {
            logger.debug("Sleeping for {} seconds", seconds);
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            logger.warn("Sleep interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    public void waitForJavaScriptToComplete() {
        try {
            logger.debug("Waiting for JavaScript to complete");
            wait.until(driver -> {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                return js.executeScript("return jQuery.active == 0");
            });
        } catch (Exception e) {
            logger.debug("jQuery not available or JavaScript wait failed", e);
        }
    }
}
