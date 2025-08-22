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
    @Setter(AccessLevel.NONE)
    private final String id;
    @Setter(AccessLevel.NONE)
    private final Platform platform;

    private String name;
    private Map<Account, Set<String>> members;
    private String description;
    private Account founder;
    private Long ephemeralExpiration;

    public Room(String id, Platform platform, String name) {
        this.id = id;
        this.platform = platform;
        this.name = name;
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
