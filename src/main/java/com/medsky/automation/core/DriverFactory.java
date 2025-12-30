package com.medsky.automation.core;

import com.medsky.automation.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public final class DriverFactory {
    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {}

    public static WebDriver initDriver(String browserName){

        WebDriver driver;

        String runMode = ConfigReader.getRunMode();
        logger.info("Initializing driver with run mode: {}", runMode);

        if(runMode.equalsIgnoreCase("remote")){
            //Implementation of REMOTE EXECUTION
            String remoteURL = ConfigReader.getRemoteURL();

            AbstractDriverOptions<?> browserOptions;

            switch(browserName.toLowerCase()) {

                case "firefox":
                    browserOptions = getFirefoxOptions();
                    break;

                case "safari":
                    browserOptions = getSafariOptions();
                    break;

                default:
                    browserOptions = getChromeOptions();
                    browserOptions.setCapability("browserName", "chrome");
                    break;
            }

            try {
                logger.info("Connecting to Remote Grid: {} with browser: {}", remoteURL, browserName);
                driver = new RemoteWebDriver(new URL(remoteURL), browserOptions);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid Remote URL: " + remoteURL, e);
            }

        } else {
            //LOCAL EXECUTION
            switch(browserName.toLowerCase()) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = getChromeOptions();
                    driver = new ChromeDriver(chromeOptions);
                    break;

                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = getFirefoxOptions();
                    driver = new FirefoxDriver(firefoxOptions);
                    break;

                case "safari":
                    WebDriverManager.safaridriver().setup();
                    SafariOptions safariOptions = getSafariOptions();
                    driver = new SafariDriver(safariOptions);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported browser name: " + browserName);
            }
        }

        return driver;
    }

    private static ChromeOptions getChromeOptions(){
        ChromeOptions chromeOptions = new ChromeOptions();
        if(ConfigReader.getRunMode().equalsIgnoreCase("remote")) {
            chromeOptions.addArguments(
                    "--no-sandbox",
                    "--disable-dev-shm-usage"
            );
            if(ConfigReader.isHeadless()) {
                chromeOptions.addArguments("--headless=new");
            }

            chromeOptions.setPlatformName("LINUX");
        } else
            chromeOptions.addArguments("--incognito");
        return chromeOptions;
    }

    private static FirefoxOptions getFirefoxOptions(){
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.addPreference("dom.webnotifications.enabled", false);
        firefoxOptions.addPreference("media.navigator.permission.disabled", true);
        firefoxOptions.addArguments("-private");

        return firefoxOptions;
    }

    private static SafariOptions getSafariOptions(){
        SafariOptions safariOptions = new SafariOptions();

        safariOptions.setAcceptInsecureCerts(true);

        return safariOptions;
    }
}
