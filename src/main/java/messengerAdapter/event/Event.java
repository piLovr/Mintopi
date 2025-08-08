package messengerAdapter.event;

import com.piLovr.messengerAdapters.messengerStructures.Platform;

import java.sql.Timestamp;

public abstract class Event {
    private String id;
    private Platform platform;
    private Object payload;
    private Timestamp timestamp; //TODO
    public Event(String id, Platform platform, Object payload) {
        this.id = id;
        this.platform = platform;
        this.payload = payload;
    }

    public Event(String id, Platform platform) {
        // Default constructor
    }
}
