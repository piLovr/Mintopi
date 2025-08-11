package com.piLovr.messenger_interop_starter.domain.account;

import com.piLovr.messenger_interop_starter.domain.common.Platform;
import lombok.Getter;

@Getter
public class Account {
    private Platform platform;
    private String id;
    private String pushName;
}
