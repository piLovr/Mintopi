package com.github.pilovr.mintopi.client.store;

import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.room.Room;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnMissingBean(value = Store.class, ignored = DefaultInMemoryStore.class)
public class DefaultInMemoryStore extends Store<Room, Account> {
    private final Map<String, Room> rooms = new HashMap<>();
    private final Map<String, Account> accounts = new HashMap<>();

    @Override
    protected Room createRoom(String id, Platform platform, String name) {
        return new Room(id, platform, name);
    }

    @Override
    protected Account createAccount(String id, Platform platform, String pushName) {
        return new Account(id, platform, pushName);
    }

    @Override
    public Room getOrCreateRoom(String id, Platform platform, String name) {
        String idWithPlatform = platform != null ? platform.getIdPrefix() + "_" + id : id;
        Room existingRoom = rooms.get(idWithPlatform);
        if (existingRoom != null) {
            if (!existingRoom.getName().equals(name)) {
                existingRoom.setName(name);
            }
            return existingRoom;
        } else {
            Room room = createRoom(id, platform, name);
            rooms.put(idWithPlatform, room);
            return room;
        }
    }

    @Override
    public Account getOrCreateAccount(String id, Platform platform, String pushName) {
        String idWithPlatform = platform != null ? platform.getIdPrefix() + "_" + id : id;
        Account existingAccount = accounts.get(idWithPlatform);
        if (existingAccount != null) {
            if(!existingAccount.getPushName().equals(pushName)){
                existingAccount.setPushName(pushName);
            }
            return existingAccount;
        } else {
            Account account = createAccount(id, platform, pushName);
            accounts.put(idWithPlatform, account);
            return account;
        }
    }
}