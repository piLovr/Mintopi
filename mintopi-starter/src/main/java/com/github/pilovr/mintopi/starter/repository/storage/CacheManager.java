package com.github.pilovr.mintopi.starter.repository.storage;

import com.github.pilovr.mintopi.starter.domain.account.Account;
import com.github.pilovr.mintopi.starter.domain.common.Platform;
import com.github.pilovr.mintopi.starter.domain.room.Room;
import kotlin.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
public class CacheManager {
    StorageConfig storageConfig;
    private final Map<Pair<Platform, String>, Account> accounts = new HashMap<>();
    private final Map<Pair<Platform, String>, Room> rooms = new HashMap<>();

    @Autowired
    public CacheManager(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }

    public Account getOrCreateAccount(String id, Platform platform, String pushName) {
        Pair<Platform, String> key = new Pair<>(platform, id);
        /*if(!storageConfig.get()) {
            return new Account(id, platform, pushName);
        }*/
        if (this.accounts.containsKey(key)) {
            Account a = this.accounts.get(key);
            if(!Objects.equals(a.getPushName(), pushName)){
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

    public Room getOrCreateRoom(String id, Platform platform, String name, Map<Account, Set<String>> members) {
        if(!storageConfig.getRoomDataCaching()) {
            return new Room(id, platform, name, members);
        }
        Pair<Platform, String> key = new Pair<>(platform, id);
        if (this.rooms.containsKey(key)) {
            Room r = this.rooms.get(key);
            if(r.getName() != name){
                r.setName(name);
            }
            return r;
        } else {
            Room room = new Room(id, platform, name, members);
            this.rooms.put(key, room);
            return room;
        }
    }
}
