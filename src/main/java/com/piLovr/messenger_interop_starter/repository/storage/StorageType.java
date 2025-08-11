package com.piLovr.messenger_interop_starter.repository.storage;

public enum StorageType {
    DISCARD,     // Do not store anything
    DATABASE,   // Store everything in database
    HYBRID      // Store in database but cache in memory
}