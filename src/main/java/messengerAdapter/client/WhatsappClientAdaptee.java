package messengerAdapter.client;

import backup.whatsapp.QrHandler;
import com.piLovr.messengerAdapters.listener.Listener;
import com.piLovr.messengerAdapters.listener.WhatsappInternalListener;
import com.piLovr.messengerAdapters.message.Message;
import com.piLovr.messengerAdapters.messengerStructures.Room;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.api.WhatsappWebHistoryPolicy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WhatsappClientAdaptee implements Client{
    protected final List<Listener> listeners = new CopyOnWriteArrayList<>();
    protected String alias;
    protected Whatsapp client;
    protected WhatsappInternalListener internalListener;
    public WhatsappClientAdaptee(String alias) {
        this.alias = alias;
        internalListener = new WhatsappInternalListener(listeners);
        // Initialize the client, e.g., set up connections, listeners, etc.
    }
    @Override
    public void connect() {
        client = Whatsapp.builder()
                .webClient()
                .newConnection(alias)
                .historySetting(WhatsappWebHistoryPolicy.discard(false))
                .unregistered(QrHandler.toTerminal());

        client.addListener(internalListener)
                .connect();
        new Thread(() -> client.waitForDisconnection()).start();
    }

    @Override
    public void disconnect() {

    }

    @Override
    public Message sendMessage(Room room, String text) {
        return null;
    }

    @Override
    public Message sendMessage(Room room, Message message) {
        return null;
    }

    @Override
    public void addListener(Listener listener) {

    }

    @Override
    public void removeListener(Listener listener) {

    }

    @Override
    public String getAlias() {
        return "";
    }

    @Override
    public boolean isConnected() {
        return false;
    }
}
