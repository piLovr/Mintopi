package com.github.pilovr.mintopi.client.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pilovr.mintopi.config.MintopiProperties;
import com.github.pilovr.mintopi.error.LanguageLoadException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class I18nProvider {
    private static final Logger log = LoggerFactory.getLogger(I18nProvider.class);
    private static String i18nPath;
    private ObjectMapper objectMapper;
    private static Map<String, JsonNode> i18nTexts;
    private JsonNode template;

    public I18nProvider(MintopiProperties properties) {
        i18nPath = properties.getInternationalizationFolderPath();

    }

    public static String getText(String lang, String... path) {
        JsonNode root = i18nTexts.get(lang);
        if (root == null) return null;

        for (String p : path) {
            if (root.has(p)) {
                root = root.get(p);
            } else {
                return null;
            }
        }
        return root.isTextual() ? root.asText() : root.toString();
    }


    @PostConstruct
    private void init() {
        loadTemplate();
        loadUserLanguages();
        validateAndPatchLanguages();
    }
    private void loadTemplate() throws LanguageLoadException {
        try (InputStream resourceAsStream = getClass().getResourceAsStream("/LanguageTemplate.json")) {
            if (resourceAsStream == null) throw new LanguageLoadException("Missing LanguageTemplate.json in resources!");
            this.template = objectMapper.readTree(resourceAsStream);
        }catch(IOException e){
            throw new LanguageLoadException("Failed to load language template: " + e.getMessage());
        }
    }

    private void loadUserLanguages() throws LanguageLoadException {
        Path langPath = Paths.get(i18nPath);
        if (!Files.exists(langPath)) {
            throw new LanguageLoadException("i18n path does not exist: " + i18nPath);
        }

        try (Stream<Path> paths = Files.list(langPath)) {
            paths.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try {
                    JsonNode node = objectMapper.readTree(path.toFile());
                    String lang = path.getFileName().toString().replace(".json", "");
                    i18nTexts.put(lang, node);
                } catch (IOException e) {
                    throw new LanguageLoadException("Failed to load language file: " + path.getFileName() + " - " + e.getMessage());
                }
            });
        } catch (IOException e) {
            throw new LanguageLoadException(e.getMessage());
        }
    }
    private void validateAndPatchLanguages() {
        for (Map.Entry<String, JsonNode> entry : i18nTexts.entrySet()) {
            String lang = entry.getKey();
            JsonNode userJson = entry.getValue();

            JsonNode patched = validateAndPatchLanguagesRecursive(template, userJson);

            // overwrite in memory
            i18nTexts.put(lang, patched);

            // write back pretty JSON
            Path langFile = Paths.get(i18nPath, lang + ".json");
            try {
                objectMapper.writeValue(langFile.toFile(), patched);
            }catch(IOException e){
                throw new LanguageLoadException("Failed to write patched language file: " + langFile + " - " + e.getMessage());
            }
        }
    }

    private JsonNode validateAndPatchLanguagesRecursive(JsonNode template, JsonNode userJson) {
        ObjectNode result = objectMapper.createObjectNode();

        template.fieldNames().forEachRemaining(field -> {
            JsonNode templateValue = template.get(field);
            JsonNode userValue = userJson.get(field);

            if (userValue == null) {
                log.warn("Missing key '{}' → adding placeholder", field);
                result.set(field, templateValue.deepCopy());
            }
            if(field.equals("commands")){
                if (templateValue.isObject()) {
                    JsonNode exampleCommand = templateValue.findValue("example");
                    userValue.fieldNames().forEachRemaining(command -> {
                        result.set(field, validateAndPatchLanguagesRecursive(exampleCommand, userValue.get(command))); //todo geht das???
                    });
                }
            }else {
                if (templateValue.isObject()) {
                    result.set(field, validateAndPatchLanguagesRecursive(templateValue, userValue));
                } else {
                    result.set(field, userValue);
                }
            }
        });

        return result;
    }
}
