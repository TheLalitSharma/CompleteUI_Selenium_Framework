package com.medsky.automation.managers;

import com.medsky.automation.enums.UserType;
import com.medsky.automation.models.Credentials;
import com.medsky.automation.utils.EncryptionHelper;
import com.medsky.automation.utils.YamlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SmartCredentialsManager {
    private static final Logger logger = LoggerFactory.getLogger(SmartCredentialsManager.class);
    private static final String CREDENTIALS_FILE = "credentials.yaml";
    private static final String LOCAL_CREDENTIALS_FILE = "credentials-local.yaml";

    private static final Map<UserType, Credentials> credentialsCache = new ConcurrentHashMap<>();

    private SmartCredentialsManager() {

    }

    public static Credentials getCredentials(UserType userType) {
        Objects.requireNonNull(userType, "User type cannot be null");

        logger.info("Fetching credentials for user type: {}", userType);

        if (credentialsCache.containsKey(userType)) {
            logger.debug("Returning cached credentials for: {}", userType);
            return credentialsCache.get(userType);
        }

        Credentials credentials = null;

        credentials = getCredentialsFromEnvironment(userType);
        if (credentials != null) {
            logger.info("Using environment variable credentials for: {}", userType);
            credentialsCache.put(userType, credentials);
            return credentials;
        }

        if (isLocalCredentialsFileExists()) {
            credentials = getCredentialsFromLocalFile(userType);
            if (credentials != null) {
                logger.info("Using local credentials file for: {}", userType);
                credentialsCache.put(userType, credentials);
                return credentials;
            }
        }

        credentials = getCredentialsFromEncryptedConfig(userType);
        logger.info("Using encrypted credentials from config for: {}", userType);
        credentialsCache.put(userType, credentials);

        return credentials;
    }

    private static Credentials getCredentialsFromEnvironment(UserType userType) {
        String prefix = userType.name().toUpperCase();
        String username = System.getenv(prefix + "_USERNAME");
        String password = System.getenv(prefix + "_PASSWORD");

        // Also check system properties as fallback
        if (username == null) {
            username = System.getProperty(userType.getValue() + ".username");
        }
        if (password == null) {
            password = System.getProperty(userType.getValue() + ".password");
        }

        if (username != null && password != null) {
            logger.debug("Found credentials in environment for: {}", userType);
            return new Credentials(username, password, userType);
        }

        logger.debug("No environment credentials found for: {}", userType);
        return null;
    }

    private static Credentials getCredentialsFromLocalFile(UserType userType) {
        try {
            String path = String.format("credentials.%s", userType.getValue());
            String username = YamlHelper.getPropertyAsString(LOCAL_CREDENTIALS_FILE, path + ".username");
            String password = YamlHelper.getPropertyAsString(LOCAL_CREDENTIALS_FILE, path + ".password");

            if (username != null && password != null) {
                logger.debug("Found credentials in local file for: {}", userType);
                return new Credentials(username, password, userType);
            }
        } catch (Exception e) {
            logger.debug("Failed to read local credentials file: {}", e.getMessage());
        }

        return null;
    }

    private static Credentials getCredentialsFromEncryptedConfig(UserType userType) {
        try {
            String path = String.format("credentials.%s", userType.getValue());
            String username = YamlHelper.getPropertyAsString(CREDENTIALS_FILE, path + ".username");
            String encryptedPassword = YamlHelper.getPropertyAsString(CREDENTIALS_FILE, path + ".password");

            if (username == null || encryptedPassword == null) {
                throw new RuntimeException("Credentials not found for user type: " + userType);
            }

            String password = EncryptionHelper.decrypt(encryptedPassword);
            logger.debug("Successfully decrypted credentials for: {}", userType);

            return new Credentials(username, password, userType);

        } catch (Exception e) {
            logger.error("Failed to get credentials from config for: {}", userType, e);
            throw new RuntimeException("Failed to retrieve credentials for: " + userType, e);
        }
    }

    private static boolean isLocalCredentialsFileExists() {
        try {
            // Check in resources directory
            File file = new File("src/main/resources/" + LOCAL_CREDENTIALS_FILE);
            boolean exists = file.exists();

            if (!exists) {
                file = new File("src/test/resources/" + LOCAL_CREDENTIALS_FILE);
                exists = file.exists();
            }

            logger.debug("Local credentials file exists: {}", exists);
            return exists;

        } catch (Exception e) {
            logger.debug("Error checking local credentials file: {}", e.getMessage());
            return false;
        }
    }

    public static void clearCache() {
        logger.info("Clearing credentials cache");
        credentialsCache.clear();
    }

    public static void clearCache(UserType userType) {
        Objects.requireNonNull(userType, "User type cannot be null");
        logger.info("Clearing cache for user type: {}", userType);
        credentialsCache.remove(userType);
    }

    public static boolean hasCredentials(UserType userType) {
        try {
            Credentials creds = getCredentials(userType);
            return creds != null;
        } catch (Exception e) {
            logger.debug("Credentials validation failed for: {}", userType, e);
            return false;
        }
    }

}
