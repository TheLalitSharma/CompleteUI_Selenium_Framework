package com.medsky.automation.utils;

import org.testng.ITestResult;

import java.util.Arrays;

public class TestUtils {
    public static String getTestIdentifier(ITestResult result) {
        String className = result.getTestClass().getName();
        String methodName = result.getMethod().getMethodName();
        Object[] parameters = result.getParameters();

        if (parameters != null && parameters.length > 0) {
            return className + "." + methodName + "_" + Arrays.toString(parameters);
        }

        return className + "." + methodName;
    }

    public static String getTestDisplayName(ITestResult result) {
        String className = result.getTestClass().getRealClass().getSimpleName();
        String methodName = result.getMethod().getMethodName();
        return className + "." + methodName;
    }
}
