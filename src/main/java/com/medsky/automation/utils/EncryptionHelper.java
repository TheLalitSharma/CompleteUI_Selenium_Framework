package com.medsky.automation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class EncryptionHelper {
    private static final Logger logger = LoggerFactory.getLogger(EncryptionHelper.class);
    private static final String ALGORITHM = "AES";

    private static final String SECRET_KEY = getSecretKey();

    private EncryptionHelper() {

    }

    public static String encrypt(String plainText) {
        Objects.requireNonNull(plainText, "Plain text cannot be null");

        try {
            logger.debug("Encrypting text");
            SecretKeySpec secretKey = new SecretKeySpec(
                    SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                    ALGORITHM
            );

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            String encrypted = Base64.getEncoder().encodeToString(encryptedBytes);

            logger.debug("Text encrypted successfully");
            return encrypted;

        } catch (Exception e) {
            logger.error("Encryption failed", e);
            throw new RuntimeException("Failed to encrypt text", e);
        }
    }

    public static String decrypt(String encryptedText) {
        Objects.requireNonNull(encryptedText, "Encrypted text cannot be null");

        try {
            logger.debug("Decrypting text");
            SecretKeySpec secretKey = new SecretKeySpec(
                    SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                    ALGORITHM
            );

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            String decrypted = new String(decryptedBytes, StandardCharsets.UTF_8);

            logger.debug("Text decrypted successfully");
            return decrypted;

        } catch (Exception e) {
            logger.error("Decryption failed", e);
            throw new RuntimeException("Failed to decrypt text", e);
        }
    }

    private static String getSecretKey() {
        String key = System.getenv("ENCRYPTION_KEY");

        if (key == null || key.isEmpty()) {
            logger.warn("ENCRYPTION_KEY not set in environment, using default key");
            key = "TestFramework16"; // 16 chars for AES-128
        }

        // Ensure key is exactly 16 characters for AES-128
        if (key.length() < 16) {
            key = String.format("%-16s", key).replace(' ', '0');
        } else if (key.length() > 16) {
            key = key.substring(0, 16);
        }

        return key;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java EncryptionHelper <text-to-encrypt>");
            System.out.println("Example: java EncryptionHelper \"Admin@123\"");
            return;
        }

        String plainText = args[0];
        String encrypted = encrypt(plainText);

        System.out.println("Plain Text: " + plainText);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("\nAdd this to your credentials.yaml file:");
        System.out.println("password: \"" + encrypted + "\"");

        // Verify decryption
        String decrypted = decrypt(encrypted);
        System.out.println("\nVerification - Decrypted: " + decrypted);
        System.out.println("Match: " + plainText.equals(decrypted));
    }
}
