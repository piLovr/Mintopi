package messengerAdapter.messengerStructures;

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
