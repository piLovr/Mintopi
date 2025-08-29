package com.github.pilovr.mintopi.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pilovr.mintopi.config.MintopiProperties;
import com.github.pilovr.mintopi.error.LanguageLoadException;
import com.github.pilovr.mintopi.subscriber.Response;
import com.github.pilovr.mintopi.subscriber.command.CommandResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class JsonTextProvider {
    //      Language    Command     Key         CommandResponse
    private Map<String, Map<String, Map<String, CommandResponse>>> commandResponses = new HashMap<>();

    //      Language    Command     Key         Response
    private Map<String, Map<String, Map<String, Response>>> eventResponses = new HashMap<>(); // "event" -> "key" -> Response

    //      Language    Command     CommandResponse
    private Map<String, Map<String, CommandResponse>> secretCommandResponses = new HashMap<>(); // "miniCommand" -> CommandResponse

    //      Language    Command     CommandResponse
    private Map<String, Map<String, CommandResponse>> miniCommandResponses = new HashMap<>(); // "miniCommand" -> CommandResponse


    //      Language    Key         Value
    private Map<String, Map<String, String>> otherValues;

    private final Map<String, String> commandCategories = new HashMap<>(); // "command" -> "category"
    private final Map<String, String> miniCommandCategories = new HashMap<>(); // "miniCommand" -> "shortDescription"
    private final Map<String, String> secretCommandCategories = new HashMap<>(); // "miniCommand" -> "shortDescription"

    private static String jsonFolderPath;
    private static Map<String, JsonNode> jsonTexts;

    private static final Logger log = LoggerFactory.getLogger(JsonTextProvider.class);

    public JsonTextProvider(MintopiProperties properties) {
        jsonFolderPath = properties.getInternationalizationFolderPath();
    }

    public static String getText(String jsonName, String... path) {
        JsonNode root = jsonTexts.get(jsonName);
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

    public CommandResponse getCommandResponse(String language, String commandName, String key) {
        Map<String, Map<String, CommandResponse>> langCommands = commandResponses.get(language);
        if (langCommands == null) return null;

        Map<String, CommandResponse> commandMap = langCommands.get(commandName);
        if (commandMap == null) return null;

        return commandMap.get(key);
    }

    public Response getEventResponse(String language, String eventName, String key) {
        Map<String, Map<String, Response>> langEvents = eventResponses.get(language);
        if (langEvents == null) return null;

        Map<String, Response> eventMap = langEvents.get(eventName);
        if (eventMap == null) return null;

        return eventMap.get(key);
    }

    public CommandResponse getMiniCommandResponse(String language, String miniCommandName) {
        Map<String, CommandResponse> langMiniCommands = secretCommandResponses.get(language);
        if (langMiniCommands == null) return null;

        return langMiniCommands.get(miniCommandName);
    }

    public String getOtherValue(String language, String key) {
        Map<String, String> langValues = otherValues.get(language);
        if (langValues == null) return null;

        return langValues.get(key);
    }







    @PostConstruct
    private void init() {
        loadUserLanguages();
    }
    private void loadUserLanguages() throws LanguageLoadException {
        ObjectMapper objectMapper = new ObjectMapper();
        Path langPath = Paths.get(jsonFolderPath);
        if (!Files.exists(langPath)) {
            throw new LanguageLoadException("i18n path does not exist: " + jsonFolderPath);
        }

        try (Stream<Path> paths = Files.list(langPath)) {
            paths.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try {
                    JsonNode node = objectMapper.readTree(path.toFile());
                    String lang = path.getFileName().toString().replace(".json", "");
                    jsonTexts.put(lang, node);
                } catch (IOException e) {
                    throw new LanguageLoadException("Failed to load language file: " + path.getFileName() + " - " + e.getMessage());
                }
            });
        } catch (IOException e) {
            throw new LanguageLoadException(e.getMessage());
        }
    }
    private void parseJson(String language, JsonNode userJson) {
        /*
        exampleKey: //demonstrate CommandResult
            reaction: text
            long: text
            short: text

        json Structure
        payloadResponses:
            commands:
                header: text //commandHeader, simple text. parse if present, always located in "commands"
                footer: text //same es header
                _games: //category folder, since it has "_"
                    pet: //command, since it has no "_"
                        key1: CommandResponse //key since it is in a command, Result is a CommandResponse as shown above
                    pet hunt:
                        key1: CommandResponse
                        key2: CommandResponse
                _exampleCategory:
                    _exampleSubCategory: //Categories can be in other Categories.
                        exampleSubCommand:
                            key1: CommandResponse
                            key2: CommandResponse

            miniCommands: //miniCommands have no keys and are stored seperately.
                _exampleCategory: //they can still have categories.
                    test: CommandResponse
            event: //events have no Categories, but can have keys.
                promote:
                    key1: Response
                    key2: Response
         */

        commandResponses.put(language, new HashMap<>()); // "command" -> "key" -> CommandResponse
        eventResponses.put(language, new HashMap<>());
        secretCommandResponses.put(language, new HashMap<>());
        otherValues.put(language, new HashMap<>());

        // Parse JSON
        if (userJson.has("payloadResponses")) {
            JsonNode payloadResponses = userJson.get("payloadResponses");

            // Parse commands with nested categories
            if (payloadResponses.has("commands")) {
                JsonNode commandsNode = payloadResponses.get("commands");
                parseCommandsRecursively(language, commandsNode, "");
            }

            // Parse miniCommands with nested categories
            if (payloadResponses.has("miniCommands")) {
                JsonNode miniCommandsNode = payloadResponses.get("miniCommands");
                parseMiniCommandsRecursively(language, miniCommandsNode, "");
            }

            if(payloadResponses.has("secretCommands")){
                JsonNode secretCommandsNode = payloadResponses.get("secretCommands");
                parseSecretCommandsRecursively(language, secretCommandsNode, "");
            }

            // Parse events
            if (payloadResponses.has("event")) {
                JsonNode eventsNode = payloadResponses.get("event");
                eventsNode.fieldNames().forEachRemaining(event -> {
                    JsonNode eventNode = eventsNode.get(event);
                    if (eventNode.isObject()) {
                        eventNode.fieldNames().forEachRemaining(key -> {
                            JsonNode keyNode = eventNode.get(key);
                            if (keyNode.has("short") && keyNode.has("long")) {
                                String shortVariant = keyNode.get("short").asText();
                                String longVariant = keyNode.get("long").asText();
                                Response response = new Response(shortVariant, longVariant);
                                eventResponses.get(language).computeIfAbsent(event, k -> new HashMap<>()).put(key, response);
                            } else {
                                log.warn("Invalid Response structure for event '{}', key '{}'", event, key);
                            }
                        });
                    }
                });
            }
        }
        //put other keys in otherValues
        userJson.fieldNames().forEachRemaining(field -> {
            if (!field.equals("payloadResponses")) {
                JsonNode fieldNode = userJson.get(field);
                if (fieldNode.isTextual()) {
                    otherValues.get(language).put(field, fieldNode.asText());
                }
            }
        });

    }

    private void parseCommandsRecursively(String language, JsonNode node, String prefix) {
        node.fieldNames().forEachRemaining(field -> {
            JsonNode fieldNode = node.get(field);

            if (field.startsWith("_")) {
                // This is a category - process recursively
                String categoryName = field.substring(1); // Remove the underscore
                String newPrefix = prefix.isEmpty() ? categoryName : prefix + "." + categoryName;
                parseCommandsRecursively(language, fieldNode, newPrefix);
            } else if (fieldNode.isTextual()) {
                // This is a header or footer or whatever, just a simple text
                otherValues.get(language).put(field, fieldNode.asText());
            } else {
                // This is a command
                commandCategories.put(field, prefix);

                if (fieldNode.isObject()) {
                    fieldNode.fieldNames().forEachRemaining(key -> {
                        JsonNode keyNode = fieldNode.get(key);
                        if (keyNode.has("reaction") && keyNode.has("short") && keyNode.has("long")) {
                            String reaction = keyNode.get("reaction").asText();
                            String shortVariant = keyNode.get("short").asText();
                            String longVariant = keyNode.get("long").asText();
                            CommandResponse commandResponse = new CommandResponse(reaction, shortVariant, longVariant);
                            commandResponses.get(language).computeIfAbsent(field, k -> new HashMap<>()).put(key, commandResponse);
                        } else {
                            log.warn("Invalid CommandResponse structure for command '{}', key '{}'", field, key);
                        }
                    });
                }
            }
        });
    }

    private void parseMiniCommandsRecursively(String language, JsonNode node, String prefix) {
        node.fieldNames().forEachRemaining(field -> {
            JsonNode fieldNode = node.get(field);

            if (field.startsWith("_")) {
                // This is a category - process recursively
                String categoryName = field.substring(1); // Remove the underscore
                String newPrefix = prefix.isEmpty() ? categoryName : prefix + "." + categoryName;
                parseMiniCommandsRecursively(language, fieldNode, newPrefix);
            } else {
                // This is a miniCommand
                miniCommandCategories.put(field, prefix);

                if (fieldNode.has("reaction") && fieldNode.has("short") && fieldNode.has("long")) {
                    String reaction = fieldNode.get("reaction").asText();
                    String shortVariant = fieldNode.get("short").asText();
                    String longVariant = fieldNode.get("long").asText();
                    CommandResponse commandResponse = new CommandResponse(reaction, shortVariant, longVariant);
                    miniCommandResponses.get(language).put(field, commandResponse);
                } else {
                    log.warn("Invalid CommandResponse structure for miniCommand '{}'", field);
                }
            }
        });
    }

    private void parseSecretCommandsRecursively(String language, JsonNode node, String prefix) {
        node.fieldNames().forEachRemaining(field -> {
            JsonNode fieldNode = node.get(field);

            if (field.startsWith("_")) {
                // This is a category - process recursively
                String categoryName = field.substring(1); // Remove the underscore
                String newPrefix = prefix.isEmpty() ? categoryName : prefix + "." + categoryName;
                parseSecretCommandsRecursively(language, fieldNode, newPrefix);
            } else {
                // This is a secretCommand
                secretCommandCategories.put(field, prefix);

                if (fieldNode.has("reaction") && fieldNode.has("short") && fieldNode.has("long")) {
                    String reaction = fieldNode.get("reaction").asText();
                    String shortVariant = fieldNode.get("short").asText();
                    String longVariant = fieldNode.get("long").asText();
                    CommandResponse commandResponse = new CommandResponse(reaction, shortVariant, longVariant);
                    secretCommandResponses.get(language).put(field, commandResponse);
                } else {
                    log.warn("Invalid CommandResponse structure for secretCommand '{}'", field);
                }
            }
        });
    }
}
