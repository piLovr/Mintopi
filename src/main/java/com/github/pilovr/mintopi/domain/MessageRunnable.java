package com.github.pilovr.mintopi.domain;

import com.github.pilovr.mintopi.domain.room.Room;

@FunctionalInterface
public interface MessageRunnable {
    void run(Room room);
}
