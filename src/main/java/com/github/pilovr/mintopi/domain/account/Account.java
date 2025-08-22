package com.github.pilovr.mintopi.domain.account;

import com.github.pilovr.mintopi.domain.common.Platform;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Account {
    private final Platform platform;
    private final String id;
    private String pushName;

    public Account(String id, Platform platform, String pushName) {
        this.id = id;
        this.platform = platform;

        if(pushName != null) {
            pushName = removeNonAsciiCharacters(pushName);
        }else{
            pushName = null;
        }
    }
    public String removeNonAsciiCharacters(String input) {
        return input.replaceAll("[^\\x00-\\x7F]", "");
    }

    public void setPushName(@NonNull String pushName) {
        this.pushName = removeNonAsciiCharacters(pushName);
    }
}
