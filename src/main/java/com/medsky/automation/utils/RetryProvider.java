package com.medsky.automation.utils;

import com.medsky.automation.config.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RetryProvider implements IRetryAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(RetryProvider.class);
    private static final int maxRetry;

    private static final ConcurrentHashMap<String, AtomicInteger> retryCountMap = new ConcurrentHashMap<>();

    static {
        maxRetry = ConfigReader.getRetryCount();
        if(maxRetry == 0) {
            logger.info("Retry feature is disabled.");
        } else {
            logger.info("Retry feature is enabled with max retry count: {}", maxRetry);
        }
    }

    @Override
    public boolean retry(ITestResult result) {
        if(maxRetry == 0)
            return false;

        String testIdentifier = TestUtils.getTestIdentifier(result);

        AtomicInteger counter = retryCountMap.computeIfAbsent(testIdentifier, k -> new AtomicInteger(0));

        int currentRetryCount = counter.get();

        logger.info("Checking if retries available for test - {}", testIdentifier);
        if (currentRetryCount < maxRetry) {
            int retryNumber = counter.incrementAndGet();
            logger.info("Retry available for test-> {}.\nAttempting retry #{} out of total {} retries.", testIdentifier, retryNumber, maxRetry);
            return true;
        } else {
            logger.info("Retries exhausted for test {}->..", testIdentifier);
            return false;
        }
    }

    public static int getRetryCount(String testIdentifier) {
        AtomicInteger counter = retryCountMap.get(testIdentifier);
        return counter != null ? counter.get() : 0;
    }

    public static void removeRetryCount(String testIdentifier) {
        retryCountMap.remove(testIdentifier);
    }

}
