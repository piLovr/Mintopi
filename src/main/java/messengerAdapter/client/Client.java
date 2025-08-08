package messengerAdapter.client;


import com.piLovr.messengerAdapters.listener.Listener;
import com.piLovr.messengerAdapters.message.Message;
import com.piLovr.messengerAdapters.messengerStructures.Room;

public interface Client {
    void connect();
    void disconnect();
    Message sendMessage(Room room, String text);
    Message sendMessage(Room room, Message message);

    void addListener(Listener listener);
    void removeListener(Listener listener);

    String getAlias();
    boolean isConnected();
}
