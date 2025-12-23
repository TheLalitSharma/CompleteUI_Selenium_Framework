package com.medsky.automation.tests;

import com.medsky.automation.config.ConfigReader;
import com.medsky.automation.core.DriverFactory;
import com.medsky.automation.core.DriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public abstract class BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected BaseTest() {}

    @BeforeSuite(alwaysRun = true)
    public void initialize(){
        logger.info("Running in: {}", ConfigReader.getCurrentEnvironment());
        logger.info("Base URL: {}", ConfigReader.getBaseUrl());
        logger.info("Browser: {}", ConfigReader.getBrowserName());
    }

    @BeforeMethod(alwaysRun = true)
    public void setup(){
        String browserName = ConfigReader.getBrowserName();
        String appUrl = ConfigReader.getBaseUrl();
        DriverManager.setDriver(DriverFactory.initDriver(browserName));
        DriverManager.getDriver().get(appUrl);
    }

    @AfterMethod(alwaysRun = true)
    public void teardown(){
        DriverManager.unloadDriver();
    }
}
