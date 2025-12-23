package com.medsky.automation.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public final class YamlHelper {
    private static final Logger logger = LoggerFactory.getLogger(YamlHelper.class);
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    private YamlHelper() {

    }

    public static <T> T readYaml(String filePath, Class<T> clazz) {
        try {
            logger.info("Reading YAML file: {}", filePath);
            File file = getFile(filePath);
            return yamlMapper.readValue(file, clazz);
        } catch (Exception e) {
            logger.error("Failed to read YAML file: {}", filePath, e);
            throw new RuntimeException("Error reading YAML file: " + filePath, e);
        }
    }

    public static Map<String, Object> readYamlAsMap(String filePath) {
        try {
            logger.info("Reading YAML file as Map: {}", filePath);
            File file = getFile(filePath);
            return yamlMapper.readValue(file, Map.class);
        } catch (Exception e) {
            logger.error("Failed to read YAML file: {}", filePath, e);
            throw new RuntimeException("Error reading YAML file: " + filePath, e);
        }
    }

    public static String getPropertyAsString(String filePath, String propertyPath) {
        Map<String, Object> yamlData = readYamlAsMap(filePath);
        Object value = getNestedValue(yamlData, propertyPath);
        return value != null ? value.toString() : null;
    }

    @SuppressWarnings("unchecked")
    private static Object getNestedValue(Map<String, Object> map, String path) {
        String[] keys = path.split("\\.");
        Object current = map;

        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(key);
                if (current == null) {
                    return null;
                }
            } else {
                return null;
            }
        }
        return current;
    }

    private static File getFile(String filePath) {
        InputStream stream = YamlHelper.class.getClassLoader().getResourceAsStream(filePath);
        if (stream != null) {
            try {
                File tempFile = File.createTempFile("yaml", ".yml");
                tempFile.deleteOnExit();
                java.nio.file.Files.copy(stream, tempFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                return tempFile;
            } catch (Exception e) {
                logger.warn("Failed to create temp file", e);
            }
        }

        File file = new File(filePath);
        if (file.exists()) {
            return file;
        }

        file = new File("src/main/resources/" + filePath);
        if (file.exists()) {
            return file;
        }

        throw new RuntimeException("YAML file not found: " + filePath);
    }


}
