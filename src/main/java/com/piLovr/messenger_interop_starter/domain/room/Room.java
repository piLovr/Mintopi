package com.piLovr.messenger_interop_starter.domain.room;

import com.piLovr.messenger_interop_starter.domain.account.Account;
import com.piLovr.messenger_interop_starter.domain.common.Platform;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

/**
 * A room describes a place, where members send Broadcast messages to every member of the room.
 * Also, referred as "Group" or "Channel"
 */
@Getter
public class Room {
    private String id;
    private Platform platform;
    private String name;
    private Map<Account, Set<String>> members;
}
