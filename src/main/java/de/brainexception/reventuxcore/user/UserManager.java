package de.brainexception.reventuxcore.user;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class UserManager {

    public UserManager() {}

    public CompletableFuture<User> loadUser(UUID uuid, String username) {
        return null;
    }

    public CompletableFuture<User> loadUser(UUID uuid) {
        return loadUser(uuid, null);
    }

    public CompletableFuture<UUID> findUniqueId(String username) {
        return  null;
    }

    public CompletableFuture<String> findUsername(UUID uuid) {
        return  null;
    }

    public CompletableFuture<Void> saveUser(User user) {
        return null;
    }

    public CompletableFuture<Void> modifyUser(UUID uuid, Consumer<? super User> action) {
        return loadUser(uuid)
                .thenApplyAsync(user -> {action.accept(user); return user;})
                .thenCompose(this::saveUser);
    }

    public CompletableFuture<UserData> saveUserData(UUID uuid) {
        return null;
    }

    public CompletableFuture<Void> deleteUserData(UUID uuid) {
        return null;
    }

    public User getUser(UUID uuid) {
        return null;
    }

    public User getUser(String username) {
        return null;
    }

    public Set<User> getLoadedUsers() {
        return null;
    }

    public boolean isLoaded(UUID uuid) {
        return false;
    }

    public void unloadUser(User user) {

    }

}
