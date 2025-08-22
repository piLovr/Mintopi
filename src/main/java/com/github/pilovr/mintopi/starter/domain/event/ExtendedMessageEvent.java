package com.github.pilovr.mintopi.starter.domain.event;

import com.github.pilovr.mintopi.starter.domain.account.Account;
import com.github.pilovr.mintopi.starter.domain.common.Client;
import com.github.pilovr.mintopi.starter.domain.common.Platform;
import com.github.pilovr.mintopi.starter.domain.message.ExtendedMessage;
import com.github.pilovr.mintopi.starter.domain.message.Message;
import com.github.pilovr.mintopi.starter.domain.message.attachment.Attachment;
import com.github.pilovr.mintopi.starter.domain.room.Room;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ExtendedMessageEvent extends RoomEvent {
    private final ExtendedMessage message;

    public ExtendedMessageEvent(Client client, String id, Account sender, Room room, Platform platform, Long timestamp, ExtendedMessage message) {
        super(client, id, platform, timestamp, sender, room);
        this.message = message;
    }

    public ExtendedMessageEvent(MessageEvent messageEvent){
        super(messageEvent.getClient(), messageEvent.getId(), messageEvent.getPlatform(), messageEvent.getTimestamp(),  messageEvent.getSender(), messageEvent.getRoom());
        if(!(messageEvent.getMessage() instanceof ExtendedMessage)) {
            throw new IllegalArgumentException("Cannot convert ExtendedMessageEvent to ExtendedMessageEvent");
        }
        this.message = (ExtendedMessage) (messageEvent.getMessage());
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
}
