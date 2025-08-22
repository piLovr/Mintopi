package com.github.pilovr.mintopi.store;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.common.Platform;
import com.github.pilovr.mintopi.domain.room.Room;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public abstract class Store {
    protected Map<String, Room> rooms;
    protected Map<String, Account> accounts;

    public Room getOrCreateRoom(String id, Platform platform, String name) {
        Room existingRoom = rooms.get(id);
        if (existingRoom != null) {
            if (!existingRoom.getName().equals(name)) {
                existingRoom.setName(name);
            }
            return existingRoom;
        } else {
            Room room = new Room(id, platform, name);
            return rooms.put(id, room);
        }
    }

    public Account getOrCreateAccount(String id, Platform platform, String pushName) {
        Account existingAccount = accounts.get(id);
        if (existingAccount!= null) {
            if(!Objects.equals(existingAccount.getPushName(), pushName)){
                existingAccount.setPushName(pushName);
            }
            return existingAccount;
        } else {
            Account account = new Account(id, platform, pushName);
            return accounts.put(id, account);
        }
    }
}
