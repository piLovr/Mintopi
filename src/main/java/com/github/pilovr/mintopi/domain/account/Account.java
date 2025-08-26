package com.github.pilovr.mintopi.domain.account;

import com.github.pilovr.mintopi.client.Platform;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public class Account {
    private final Platform platform;
    private final String internalId;
    private final String rawId;
    private final String platformId;
    private String pushName;

    public Account(@NonNull String platformId, Platform platform, String pushName) {
        this.platformId = platformId;
        this.platform = platform;
        this.rawId = extractRawIdFromPlatformId(platformId, platform);
        this.internalId = buildInternalIdFromPlatformId(platformId, platform);
        if(pushName != null) {
            this.pushName = removeNonAsciiCharacters(pushName);
        }else{
            this.pushName = null;
        }
    }
    public String removeNonAsciiCharacters(String input) {
        //Ony keep A-Z, a-z, 0-9 and basic punctuation
        return input.replaceAll("[^\\x20-\\x7E]", "");
    }

    public void setPushName(@NonNull String pushName) {
        this.pushName = removeNonAsciiCharacters(pushName);
    }

    public static String extractRawIdFromPlatformId(String platformId, Platform platform) {
        //Discord IDs are numeric
        //Telegram usernames start with @
        //WhatsApp IDs are phone numbers
        //Signal IDs are phone numbers
        //Matrix IDs start with @ and have a domain
        return switch (platform){
            case DISCORD -> platformId.replaceAll("[^0-9]", ""); //Discord IDs are numeric
            case TELEGRAM -> platformId.replaceAll("@", ""); //Telegram usernames start with @
            case WHATSAPP -> platformId.replaceAll("[^0-9]", ""); //WhatsApp IDs are phone numbers
            case SIGNAL -> platformId.replaceAll("[^0-9]", ""); //Signal IDs are phone numbers
            case MATRIX -> platformId.replaceAll("@|:.*", ""); //Matrix IDs start with @ and have a domain
            default -> platformId;
        };
    }
    public static String buildInternalIdFromPlatformId(String platformId, Platform platform){
        return extractRawIdFromPlatformId(platformId, platform) + "@" + platform.toString() + "a";
    }
    public String getPlatformMention() {
        switch (platform) {
            case WHATSAPP, TELEGRAM, MATRIX, SIGNAL -> {
                return "@" + rawId;
            }
            case DISCORD -> {
                return "<@" + rawId + ">";
            }
            default -> {
                return rawId;
            }
        }
    }
}
