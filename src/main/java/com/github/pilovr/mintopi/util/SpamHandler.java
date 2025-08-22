package com.github.pilovr.mintopi.util;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.event.MessageEvent;
import com.github.pilovr.mintopi.domain.room.Room;
import com.github.pilovr.mintopi.domain.spring.properties.SpamProperties;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@ConditionalOnProperty(
        name = "mintopi.spam-handler.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class SpamHandler {
    private final SpamProperties spamProperties;

    @Autowired
    public SpamHandler(SpamProperties spamProperties) {
        this.spamProperties = spamProperties;
    }
    private final HashSet<Triplet<Platform, Room, Account>> spamSet = new HashSet<>();
    public boolean isSpam(MessageEvent event) {
        if(spamSet.contains(Triplet.with(event.getPlatform(), event.getRoom(), event.getSender()))) return true;
        addSpamTimer(Triplet.with(event.getPlatform(), event.getRoom(), event.getSender()));
        return false;
    }

    private void addSpamTimer(Triplet<Platform, Room, Account> triplet) {
        new Thread(() -> {
            try {
                Thread.sleep(spamProperties.getSpamTimeout());
                spamSet.remove(triplet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
