package com.medsky.automation.core;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DriverManager {
    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);

    public static void setDriver(WebDriver driver){
        tlDriver.set(driver);
    }

    public static WebDriver getDriver(){
        return tlDriver.get();
    }

    public static void unloadDriver(){
        WebDriver driver = tlDriver.get();
        if(driver != null){
            try{
                driver.quit();
            } catch (Exception e){
                logger.debug("Unable to quit browser on threadId: {}", Thread.currentThread().getId());
            } finally {
                tlDriver.remove();
            }
        }
        tlDriver.remove();
    }


}
