package com.github.pilovr.mintopi.domain.room;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.client.Platform;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

/**
 * A room describes a place, where members send Broadcast messages to every member of the room.
 * Also, referred as "Group" or "Channel"
 */
@Getter @Setter
public class Room {
    @Setter(AccessLevel.NONE) private final Platform platform;
    @Setter(AccessLevel.NONE) private final String internalId;
    @Setter(AccessLevel.NONE) private final String rawId;
    @Setter(AccessLevel.NONE) private final String platformId;

    private String name;
    private Map<Account, Set<String>> members;
    private String description;
    private Account founder;
    private Long ephemeralExpiration;

    public Room(String platformId, Platform platform, String name) {
        this.platformId = platformId;
        this.platform = platform;
        this.rawId = extractRawIdFromPlatformId(platformId, platform);
        this.internalId = buildInternalIdFromPlatformId(platformId, platform);

        if(name != null) {
            this.name = removeNonAsciiCharacters(name);
        }else{
            this.name = null;
        }
    }

    public String removeNonAsciiCharacters(String input) {
        //Ony keep A-Z, a-z, 0-9 and basic punctuation
        return input.replaceAll("[^\\x20-\\x7E]", "");
    }

    public static String extractRawIdFromPlatformId(String platformId, Platform platform) {
        return switch (platform){
            case WHATSAPP -> platformId.replaceAll("[^0-9]", ""); //WhatsApp IDs are numbers
            default -> platformId;
        };
    }
    public static String buildInternalIdFromPlatformId(String platformId, Platform platform){
        return extractRawIdFromPlatformId(platformId, platform) + "@" + platform.toString() + "r";
    }

    public void addMember(Account account, Set<String> memberIds) {
        if(this.members == null) {
            this.members = new java.util.HashMap<>();
        }
        this.members.put(account, memberIds);
    }
    public void removeMember(Account account) {
        this.members.remove(account);
    }

    public void updateMember(Account account, Set<String> memberIds) {
        this.members.put(account, memberIds);
    }

    public boolean hasRole(Account account, String role) {
        return false;
    }

    public boolean hasEphemeralExpiration(){
        return ephemeralExpiration == null || ephemeralExpiration == 0;
    }
}
