package com.github.pilovr.mintopi.starter.domain.message;

import com.github.pilovr.mintopi.starter.domain.account.Account;
import com.github.pilovr.mintopi.starter.domain.message.attachment.Attachment;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MessageBuilder {
    private String text;
    private List<Account> mentions;
    private ExtendedMessage quoted;
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

    public ExtendedMessage build() {
        switch (type) {
            /*case POLL:
                return new PollMessage(this);

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