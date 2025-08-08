package messengerAdapter.event;

import com.piLovr.messengerAdapters.message.Message;
import com.piLovr.messengerAdapters.messengerStructures.Account;
import com.piLovr.messengerAdapters.messengerStructures.Platform;
import com.piLovr.messengerAdapters.messengerStructures.Room;

public class MessageEvent extends RoomEvent{
    private Message message;

    public MessageEvent(String id, Account sender, Room room, Platform platform, Object payload, Message message) {
        super(id, sender, room, platform, payload);

    }
}
