package com.github.pilovr.mintopi.store;

import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.domain.room.Room;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public abstract class Store<R extends Room, A extends Account> {


    // Factory methods to be implemented by subclasses
    protected abstract R createRoom(String id, Platform platform, String name);
    protected abstract A createAccount(String id, Platform platform, String pushName);

    public abstract R getOrCreateRoom(String id, Platform platform, String name);
    public abstract A getOrCreateAccount(String id, Platform platform, String pushName);
}