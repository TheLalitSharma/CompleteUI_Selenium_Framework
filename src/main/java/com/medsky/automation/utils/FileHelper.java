package com.medsky.automation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class FileHelper {
    private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);

    private FileHelper() {}

    public static boolean createDirectories(String directoryName) {
        boolean flag = false;
        File dir = new File(directoryName);
        if(!dir.exists()) {
            try {
                flag = dir.mkdirs();
            } catch (Exception e) {
                logger.error("Unable to create directory - {}", directoryName, e);
            }
        } else {
            return true;
        }
        return flag;
    }

    public static String getRelativePath(File firstFile, File secondFile) {
        try {
            // Get paths
            Path reportPath = firstFile.toPath();
            Path screenshotPath = secondFile.toPath();

            // Make both paths absolute and normalize
            reportPath = reportPath.toAbsolutePath().normalize();
            screenshotPath = screenshotPath.toAbsolutePath().normalize();

            // Calculate relative path
            System.out.println(reportPath);
            System.out.println(screenshotPath);
            System.out.println(reportPath.relativize(screenshotPath));
            Path relativePath = reportPath.relativize(screenshotPath);

            // Convert to string with forward slashes (works in all browsers)

            return relativePath.toString().replace("\\", "/");

        } catch (Exception e) {
            logger.warn("Failed to calculate relative path, using absolute path as fallback", e);
            return secondFile.getAbsolutePath();
        }
    }
}
