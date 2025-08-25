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
public class ExtendedMessageEvent<R extends Room, A extends Account> extends MessageEvent<ExtendedMessage, R, A> {
    private CommandMessageProperties commandMessageProperties = null;

    public ExtendedMessageEvent(Client client, String id, A sender, R room, Platform platform, Long timestamp, ExtendedMessage message) {
        super(client, id, platform, timestamp, sender, room, message);
    }

    public ExtendedMessageEvent(MessageEvent<ExtendedMessage, R, A> messageEvent){
        super(messageEvent.getClient(), messageEvent.getId(), messageEvent.getPlatform(), messageEvent.getTimestamp(), messageEvent.getSender(), messageEvent.getRoom(), messageEvent.getMessage());
    }

    public List<byte[]> downloadAttachments(){
        List<Attachment> attachments = this.getMessage().getAttachments();
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
