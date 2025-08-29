package com.github.pilovr.mintopi.domain.payload.message;

import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.room.Room;
import com.github.pilovr.mintopi.domain.payload.message.attachment.Attachment;
import com.github.pilovr.mintopi.store.Store;
import com.github.pilovr.mintopi.subscriber.command.CommandProperties;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public final class TextMessagePayload<R extends Room, A extends Account> extends MessagePayload {
    private final String text;
    private final MessageContext<R,A> context;
    private CommandProperties textCommandProperties;
    private final List<Attachment> attachments; //LinkedList

    public TextMessagePayload(String text, MessageContext<R,A> context) {
        this.text = text;
        this.context = context;
        this.attachments = null;
    }

    public TextMessagePayload(String text, MessageContext<R,A> context, List<Attachment> attachments) {
        this.text = text;
        this.context = context;
        this.attachments = attachments;
    }

    public Set<TextMessagePayload<R,A>> splitByLines(Platform platform) {
        Set<String> lines = text.lines().filter(line -> !line.isBlank()).collect(Collectors.toSet());
        return lines.stream()
                .map(line -> TextMessagePayload.<R,A>builder()
                        .text(line)
                         //todo .context(context.toBuilder().mentions(context.getMentionsInText(line)).build())
                        .build())
                .collect(Collectors.toSet());
    }

    public CommandProperties getCommandProperties(Platform platform) {
        if (textCommandProperties == null) {
            textCommandProperties = new CommandProperties(text);
            return textCommandProperties;
        }
        return textCommandProperties;
    }

    public static <R extends Room, A extends Account> TextMessagePayload<R,A> concat(Set<TextMessagePayload<R,A>> parts) {
        StringBuilder sb = new StringBuilder();
        List<A> mentions = null;

        for (TextMessagePayload<R,A> part : parts) {
            if (!sb.isEmpty()) sb.append(System.lineSeparator());
            sb.append(part.getText());

            if (part.getContext() != null && part.getContext().getMentions() != null) {
                if (mentions == null) {
                    mentions = new ArrayList<>(part.getContext().getMentions());
                } else {
                    for (A mention : part.getContext().getMentions()) {
                        if (!mentions.contains(mention)) mentions.add(mention);
                    }
                }
            }
        }

        return TextMessagePayload.<R,A>builder()
                .text(sb.toString())
                .context(MessageContext.<R,A>builder().mentions(mentions).build())
                .build();
    }

    public MessageContext<R,A> addTextMentions(String text, MessageContext<R,A> context, Platform platform, Store store) {
        return null; //TODO store needed.
    }

    public TextMessagePayloadBuilder<R,A> toBuilder() {
        return new TextMessagePayloadBuilder<R,A>()
                .text(this.text)
                .context(this.context)
                .attachments(this.attachments);
    }

    public static <R extends Room, A extends Account> TextMessagePayloadBuilder<R,A> builder() {
        return new TextMessagePayloadBuilder<>();
    }

    public static class TextMessagePayloadBuilder<R extends Room,A extends Account> {
        private String text;
        private MessageContext<R,A> context;
        private List<Attachment> attachments; //LinkedList

        public TextMessagePayloadBuilder<R,A> text(String text) {
            this.text = text;
            return this;
        }

        public TextMessagePayloadBuilder<R,A> context(MessageContext<R,A> context) {
            this.context = context;
            return this;
        }

        public TextMessagePayloadBuilder<R,A> attachments(List<Attachment> attachments) {
            this.attachments = attachments;
            return this;
        }

        public TextMessagePayloadBuilder<R,A> addAttachment(Attachment attachment) {
            if (this.attachments == null) this.attachments = new ArrayList<>();
            this.attachments.add(attachment);
            return this;
        }

        public MessageContext.MessageContextBuilder<R,A> contextBuilder() {
            return MessageContext.builder();
        }

        public TextMessagePayload<R,A> build() {
            return new TextMessagePayload<>(text, context, attachments);
        }
    }
}