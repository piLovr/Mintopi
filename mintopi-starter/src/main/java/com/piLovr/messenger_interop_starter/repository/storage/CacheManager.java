package com.piLovr.messenger_interop_starter.repository.storage;

import com.piLovr.messenger_interop_starter.domain.account.Account;
import com.piLovr.messenger_interop_starter.domain.common.Platform;
import com.piLovr.messenger_interop_starter.domain.room.Room;
import kotlin.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CacheManager {
    StorageConfig storageConfig;
    private Map<Pair<Platform, String>, Account> accounts;
    private Map<Pair<Platform, String>, Room> rooms;

    @Autowired
    public CacheManager(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
        if(storageConfig.getRoomDataCaching()) this.accounts = new java.util.HashMap<>();
        this.rooms = new java.util.HashMap<>();
    }

    public Account getOrCreateAccount(String id, Platform platform, String pushName) {
        Pair<Platform, String> key = new Pair<>(platform, id);
        /*if(!storageConfig.get()) {
            return new Account(id, platform, pushName);
        }*/
        if (this.accounts.containsKey(key)) {
            Account a = this.accounts.get(key);
            if(a.getPushName() != pushName){
                a.setPushName(pushName);
            }
            return a;
        } else {
            Account account = new Account(id, platform, pushName);
            this.accounts.put(key, account);
            accounts.put(key, account);
            return account;
        }
    }

    public Room getOrCreateRoom(String id, Platform platform, String name) {
        if(!storageConfig.getRoomDataCaching()) {
            return new Room(id, platform, name);
        }
        Pair<Platform, String> key = new Pair<>(platform, id);
        if (this.rooms.containsKey(key)) {
            Room r = this.rooms.get(key);
            if(r.getName() != name){
                r.setName(name);
            }
            return r;
        } else {
            Room room = new Room(id, platform, name);
            this.rooms.put(key, room);
            return room;
        }
    }
}
