package com.medsky.automation.utils;

import com.medsky.automation.core.DriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static final String EXTENT_REPORT_DIR = "reports";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");

    private ScreenshotUtils() {
    }

    public static String takeScreenshotForExtentReport(String testName) {
        File extentReportDir = new File(EXTENT_REPORT_DIR);
        File screenshotDir = new File(SCREENSHOT_DIR);
        boolean directoryExists = FileHelper.createDirectories(SCREENSHOT_DIR);
        if(directoryExists) {
            String currentTime = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String screenshotFileName = testName + "_" + currentTime + ".png";
            File screenshotFile = new File(screenshotDir, screenshotFileName);
            File file = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
            try {
                FileUtils.copyFile(file, screenshotFile);
            } catch (IOException e) {
                logger.error("Unable to take screenshot", e);
            }
            return FileHelper.getRelativePath(extentReportDir, screenshotFile);
        }
        return null;
    }

    public static String takeScreenshot(String testName) {
        File screenshotDir = new File(SCREENSHOT_DIR);
        boolean directoryExists = FileHelper.createDirectories(SCREENSHOT_DIR);
        if(directoryExists) {
            String currentTime = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String screenshotFileName = testName + "_" + currentTime + ".png";
            File screenshotFile = new File(screenshotDir, screenshotFileName);
            File file = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
            try {
                FileUtils.copyFile(file, screenshotFile);
            } catch (IOException e) {
                logger.error("Unable to take screenshot", e);
            }
            return screenshotFile.getPath();
        }
        return null;
    }



}
