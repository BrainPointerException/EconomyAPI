package de.brainexception.reventuxcore.user;

import java.util.UUID;

public class User {

    private UUID uuid;
    private String username;
    private UserData data;

    public User(UUID uuid) {
        this.uuid = uuid;
    }

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UserData getUserData() {
        return data;
    }
}