package messengerAdapter.listener;

import com.piLovr.messengerAdapters.event.MessageEvent;
import com.piLovr.messengerAdapters.message.messageDecoder.WhatsappEventDecoder;
import it.auties.whatsapp.api.WhatsappListener;
import it.auties.whatsapp.model.info.MessageInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WhatsappInternalListener implements WhatsappListener {
    private final List<Listener> listeners;
    private WhatsappEventDecoder decoder;

    public WhatsappInternalListener(List<Listener> listeners){
        this.listeners = listeners;
        decoder = new WhatsappEventDecoder();
    }

    @Override
    public void onNewMessage(MessageInfo info) {
        for (Listener listener : listeners) {
            try {
                listener.onMessage((MessageEvent) decoder.decode(info));
            } catch (Exception e) {
                //TODO: Handle exception properly
            }
        }
    }
}
