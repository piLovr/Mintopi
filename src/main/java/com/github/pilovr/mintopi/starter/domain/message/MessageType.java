package com.github.pilovr.mintopi.starter.domain.message;

import java.util.Set;

public enum MessageType {
    TEXT(MessageCategory.EXTENDED),
    TEXT_WITH_ATTACHMENTS(MessageCategory.EXTENDED),

    REACTION(MessageCategory.REACTION),

    CONTACT(MessageCategory.SPECIAL),
    LOCATION(MessageCategory.SPECIAL),
    CONTACT_ARRAY(MessageCategory.SPECIAL),
    LIVE_LOCATION(MessageCategory.SPECIAL),
    ROOM_INVITE(MessageCategory.SPECIAL),
    LIST(MessageCategory.SPECIAL),
    LIST_RESPONSE(MessageCategory.SPECIAL),
    BUTTONS(MessageCategory.SPECIAL),
    BUTTONS_RESPONSE(MessageCategory.SPECIAL),
    POLL_CREATION(MessageCategory.SPECIAL),
    POLL_UPDATE(MessageCategory.SPECIAL),
    REQUEST_PHONE_NUMBER(MessageCategory.SPECIAL),
    STICKER_SYNC(MessageCategory.SPECIAL),
    NEWSLETTER_ADMIN_INVITE(MessageCategory.SPECIAL),

    CALENDAR_EVENT(MessageCategory.SPECIAL),

    UNKNOWN(MessageCategory.NONE),
    EMPTY(MessageCategory.NONE);

    private final MessageCategory messageCategory;

    MessageType(MessageCategory messageCategory) {
        this.messageCategory = messageCategory;
    }

    public MessageCategory getMessageCategory() {
        return messageCategory;
    }

    public static Set<MessageType> getTypesForCategory(MessageCategory category) {
        return switch (category) {
            case REACTION -> Set.of(REACTION);
            case EXTENDED -> Set.of(TEXT, TEXT_WITH_ATTACHMENTS);
            case SPECIAL ->
                    Set.of(CONTACT, LOCATION, CONTACT_ARRAY, LIVE_LOCATION, ROOM_INVITE, LIST, LIST_RESPONSE, BUTTONS, BUTTONS_RESPONSE, POLL_CREATION, POLL_UPDATE, REQUEST_PHONE_NUMBER, STICKER_SYNC, NEWSLETTER_ADMIN_INVITE, CALENDAR_EVENT);
            case NONE -> null;
        };
    }
}
