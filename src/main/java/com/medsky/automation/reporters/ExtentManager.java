package com.medsky.automation.reporters;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtentManager {
    private static final Logger logger = LoggerFactory.getLogger(ExtentManager.class);
    private static ExtentReports extentReport;

    public static ExtentReports getExtentReports() {
        if(extentReport == null) {
            String path = System.getProperty("user.dir") + "/reports/ExtentReport.html";
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(path);
            sparkReporter.config().setReportName("Automation Test Results");
            sparkReporter.config().setDocumentTitle("Selenium TestNG Report");
            sparkReporter.config().setTheme(Theme.DARK);
            //sparkReporter.config().setTimeStampFormat();
            logger.debug("Reporter config setup done");

            extentReport = new ExtentReports();
            extentReport.attachReporter(sparkReporter);
            logger.debug("Spark reporter attached");

            extentReport.setSystemInfo("OS", System.getProperty("os.name"));
            extentReport.setSystemInfo("Java Version", System.getProperty("java.version"));
            extentReport.setSystemInfo("Tester Name", "Lalit Sharma");
        }
        return extentReport;
    }
}
