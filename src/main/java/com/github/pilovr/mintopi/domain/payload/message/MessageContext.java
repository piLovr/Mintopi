package com.github.pilovr.mintopi.domain.payload.message;

import com.github.pilovr.mintopi.domain.A.A;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.event.EventContext;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder(toBuilder = true) @Getter
public class MessageContext<R extends Room, A extends Account> {
    private final List<A> mentions; //HashSet
    private final EventContext<?,R,A> quoted;

    private final List<A> who;
    @Getter(AccessLevel.NONE)
    private boolean usesHideTags = false;

    public MessageContext(List<A> mentions,  EventContext<?,R,A> quoted) {
        this.mentions = mentions;
        this.quoted = quoted;
        this.who = mentions != null ? mentions : (quoted != null ? List.of(quoted.getSender()) : null);
    }

    public boolean hasMention(A A) {
        return mentions.contains(A);
    }

    public void addMentionsFromText(String text) {
        if (text == null || text.isEmpty() || mentions == null || mentions.isEmpty()) {
            return;
        }
        //todo has to be done via store :(
    }

    public boolean isUsesHideTags(String text) {
        if (text == null || text.isEmpty() || mentions == null || mentions.isEmpty()) {
            usesHideTags = false;
            return false;
        }
        //are mentions in text that are not in the mentions list?
        usesHideTags = mentions.stream().anyMatch(mention -> {
            String mentionTag = mention.getPlatformMention();
            return mentionTag != null && !mentionTag.isEmpty() && !text.contains(mentionTag);
        });
        return usesHideTags;
    }
}
