package com.piLovr.messenger_interop_starter.domain.message;

import com.piLovr.messenger_interop_starter.domain.account.Account;
import com.piLovr.messenger_interop_starter.domain.message.attachment.Attachment;
import com.piLovr.messenger_interop_starter.domain.message.types.MessageType;
import com.piLovr.messenger_interop_starter.domain.message.types.PollMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MessageBuilder {
    private String text;
    private List<Account> mentions;
    private Message quoted;
    private List<Attachment> attachments;
    private MessageType type = MessageType.TEXT;

    // Poll-specific fields
    private String question;
    private List<String> options = new ArrayList<>();
    private boolean allowMultipleVotes = false;

    // Location-specific fields
    private double latitude;
    private double longitude;
    private String locationName;

    public MessageBuilder(MessageType type) {
        this.type = type;
    }

    public Message build() {
        switch (type) {
            case POLL:
                return new PollMessage(this);
                /*
            case LOCATION:
                return new LocationMessage(this);
            case TEXT:
            */
            default:
                return new ExtendedMessage(this);
        }
    }

    // Add poll option
    public void addPollOption(String option) {
        if (options == null) {
            options = new ArrayList<>();
        }
        options.add(option);
    }

    // Existing methods...
}