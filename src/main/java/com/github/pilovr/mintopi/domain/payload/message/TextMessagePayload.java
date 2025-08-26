package com.github.pilovr.mintopi.domain.payload.message;

import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.payload.message.attachment.Attachment;
import com.github.pilovr.mintopi.subscriber.command.CommandProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class TextMessagePayload extends MessagePayload {
    private final String text;
    private final Context context;
    private CommandProperties textCommandProperties;
    private final List<Attachment> attachments; //LinkedList

    public TextMessagePayload(String text, Context context) {
        this.text = text;
        this.context = context;
        this.attachments = null;
    }

    public TextMessagePayload(String text, Context context, List<Attachment> attachments) {
        this.text = text;
        this.context = context;
        this.attachments = attachments;
    }
    public Set<TextMessagePayload> splitByLines(Platform platform){
        Set<String> lines = text.lines().filter(line -> !line.isBlank()).collect(java.util.stream.Collectors.toSet());
        return lines.stream().map(line -> TextMessagePayload.builder().text(line).context(context.toBuilder().mentions(context.getMentionsInText(line)).build()).build()).collect(java.util.stream.Collectors.toSet());
    }

    public CommandProperties getCommandProperties(Platform platform) {
        if(textCommandProperties == null){
            textCommandProperties = new CommandProperties(text);
            return textCommandProperties;
        }
        return textCommandProperties;
    }

    public static TextMessagePayload concat(Set<TextMessagePayload> parts){
        StringBuilder sb = new StringBuilder();
        List<Account> mentions = null;
        for(TextMessagePayload part : parts){
            if(!sb.isEmpty()) sb.append(System.lineSeparator());
            sb.append(part.getText());
            if(part.getContext() != null && part.getContext().getMentions() != null){
                if(mentions == null) mentions = part.getContext().getMentions();
                else{
                    for(Account mention : part.getContext().getMentions()){
                        if(!mentions.contains(mention)) mentions.add(mention);
                    }
                }
            }
        }
        return TextMessagePayload.builder().text(sb.toString()).context(Context.builder().mentions(mentions).build()).build();
    }
    public TextMessagePayloadBuilder toBuilder(){
        return new TextMessagePayloadBuilder();
    }

    public static TextMessagePayloadBuilder builder(){
        return new TextMessagePayloadBuilder();
    }
    public static class TextMessagePayloadBuilder{
        private String text;
        private Context context;
        private CommandProperties textCommandProperties;
        private List<Attachment> attachments; //LinkedList

        public TextMessagePayloadBuilder text(String text){
            this.text = text;
            return this;
        }

        public TextMessagePayloadBuilder context(Context context){
            this.context = context;
            return this;
        }

        public TextMessagePayloadBuilder  textCommandProperties(CommandProperties textCommandProperties){
            this.textCommandProperties = textCommandProperties;
            return this;
        }

        public TextMessagePayloadBuilder attachments(List<Attachment> attachments){
            this.attachments = attachments;
            return this;
        }

        public TextMessagePayloadBuilder addAttachment(Attachment attachment){
            if(this.attachments == null) this.attachments = new ArrayList<>();
            this.attachments.add(attachment);
            return this;
        }

        public Context.ContextBuilder  contextBuilder(){
            return new Context.ContextBuilder();
        }

        public TextMessagePayloadBuilder contextWithMentionsFromText(){
            this.context = new Context.ContextBuilder().
        }
    }
}
