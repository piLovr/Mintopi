package com.github.pilovr.mintopi.util;

import com.github.pilovr.mintopi.config.MintopiProperties;
import lombok.Setter;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;

@Service
@ConditionalOnProperty(
        name = "mintopi.spam-handler.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class MessageDoorman {
    private final MintopiProperties.SpamHandler spamProperties;
    private final HashSet<String> spamSet = new HashSet<>();
    private final HashMap<String, Integer> heavySpamSet = new HashMap<>();
    private final HashSet<String> timeoutSet = new HashSet<>();
    @Setter
    private HashSet<String> blacklist = new HashSet<>();
    @Setter
    private HashSet<String> whiteList = new HashSet<>();


    @Autowired
    public MessageDoorman(MintopiProperties mintopiProperties) {
        this.spamProperties = mintopiProperties.getSpamHandler();
    }

    public Pair<Boolean, Boolean> isForbidden(String senderId, String roomId){
        Pair<Boolean, Boolean> timeoutCheck = isTimeout(senderId);
        return Pair.with(!isWhiteListed(senderId) || isBlackListed(senderId) || timeoutCheck.getValue0() || isSpam(senderId, roomId), timeoutCheck.getValue1());
    }
    public boolean isSpam(String senderId, String roomId) {
       return spamSet.contains(getTypeValue(senderId, roomId));
    }

    public Pair<Boolean, Boolean> isTimeout(String senderId){
        if(timeoutSet.contains(senderId)) return Pair.with(true, false);
        Integer count = heavySpamSet.get(senderId);
        if(count == null || count < spamProperties.getSpamThreshold()) return Pair.with(false, false);
        timeoutSet.add(senderId);
        new Thread(() -> {
            try {
                Thread.sleep((long) spamProperties.getTimeoutDuration() *60*1000);
                timeoutSet.remove(senderId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        return Pair.with(true, true);
    }

    public void setCustomTimeout(String senderId, int duration){
        timeoutSet.add(senderId);
        new Thread(() -> {
            try {
                Thread.sleep((long) duration*1000*60);
                timeoutSet.remove(senderId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public boolean isBlackListed(String senderId){
        return blacklist.contains(senderId);
    }

    public boolean isWhiteListed(String senderId){
        return whiteList.contains(senderId);
    }

    public void addBlacklist(String senderId){
        blacklist.add(senderId);
    }

    public void removeBlacklist(String senderId){
        blacklist.remove(senderId);
    }

    public void addWhitelist(String senderId){
        whiteList.add(senderId);
    }

    public void removeWhitelist(String senderId){
        whiteList.remove(senderId);
    }

    private String getTypeValue(String senderId, String roomId){
        return switch(spamProperties.getMessageCooldownParam()){
            case "perRoom" -> roomId;
            case "perMember" -> senderId;
            default -> throw new IllegalStateException("Unexpected value: " + spamProperties.getMessageCooldownParam());
        };
    }
    public void register(String senderId, String roomId) {
        registerSpam(senderId, roomId);
        registerHeavySpam(senderId);
    }

    public void registerSpam(String senderId, String roomId){
        String val = getTypeValue(senderId, roomId);
        spamSet.add(val);
        new Thread(() -> {
            try {
                Thread.sleep(spamProperties.getMessageCooldown()* 1000L);
                spamSet.remove(val);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void registerHeavySpam(String senderId){
        heavySpamSet.putIfAbsent(senderId, 0);
        heavySpamSet.put(senderId, heavySpamSet.get(senderId) + 1);
        new Thread(() -> {
            try {
                Thread.sleep(60* 1000L);
                heavySpamSet.remove(senderId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public Pair<Boolean, Boolean> shouldDecode(String senderId, String roomId){
        registerHeavySpam(senderId);
        Pair<Boolean, Boolean> forbiddenCheck = isForbidden(senderId, roomId);
        if(spamProperties.isDecodeAnyways()) return Pair.with(true, forbiddenCheck.getValue1());
        return forbiddenCheck;
    }
}
