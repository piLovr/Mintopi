package messengerAdapter.message.messageDecoder;

import com.piLovr.messengerAdapters.event.MessageEvent;
import com.piLovr.messengerAdapters.message.Message;
import com.piLovr.messengerAdapters.event.Event;
import com.piLovr.messengerAdapters.message.MessageBuilder;
import it.auties.whatsapp.model.info.MessageInfo;

public class WhatsappEventDecoder extends MultiEventDecoder {
    public WhatsappEventDecoder() {
        this.register(MessageInfo.class, this::decodeMessage);
    }

    private MessageEvent decodeMessage(MessageInfo messageInfo) {
        MessageBuilder builder = new MessageBuilder();

        return new MessageEvent(
                messageInfo.id(),
                null,
                null,
                null,
                messageInfo,
                builder.build()
        );
    }
}
