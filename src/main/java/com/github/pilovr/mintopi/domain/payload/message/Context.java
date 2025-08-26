package com.github.pilovr.mintopi.domain.payload.message;

import com.github.pilovr.mintopi.domain.account.Account;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder(toBuilder = true) @Getter
public class Context {
    private final List<Account> mentions; //HashSet
    private final Account quotedMessageSender;
    private final MessagePayload quoted;
    private final List<Account> who;
    private boolean usesHideTags = false;

    public Context(List<Account> mentions, Account quotedMessageSender, MessagePayload quoted) {
        this.mentions = mentions;
        this.quotedMessageSender = quotedMessageSender;
        this.quoted = quoted;
        this.who = mentions != null ? mentions : (quotedMessageSender != null ? List.of(quotedMessageSender) : null);
    }

    public Context(List<Account> mentions, Account quotedMessageSender, MessagePayload quoted, String text) {
        this.mentions = mentions;
        this.quotedMessageSender = quotedMessageSender;
        this.quoted = quoted;
        this.who = mentions != null ? mentions : (quotedMessageSender != null ? List.of(quotedMessageSender) : null);
        determineHideTagsUsage(text);
    }
    public boolean hasMention(Account account) {
        return mentions.contains(account);
    }

    public List<Account> getMentionsInText(String text) {
        if (text == null || text.isEmpty() || mentions == null || mentions.isEmpty()) {
            return List.of();
        }
        return mentions.stream().filter(mention -> {
            String mentionTag = mention.getPlatformMention();
            return mentionTag != null && !mentionTag.isEmpty() && text.contains(mentionTag);
        }).toList();
    }

    public boolean determineHideTagsUsage(String text) {
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
