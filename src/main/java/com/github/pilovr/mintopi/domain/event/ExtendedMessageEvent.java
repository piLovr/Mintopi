package com.github.pilovr.mintopi.domain.event;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.message.CommandMessageProperties;
import com.github.pilovr.mintopi.domain.message.ExtendedMessage;
import com.github.pilovr.mintopi.domain.message.attachment.Attachment;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ExtendedMessageEvent extends RoomEvent {
    private final ExtendedMessage message;
    private final Account sender;
    private CommandMessageProperties commandMessageProperties = null;

    public ExtendedMessageEvent(Client client, String id, Account sender, Room room, Platform platform, Long timestamp, ExtendedMessage message) {
        super(client, id, platform, timestamp, room);
        this.message = message;
        this.sender = sender;
    }

    public ExtendedMessageEvent(Client client, String id, Account sender, Room room, Platform platform, Long timestamp, ExtendedMessage message, CommandMessageProperties commandMessageProperties) {
        super(client, id, platform, timestamp, room);
        this.message = message;
        this.sender = sender;
        this.commandMessageProperties = commandMessageProperties;
    }

    public ExtendedMessageEvent(MessageEvent messageEvent){
        super(messageEvent.getClient(), messageEvent.getId(), messageEvent.getPlatform(), messageEvent.getTimestamp(), messageEvent.getRoom());
        if(!(messageEvent.getMessage() instanceof ExtendedMessage)) {
            throw new IllegalArgumentException("Cannot convert ExtendedMessageEvent to ExtendedMessageEvent");
        }
        this.message = (ExtendedMessage) (messageEvent.getMessage());
        this.sender = messageEvent.getSender();
    }

    public List<byte[]> downloadAttachments(){
        List<Attachment> attachments = this.message.getAttachments();
        List<byte[]> result = new ArrayList<>();
        for(Attachment attachment : attachments){
            byte[] res = this.getClient().downloadMedia(attachment.getDownloadableMedia());
            result.add(res);
            attachment.setDownloadedMedia(res);
        }
        return result;
    }

    public CommandMessageProperties getCommandMessageProperties(boolean lineSplit) {
        if(commandMessageProperties != null){
            return commandMessageProperties;
        }
        commandMessageProperties = lineSplit ? CommandMessageProperties.ofLines(this) : CommandMessageProperties.of(this);
        return commandMessageProperties;
    }
}
