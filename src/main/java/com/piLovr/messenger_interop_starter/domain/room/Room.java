package com.piLovr.messenger_interop_starter.domain.room;

import com.piLovr.messenger_interop_starter.domain.account.Account;
import com.piLovr.messenger_interop_starter.domain.common.Platform;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

/**
 * A room describes a place, where members send Broadcast messages to every member of the room.
 * Also, referred as "Group" or "Channel"
 */
@Getter
public class Room {
    private final String id;
    private final Platform platform;
    @Setter
    private String name;
    private final Map<Account, Set<String>> members;

    public Room(String id, Platform platform, String name, Map<Account, Set<String>> members) {
        this.id = id;
        this.platform = platform;
        this.name = name;
        this.members = members;
    }

    public void addMember(Account account, Set<String> memberIds) {
        this.members.put(account, memberIds);
    }
    public void removeMember(Account account) {
        this.members.remove(account);
    }

    public void updateMember(Account account, Set<String> memberIds) {
        this.members.put(account, memberIds);
    }

    public boolean hasRole(Account account, String role) {
        Set<String> roles = this.members.get(account);
        return roles != null && roles.contains(role);
    }
}
