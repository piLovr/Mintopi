package com.github.pilovr.mintopi.client.store;

import com.github.pilovr.mintopi.domain.account.Account;
import com.github.pilovr.mintopi.client.Platform;
import com.github.pilovr.mintopi.domain.room.Room;
import com.github.pilovr.mintopi.util.MessageDoorman;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

@Service
public abstract class Store {
    protected Map<String, Room> rooms;
    protected Map<String, Account> accounts;
    @Getter
    protected MessageDoorman messageDoorman;

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

    public abstract Room updateRoomMetadata(Room room);

    public Room getRoomWithParticipants(Room room){
        return room.getMembers() == null ? updateRoomMetadata(room) : room;
    }

    public Room getRoomWithParticipant(String id){
        return getRoomWithParticipants(rooms.get(id));
    }

    public void setBannedUsers(HashSet<String> bannedUsers){
        messageDoorman.setBlacklist(bannedUsers);
    }

    public void setWhitelistedUsers(HashSet<String> whitelistedUsers){
        messageDoorman.setWhiteList(whitelistedUsers);
    }

    public void addBannedUser(String userId){
        messageDoorman.addBlacklist(userId);
    }

    public void removeBannedUser(String userId){
        messageDoorman.removeBlacklist(userId);
    }

    public void addWhitelistedUser(String userId){
        messageDoorman.addWhitelist(userId);
    }

    public void removeWhitelistedUser(String userId){
        messageDoorman.removeWhitelist(userId);
    }
}
