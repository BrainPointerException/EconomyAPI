package de.brainexception.reventuxcore.user;

import java.util.UUID;

public class User {

    private UUID uuid;
    private String username;
    private double coins;

    public User(UUID uuid) {
        this.uuid = uuid;
    }

    public User(String username) {
        this.username = username;
    }

    public User (UUID uuid, String username, double coins) {
        this.uuid = uuid;
        this.username = username;
        this.coins = coins;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getCoins() {
        return coins;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public void addCoins(double coins) {
        this.coins =+ coins;
    }

    public void removeCoins (double coins) {
        this.coins -= coins;
    }
}
