package com.github.pilovr.mintopi.service.language;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.pilovr.mintopi.config.MintopiProperties;
import com.github.pilovr.mintopi.error.ErrorMessageProvider;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class LanguageProvider {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(\\w+)\\}");
    @Getter
    protected final Map<String, Map<String, Object>> languageData = new ConcurrentHashMap<>();
    @Getter
    private String currentLanguage = "en";
    MintopiProperties mintopiProperties;

    @Autowired
    public LanguageProvider(MintopiProperties mintopiProperties) {
        this.mintopiProperties = mintopiProperties;
        loadLanguages(mintopiProperties.getInternationalizationFolderPath());
    }

    /**
     * Load all language files from the specified directory
     */
    public void loadLanguages(String directoryPath) {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            paths.filter(path -> path.toString().endsWith(".json"))
                    .forEach(this::loadLanguageFile);
        }catch(IOException e){
            throw ErrorMessageProvider.languageLoadException("en", e.getMessage());

        }
    }

    /**
     * Get text by key path with optional parameters
     */
    public String getText(String keyPath, Map<String, String> params) {
        String text = getTextByPath(keyPath);
        if (text == null) return keyPath;

        if (params != null && !params.isEmpty()) {
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                String paramName = matcher.group(1);
                String replacement = params.getOrDefault(paramName, matcher.group(0));
                matcher.appendReplacement(result, replacement.replace("$", "\\$"));
            }
            matcher.appendTail(result);
            return result.toString();
        }

        return text;
    }

    public String getText(String language, String keyPath, Object... keyValuePairs) {
        String text = getTextByPath(keyPath);
        if (text == null) return keyPath;

        Map<String, String> params = params(keyValuePairs);

        if (params != null && !params.isEmpty()) {
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                String paramName = matcher.group(1);
                String replacement = params.getOrDefault(paramName, matcher.group(0));
                matcher.appendReplacement(result, replacement.replace("$", "\\$"));
            }
            matcher.appendTail(result);
            return result.toString();
        }

        return text;
    }

    /**
     * Get text by key path
     */
    public String getText(String keyPath) {
        return getText(keyPath, null);
    }

    /**
     * Parse keyPath and retrieve text
     */
    protected String getTextByPath(String keyPath) {
        String[] parts = keyPath.split("\\.");
        Map<String, Object> current = languageData.get(currentLanguage);
        if (current == null) return null;

        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (!(next instanceof Map)) {
                return null;
            }
            current = (Map<String, Object>) next;
        }

        Object result = current.get(parts[parts.length - 1]);
        return result != null ? result.toString() : null;
    }

    protected String getTextByPath(String language, String keyPath) {
        String[] parts = keyPath.split("\\.");
        Map<String, Object> current = languageData.get(language);
        if (current == null) return null;

        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (!(next instanceof Map)) {
                return null;
            }
            current = (Map<String, Object>) next;
        }

        Object result = current.get(parts[parts.length - 1]);
        return result != null ? result.toString() : null;
    }

    private void loadLanguageFile(Path path) {
        try {
            String filename = path.getFileName().toString();
            String language = filename.substring(0, filename.lastIndexOf('.'));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = mapper.readValue(path.toFile(),
                    new TypeReference<Map<String, Object>>() {});
            languageData.put(language, data);
        } catch (IOException e) {
            // Log error
        }
    }

    /**
     * Create parameter map for placeholder substitution
     */
    public static Map<String, String> params(Object... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Parameters must be provided as key-value pairs");
        }

        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            params.put(keyValuePairs[i].toString(), keyValuePairs[i+1].toString());
        }
        return params;
    }




    /**
     * Validates that all defined keys exist in the loaded language files
     * @return Map of languages with their missing keys
     */
    public Map<String, List<String>> validateKeys() {
        Map<String, List<String>> missingKeys = new HashMap<>();
        List<String> allDefinedKeys = getAllDefinedKeys();

        // Check each language
        for (String language : languageData.keySet()) {
            List<String> missing = new ArrayList<>();
            for (String key : allDefinedKeys) {
                if (getTextByPath(key) == null) {
                    missing.add(key);
                }
            }

            if (!missing.isEmpty()) {
                missingKeys.put(language, missing);
            }
        }

        return missingKeys;
    }

    /**
     * Extract all keys defined in the Keys class using reflection
     */
    private List<String> getAllDefinedKeys() {
        List<String> keys = new ArrayList<>();
        collectKeysFromClass(LanguageKeys.class, "", keys);
        return keys;
    }

    private void collectKeysFromClass(Class<?> clazz, String prefix, List<String> keys) {
        // Get all declared fields
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == String.class) {
                try {
                    String value = (String) field.get(null);
                    keys.add(value);
                } catch (IllegalAccessException e) {
                    // Skip inaccessible field
                }
            }
        }

        // Process nested classes
        for (Class<?> nestedClass : clazz.getDeclaredClasses()) {
            collectKeysFromClass(nestedClass, prefix + nestedClass.getSimpleName() + ".", keys);
        }
    }

    /**
     * Generate a skeleton JSON file with all defined keys but no values
     * @param outputPath Path to save the skeleton file
     * @throws IOException If file writing fails
     */
    public void generateSkeletonJson(String outputPath) throws IOException {
        Map<String, Object> skeleton = buildSkeletonStructure();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(outputPath).toFile(), skeleton);
    }

    /**
     * Builds a hierarchical map structure from all defined keys
     */
    private Map<String, Object> buildSkeletonStructure() {
        Map<String, Object> root = new HashMap<>();
        List<String> allKeys = getAllDefinedKeys();

        for (String keyPath : allKeys) {
            String[] parts = keyPath.split("\\.");
            Map<String, Object> current = root;

            // Navigate through the path parts
            for (int i = 0; i < parts.length - 1; i++) {
                current.putIfAbsent(parts[i], new HashMap<String, Object>());
                current = (Map<String, Object>) current.get(parts[i]);
            }

            // Add the leaf node with an empty value
            current.put(parts[parts.length - 1], "");
        }

        return root;
    }
}