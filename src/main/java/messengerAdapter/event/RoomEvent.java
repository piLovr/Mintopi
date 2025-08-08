package messengerAdapter.event;

import com.piLovr.messengerAdapters.messengerStructures.Account;
import com.piLovr.messengerAdapters.messengerStructures.Platform;
import com.piLovr.messengerAdapters.messengerStructures.Room;

public abstract class RoomEvent extends Event {
    private Account sender;
    private Room room;

    public RoomEvent(String id, Account sender, Room room) {
        super(id, room.getPlatform());
        this.sender = sender;
        this.room = room;
    }

    public RoomEvent(String id, Account sender, Room room, Platform platform, Object payload) {
        super(id, platform, payload);
        this.sender = sender;
        this.room = room;
    }
}
