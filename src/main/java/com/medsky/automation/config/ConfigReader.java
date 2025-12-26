package com.medsky.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigReader {

    private static final String DEFAULT_CONFIG_FILE = "config/config.properties";
    private static final String ENV_CONFIG_PATTERN = "config/config_%s.properties";
    private static final String DEFAULT_ENV = "local";

    private static Properties properties;
    private static String currentEnvironment;

    private ConfigReader() {}

    static {
        loadProperties();
    }

    private static void loadProperties(){
        properties = new Properties();
        currentEnvironment = determineEnvironment();

        loadConfigFile(DEFAULT_CONFIG_FILE, true);

        String envConfigFile = String.format(ENV_CONFIG_PATTERN, currentEnvironment);
        loadConfigFile(envConfigFile, false);
    }

    private static String determineEnvironment(){
        String env = System.getProperty("env");

        if(env == null || env.trim().isEmpty())
            env = System.getenv("TEST_ENV");

        if(env == null || env.trim().isEmpty())
            env = DEFAULT_ENV;

        return env.toLowerCase().trim();
    }

    private static void loadConfigFile(String configPath, boolean required){
        try(InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(configPath)){
            if(inputStream == null) {
                if(required)
                    throw new RuntimeException("Required config file not found: " + configPath);
                else {
                    System.out.println("Optional config file not found: " + configPath);
                    return;
                }
            }
            properties.load(inputStream);
        } catch (IOException e) {
            String message = String.format("Error loading config file: %s", configPath);
            throw new RuntimeException(message, e);
        }
    }

    public static String getCurrentEnvironment() {
        return currentEnvironment;
    }

    public static void reload() {
        loadProperties();
    }

    public static String getProperty(String key){
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue){
       return properties.getProperty(key, defaultValue);
    }

    public static int getIntProperty(String key){
        try{
            return Integer.parseInt(properties.getProperty(key).trim());
        } catch (NumberFormatException exception){
            throw new RuntimeException(exception);
        }
    }

    public static int getIntProperty(String key, int defaultValue){
        try{
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)).trim());
        } catch (NumberFormatException exception){
            throw new RuntimeException(exception);
        }
    }

    public static boolean getBooleanProperty(String key){
        return Boolean.parseBoolean(properties.getProperty(key).trim());
    }

    public static boolean getBooleanProperty(String key, boolean defaultValue){
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)).trim());
    }

    public static Long getLongProperty(String key){
        try{
            return Long.parseLong(properties.getProperty(key).trim());
        } catch (NumberFormatException exception){
            throw new RuntimeException(exception);
        }
    }

    public static Long getLongProperty(String key, long defaultValue){
        try{
            return Long.parseLong(properties.getProperty(key, String.valueOf(defaultValue)).trim());
        } catch (NumberFormatException exception){
            throw new RuntimeException(exception);
        }
    }

    //Methods for common properties
    public static String getBrowserName(){
        String browser = System.getProperty("browser");

        if(browser == null || browser.trim().isEmpty()) {
            getProperty("browser", "chrome");
        }

        return browser;
    }

    public static String getRunMode(){
        String runMode = System.getProperty("runMode");

        if(runMode == null) {
            runMode = getProperty("runMode", "local");
        }

        return runMode;
    }

    public static boolean isHeadless(){
        return getBooleanProperty("headless", true);
    }

    public static int getImplicitWait(){
        return getIntProperty("implicitWait", 10);
    }

    public static int getExplicitWait(){
        return getIntProperty("explicitWait", 20);
    }

    public static String getBaseUrl(){
        return getProperty("baseUrl", "https://www.saucedemo.com");
    }

    public static int getRetryCount() {
        return getIntProperty("retryCount", 0);
    }

    public static String getRemoteURL() {
        String url = System.getProperty("gridURL");

        if(url == null || url.isEmpty()) {
            url = getProperty("gridURL");
        }

        if(url == null || url.isEmpty()) {
            url = "http://localhost:4444";
        }

        return url;
    }
}
