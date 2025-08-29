package com.github.pilovr.mintopi.domain.event;

import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.payload.Payload;
import com.github.pilovr.mintopi.domain.room.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Set;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class EventContext< P extends Payload, R extends Room, A extends Account> {
    protected final Client<R,A> client;
    protected final P payload;

    protected final String id;
    protected final R room;
    protected final A sender;
    protected final Platform platform;
    protected final Instant timestamp;
    protected final boolean fromMe;

    protected final Object originalObject;




    

    private boolean collectSends = false;
    private Set<String> messagesToSend;

    protected CommandResponseBuilder textBuilder;
    protected String jsonName;

    public static <P extends Payload, R extends Room, A extends Account> EventContextBuilder<P,R,A> builder() {
        return new EventContextBuilder<>();
    }
    public static class EventContextBuilder<P extends Payload, R extends Room, A extends Account> {
        protected Client<R,A> client;
        protected P payload;

        protected String id;
        protected R room;
        protected A sender;
        protected Platform platform;
        protected Instant timestamp;
        protected boolean fromMe;

        protected Object originalObject;
        public EventContextBuilder<P,R,A> client(Client<R,A> client) {
           this.client = client;
           return this;
        }
        public EventContextBuilder<P,R,A> payload(P payload) {
            this.payload = payload;
            return this;
        }
        public EventContextBuilder<P,R,A> id(String id) {
            this.id = id;
            return this;
        }
        public EventContextBuilder<P,R,A> room(R room) {
            this.room = room;
            return this;
        }
        public EventContextBuilder<P,R,A> sender(A sender) {
            this.sender = sender;
            return this;
        }
        public EventContextBuilder<P,R,A> platform(Platform platform) {
            this.platform = platform;
            return this;
        }
        public EventContextBuilder<P,R,A> timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        public EventContextBuilder<P,R,A> fromMe(boolean fromMe) {
            this.fromMe = fromMe;
            return this;
        }
        public EventContextBuilder<P,R,A> originalObject(Object originalObject) {
            this.originalObject = originalObject;
            return this;
        }
        public EventContext<P,R,A> build() {
            return new EventContext<>(client, payload, id, room, sender, platform, timestamp, fromMe, originalObject);
        }
    }
}
