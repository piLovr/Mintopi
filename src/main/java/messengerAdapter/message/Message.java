package messengerAdapter.message;

import com.piLovr.messengerAdapters.event.Event;
import com.piLovr.messengerAdapters.event.RoomEvent;
import com.piLovr.messengerAdapters.messengerStructures.Account;
import lombok.Getter;

import java.util.List;

@Getter
public class Message {
    private String text;
    private List<Account> mentions;
    private Message quoted;
    private List<Attachment> attachments;

    public Message(String text, List<Account> mentions, Message quoted, List<Attachment> attachments) {
        this.text = text;
        this.mentions = mentions;
        this.quoted = quoted;
        this.attachments = attachments;
    }

    Message(MessageBuilder builder) {
        this.text = builder.getText();
        this.mentions = builder.getMentions();
        this.quoted = builder.getQuoted();
        this.attachments = builder.getAttachments();
    }
}