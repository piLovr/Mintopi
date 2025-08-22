package com.github.pilovr.mintopi.starter.decoder.whatsapp;

import com.github.pilovr.mintopi.starter.domain.message.MessageType;
import com.github.pilovr.mintopi.starter.domain.message.attachment.AttachmentType;
import it.auties.whatsapp.model.message.model.Message;

import java.util.HashMap;

public class WhatsappMessageTypeTranslator {
    public static HashMap<Message.Type, MessageType> translations = new HashMap<>();
    public static HashMap<Message.Type, AttachmentType> attachments = new HashMap<>();
    static {

        /*

        EMPTY,
                TEXT,
                SENDER_KEY_DISTRIBUTION,
                IMAGE,
                CONTACT,
                LOCATION,
                DOCUMENT,
                AUDIO,
                VIDEO,
                PROTOCOL,
                CONTACT_ARRAY,
                HIGHLY_STRUCTURED,
                SEND_PAYMENT,
                LIVE_LOCATION,
                REQUEST_PAYMENT,
                DECLINE_PAYMENT_REQUEST,
                CANCEL_PAYMENT_REQUEST,
                TEMPLATE,
                STICKER,
                GROUP_INVITE,
                TEMPLATE_REPLY,
                PRODUCT,
                DEVICE_SENT,
                DEVICE_SYNC,
                LIST,
                VIEW_ONCE,
                PAYMENT_ORDER,
                LIST_RESPONSE,
                EPHEMERAL,
                PAYMENT_INVOICE,
                BUTTONS,
                BUTTONS_RESPONSE,
                PAYMENT_INVITE,
                INTERACTIVE,
                REACTION,
                INTERACTIVE_RESPONSE,
                NATIVE_FLOW_RESPONSE,
                KEEP_IN_CHAT,
                POLL_CREATION,
                POLL_UPDATE,
                REQUEST_PHONE_NUMBER,
                ENCRYPTED_REACTION,
                CALL,
                STICKER_SYNC,
                EDITED,
                NEWSLETTER_ADMIN_INVITE
         */
        translations.put(Message.Type.TEXT, MessageType.TEXT);
        translations.put(Message.Type.POLL_CREATION, MessageType.POLL_CREATION);
        translations.put(Message.Type.POLL_UPDATE, MessageType.POLL_UPDATE);
        translations.put(Message.Type.CONTACT, MessageType.CONTACT);
        translations.put(Message.Type.CONTACT_ARRAY, MessageType.CONTACT_ARRAY);
        translations.put(Message.Type.LOCATION, MessageType.LOCATION);
        translations.put(Message.Type.LIVE_LOCATION, MessageType.LIVE_LOCATION);
        translations.put(Message.Type.GROUP_INVITE, MessageType.ROOM_INVITE);
        translations.put(Message.Type.REACTION, MessageType.REACTION);
        translations.put(Message.Type.ENCRYPTED_REACTION, MessageType.REACTION);
        translations.put(Message.Type.NEWSLETTER_ADMIN_INVITE, MessageType.NEWSLETTER_ADMIN_INVITE);
        translations.put(Message.Type.EMPTY, MessageType.EMPTY);
        translations.put(Message.Type.LIST, MessageType.LIST);
        translations.put(Message.Type.LIST_RESPONSE, MessageType.LIST_RESPONSE);
        translations.put(Message.Type.BUTTONS, MessageType.BUTTONS);
        translations.put(Message.Type.BUTTONS_RESPONSE, MessageType.BUTTONS_RESPONSE);
        translations.put(Message.Type.REQUEST_PHONE_NUMBER, MessageType.EMPTY);
        translations.put(Message.Type.STICKER_SYNC, MessageType.STICKER_SYNC);
        //translations.put(Message.Type.CALENDAR_EVENT, MessageType.CALENDAR_EVENT);
        translations.put(Message.Type.PRODUCT, MessageType.EMPTY);
        translations.put(Message.Type.SENDER_KEY_DISTRIBUTION, MessageType.EMPTY);
        translations.put(Message.Type.DEVICE_SENT, MessageType.EMPTY);
        translations.put(Message.Type.DEVICE_SYNC, MessageType.EMPTY);
        translations.put(Message.Type.VIEW_ONCE, MessageType.EMPTY);
        translations.put(Message.Type.EPHEMERAL, MessageType.EMPTY);
        translations.put(Message.Type.PAYMENT_INVOICE, MessageType.EMPTY);
        translations.put(Message.Type.PAYMENT_ORDER, MessageType.EMPTY);
        translations.put(Message.Type.SEND_PAYMENT, MessageType.EMPTY);
        translations.put(Message.Type.REQUEST_PAYMENT, MessageType.EMPTY);
        translations.put(Message.Type.DECLINE_PAYMENT_REQUEST, MessageType.EMPTY);
        translations.put(Message.Type.CANCEL_PAYMENT_REQUEST, MessageType.EMPTY);
        translations.put(Message.Type.TEMPLATE, MessageType.EMPTY);
        translations.put(Message.Type.TEMPLATE_REPLY, MessageType.EMPTY);
        translations.put(Message.Type.PROTOCOL, MessageType.EMPTY);

        attachments.put(Message.Type.AUDIO, AttachmentType.AUDIO);
        attachments.put(Message.Type.IMAGE, AttachmentType.IMAGE);
        attachments.put(Message.Type.VIDEO, AttachmentType.VIDEO);
        attachments.put(Message.Type.DOCUMENT, AttachmentType.DOCUMENT);
        attachments.put(Message.Type.STICKER, AttachmentType.STICKER);
    }

    public static MessageType translate(Message.Type messageType) {
        return translations.getOrDefault(messageType, MessageType.UNKNOWN);
    }

    public static AttachmentType translateAttachment(Message.Type messageType) {
        return attachments.get(messageType);
    }
}
