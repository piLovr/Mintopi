package messengerAdapter.message;

import com.piLovr.messengerAdapters.messengerStructures.Account;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
public class MessageBuilder {
    private String text;
    private List<Account> mentions;
    private Message quoted;
    private List<Attachment> attachments;

    public Message build() {
        return new Message(this);
    }

    public Stream<Attachment> attachmentsStream() {
        return attachments == null ? Stream.empty() : attachments.stream();
    }

    public void addAttachment(Attachment attachment) {
        if (attachments == null) {
            attachments = List.of(attachment);
        } else {
            attachments.add(attachment);
        }
    }

    public void addMention(Account account) {
        if (mentions == null) {
            mentions = List.of(account);
        } else {
            mentions.add(account);
        }
    }
}
