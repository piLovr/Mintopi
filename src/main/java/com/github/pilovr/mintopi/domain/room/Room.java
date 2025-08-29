package com.github.pilovr.mintopi.domain.room;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.client.Platform;
import lombok.AccessLevel;
import lombok.Getter;
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

    @Setter(AccessLevel.NONE) private Boolean isOneToOneChat = null; //A room that is actually a 1:1 chat

    private String name;
    private Map<Account, Set<String>> membersPlusPermissions;
    private String description;
    private Account founder;
    private Long ephemeralExpiration;

    public Room(String platformId, Platform platform, String name, boolean isOneToOneChat) {
        this.platformId = platformId;
        this.platform = platform;
        this.isOneToOneChat = isOneToOneChat;
        this.rawId = extractRawIdFromPlatformId(platformId, platform);
        this.internalId = buildInternalIdFromPlatformId(platformId, platform);

        if(name != null) {
            this.name = removeNonAsciiCharacters(name);
        }else{
            this.name = null;
        }
    }

    private String removeNonAsciiCharacters(String input) {
        //Ony keep A-Z, a-z, 0-9 and basic punctuation
        return input.replaceAll("[^\\x20-\\x7E]", "");
    }

    public static String extractRawIdFromPlatformId(String platformId, Platform platform) {
        return switch (platform){
            case WHATSAPP -> platformId.replaceAll("[^0-9]", ""); //WhatsApp IDs are numbers
            case TELEGRAM -> platformId.startsWith("-") ? platformId.substring(1) : platformId; //Telegram group IDs start with "-"
            case DISCORD -> platformId; //Discord IDs are alphanumeric, no change needed
            default -> platformId;
        };
    }

    public static String buildInternalIdFromPlatformId(String platformId, Platform platform){
        return extractRawIdFromPlatformId(platformId, platform) + "@" + platform.toString() + "r";
    }

    public void addMember(Account account, Set<String> memberPermissions) {
        if(this.membersPlusPermissions == null) {
            this.membersPlusPermissions = new java.util.HashMap<>();
        }
        this.membersPlusPermissions.put(account, memberPermissions);
    }
    public void removeMember(Account account) {
        this.membersPlusPermissions.remove(account);
    }

    public void updateMember(Account account, Set<String> memberPermissions) {
        this.membersPlusPermissions.put(account, memberPermissions);
    }

    public void addPermission(Account account, String permission) {
        if(this.membersPlusPermissions == null || !this.membersPlusPermissions.containsKey(account)) {
            return;
        }
        this.membersPlusPermissions.get(account).add(permission);
    }

    public void removePermission(Account account, String permission) {
        if(this.membersPlusPermissions == null || !this.membersPlusPermissions.containsKey(account)) {
            return;
        }
        this.membersPlusPermissions.get(account).remove(permission);
    }

    public boolean hasPermission(Account account, String requiredPermission) {
        if(this.membersPlusPermissions == null || !this.membersPlusPermissions.containsKey(account)) {
            return false;
        }
        Set<String> userPermissions = this.membersPlusPermissions.get(account);
        return userPermissions.contains(requiredPermission);
    }

    public boolean hasAnyPermission(Account account, Set<String> requiredPermissions) {
        if(this.membersPlusPermissions == null || !this.membersPlusPermissions.containsKey(account)) {
            return false;
        }
        Set<String> userPermissions = this.membersPlusPermissions.get(account);
        for(String permission : requiredPermissions) {
            if(userPermissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEphemeralExpiration(){
        return ephemeralExpiration == null || ephemeralExpiration == 0;
    }
}
