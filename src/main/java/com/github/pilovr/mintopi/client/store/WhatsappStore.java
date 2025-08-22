package com.github.pilovr.mintopi.client.store;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.client.Client;
import com.github.pilovr.mintopi.client.listener.InternalListener;
import com.github.pilovr.mintopi.domain.Listener;
import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.event.StubEvent;
import com.github.pilovr.mintopi.domain.room.Room;
import com.github.pilovr.mintopi.client.listener.WhatsappInternalListener;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.chat.ChatMetadata;
import it.auties.whatsapp.model.chat.ChatRole;
import it.auties.whatsapp.model.jid.Jid;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Scope("prototype")
public class WhatsappStore extends Store{
    @Setter
    private Client client;
    @Setter
    private Whatsapp api;
    private WhatsappInternalListener internalListener;


    public Room updateRoomMetadata(Room room) {
        ChatMetadata meta = api.queryGroupMetadata(Jid.of(room.getId()));
        Map<Account, Set<String>> members = new HashMap<>();
        meta.participants().forEach(participant -> {
            Set<String> roles = new HashSet<>(Collections.singleton(participant.role().data()));
            if(participant.role() == ChatRole.FOUNDER) {
                roles.add("admin");
            }
            members.put(getOrCreateAccount(participant.jid().toString(), Platform.Whatsapp, null), roles);
        });
        room.setMembers(members);
        room.setFounder(getOrCreateAccount(meta.founder().toString(), Platform.Whatsapp, null));
        room.setDescription(meta.description().toString());
        room.setEphemeralExpiration(meta.ephemeralExpirationSeconds());
        return room;
    }

    public Room getRoomWithParticipants(Room room){
        return room.getMembers() == null ? updateRoomMetadata(room) : room;
    }

    public Room getRoomWithParticipant(String id){
        return getRoomWithParticipants(rooms.get(id));
    }

    public void setInternalListener(InternalListener internalListener) {
        internalListener.registerListener(new Listener() {
            @Override
            public void onStubEvent(StubEvent stubEvent) {
                rooms.replace(stubEvent.getRoom().getId(), stubEvent.getRoom());
            }
        });
    }
}
