package com.piLovr.messenger_interop_starter.domain.message.types;

public enum MessageType {
    EMPTY,

    TEXT,
    TEXT_WITH_ATTACHMENTS,
    CONTACT,
    LOCATION,
    CONTACT_ARRAY,
    LIVE_LOCATION,
    ROOM_INVITE,
    LIST,
    LIST_RESPONSE,
    BUTTONS,
    BUTTONS_RESPONSE,
    REACTION,
    POLL_CREATION,
    POLL_UPDATE,
    REQUEST_PHONE_NUMBER,
    STICKER_SYNC,
    NEWSLETTER_ADMIN_INVITE,

    CALENDAR_EVENT,

    UNKNOWN,
}
