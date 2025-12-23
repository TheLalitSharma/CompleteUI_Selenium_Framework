package com.medsky.automation.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.medsky.automation.config.ConfigReader;
import com.medsky.automation.reporters.ExtentManager;
import com.medsky.automation.utils.RetryProvider;
import com.medsky.automation.utils.ScreenshotUtils;
import com.medsky.automation.utils.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtentTestListener implements ITestListener {
    private static final Logger logger = LoggerFactory.getLogger(ExtentTestListener.class);
    private static final ExtentReports extentReports = ExtentManager.getExtentReports();
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static final Map<String, ExtentTest> testMap = new ConcurrentHashMap<>();

    private static final String TEST_NAME_SEPARATOR = ".";

    @Override
    public void onTestStart(ITestResult result) {
        String testIdentifier = TestUtils.getTestIdentifier(result);

        // If testMap has the key, it means this is a RETRY of a previously started test
        if (testMap.containsKey(testIdentifier)) {
            test.set(testMap.get(testIdentifier));
            int attemptNumber = RetryProvider.getRetryCount(testIdentifier) + 1;
            logger.info("Retrying test (Attempt #{}): {}", attemptNumber, getCompleteTestName(result));
        } else {
            // First time execution
            ExtentTest extentTest = createNewExtentTest(result);
            testMap.put(testIdentifier, extentTest);
            test.set(extentTest);
            logger.info("Started new test: {}", getCompleteTestName(result));
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testIdentifier = TestUtils.getTestIdentifier(result);
        ExtentTest extentTest = getExtentTest(testIdentifier);

        if (extentTest == null) {
            return;
        }

        int retriesMade = RetryProvider.getRetryCount(testIdentifier);
        int totalAttempts = retriesMade + 1;

        if (retriesMade > 0) {
            extentTest.log(Status.PASS, MarkupHelper.createLabel(
                    "Test PASSED on attempt #" + totalAttempts, ExtentColor.GREEN));
            logger.info("Test PASSED on attempt #{} - {}", totalAttempts, getCompleteTestName(result));
        } else {
            extentTest.log(Status.PASS, "Test Passed");
            logger.info("Test PASSED - {}", getCompleteTestName(result));
        }

        cleanupTest(testIdentifier);

    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testIdentifier = TestUtils.getTestIdentifier(result);
        ExtentTest extentTest = getExtentTest(testIdentifier);

        if(extentTest == null) {
            return;
        }

        int retriesMade = RetryProvider.getRetryCount(testIdentifier);
        int totalAttempts = retriesMade + 1;

        if (retriesMade > 0) {
            extentTest.log(Status.FAIL, MarkupHelper.createLabel(
                    "Test FAILED after " + totalAttempts + " attempts", ExtentColor.RED));
            logger.error("Test FAILED after {} attempts - {}", totalAttempts, getCompleteTestName(result));
        } else {
            extentTest.fail("Test Failed");
            logger.error("Test FAILED - {}", getCompleteTestName(result));
        }

        if (result.getThrowable() != null) {
            extentTest.fail(result.getThrowable());
        }

        attachScreenshotIfAvailable(extentTest, result.getName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testIdentifier = TestUtils.getTestIdentifier(result);
        ExtentTest extentTest = getExtentTest(testIdentifier);

        if (extentTest == null) {
            return;
        }

        int currentRetryCount = RetryProvider.getRetryCount(testIdentifier);
        int maxRetries = ConfigReader.getRetryCount();

        // If the retry analyzer incremented the count, it's a retry attempt.
        boolean isRetryAttempt = (currentRetryCount > 0 && currentRetryCount <= maxRetries);

        if (isRetryAttempt) {
            logger.warn("Test Attempt #{} Failed. Retrying... - {}", currentRetryCount, getCompleteTestName(result));

            extentTest.log(Status.WARNING, MarkupHelper.createLabel(
                    "Attempt #" + currentRetryCount + " FAILED - Retrying...", ExtentColor.ORANGE));

            if (result.getThrowable() != null) {
                extentTest.warning(result.getThrowable());
            }

            attachScreenshotIfAvailable(extentTest, result.getName());
        } else {
            logger.warn("Test SKIPPED - {}", getCompleteTestName(result));
            extentTest.log(Status.SKIP, MarkupHelper.createLabel("Test SKIPPED", ExtentColor.ORANGE));

            if (result.getThrowable() != null) {
                extentTest.skip(result.getThrowable());
                cleanupTest(testIdentifier);
            }
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        if (!testMap.isEmpty()) {
            logger.warn("Cleaning up {} orphaned test entries in onFinish.", testMap.size());
            testMap.clear();
        }

        extentReports.flush();
        test.remove();

        logger.debug("ExtentReports flushed successfully");
    }

    // --- Helper Methods ---

    private ExtentTest createNewExtentTest(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getRealClass().getSimpleName();

        ExtentTest extentTest = extentReports.createTest(methodName);
        extentTest.assignCategory(className);

        return extentTest;
    }

    private String getCompleteTestName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName()
                + TEST_NAME_SEPARATOR
                + result.getName();
    }

    private ExtentTest getExtentTest(String testIdentifier) {
        ExtentTest extentTest = test.get();

        if (extentTest == null) {
            logger.error("ExtentTest is null for test: {}. This indicates onTestStart was not called properly.",
                    testIdentifier);
        }

        return extentTest;
    }

    private void cleanupTest(String testIdentifier) {
        testMap.remove(testIdentifier);
        RetryProvider.removeRetryCount(testIdentifier);
        test.remove();
        logger.debug("Cleaned up resources for test: {}", testIdentifier);
    }

    private void attachScreenshotIfAvailable(ExtentTest extentTest, String testName) {
        try {
            String screenshotPath = ScreenshotUtils.takeScreenshotForExtentReport(testName);
            if (screenshotPath != null && !screenshotPath.isEmpty()) {
                extentTest.addScreenCaptureFromPath(screenshotPath);
            }
        } catch (Exception e) {
            logger.error("Failed to attach screenshot for test: {}", testName, e);
        }
    }

}
