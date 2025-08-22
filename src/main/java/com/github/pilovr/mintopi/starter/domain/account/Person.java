package com.github.pilovr.mintopi.starter.domain.account;

import com.github.pilovr.mintopi.starter.domain.common.Platform;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Person {
    //TODO id wegen datenbank?
    private final Map<Platform, Account> accounts;

    public Person(Account... accounts) {
        this.accounts = new HashMap<>();
        Arrays.stream(accounts).forEach(this::addAccount);
    }

    public Account getAccount(Platform platform) {
        return accounts.get(platform);
    }

    public void addAccount(Account account) {
        this.accounts.put(account.getPlatform(), account);
    }
}
