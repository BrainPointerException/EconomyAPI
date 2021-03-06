package de.brainexception.reventuxcore.user;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.inject.Inject;
import de.brainexception.reventuxcore.ReventuxCorePlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UserManager {

    /**
     * Plugin instance
     */
    private final ReventuxCorePlugin plugin;
    public final LoadingCache<UUID, CompletableFuture<Optional<User>>> userCache;

    @Inject
    public UserManager(ReventuxCorePlugin plugin) {
        this.plugin = plugin;
        this.userCache =
                Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(this::loadUser);
    }

    /**
     * Loads a user async from the database
     *
     * @param uuid The {@link UUID} of the user to load
     * @return A {@link CompletableFuture} which contains a {@link Optional} to return a {@link User} or an empty Optional
     */
    private CompletableFuture<Optional<User>> loadUser(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return loadUserSync(uuid);
            } catch (SQLException throwables) {
                plugin.getLogger().warning("Error while loading user " + uuid);
                plugin.getLogger().warning(throwables.getMessage());
                throwables.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * Loads a user from the database
     *
     * @param uuid The {@link UUID} of the user to load.
     * @return An {@link Optional} which contains a {@link User} or an empty Optional
     * @throws SQLException when a {@link ResultSet} is invalid
     */
    private Optional<User> loadUserSync(UUID uuid) throws SQLException {
        try (Connection connection = plugin.getDataSource().getSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                             "SELECT name, coins FROM reventuxcore_users WHERE uuid = ? LIMIT 1")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString(1);
                    double coins = rs.getDouble(2);
                    return Optional.of(new User(uuid, name, coins));
                } else return Optional.empty();
            }
        }
    }

    /**
     * Updates the user async and loads the user afterwards async
     *
     * @param uuid The {@link UUID} of the user to update and load
     * @param name The {@link String} of the user to update and load
     * @return A {@link CompletableFuture} which contains a {@link Optional} which is empty or contains a {@link User}
     */
    public CompletableFuture<Optional<User>> loadAndUpdateUser(UUID uuid, String name) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                updateUserSync(uuid, name);
                return loadUserSync(uuid);
            } catch (SQLException e) {
                plugin.getLogger().warning("Error while loading user " + uuid);
                e.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * Updates the uuid and name given name sync
     *
     * @param uuid The {@link UUID} which is getting updated
     * @param name The {@link String} which is getting updated
     * @throws SQLException if the uuid is invalid
     */
    public void updateUserSync(UUID uuid, String name) throws SQLException {
        try (Connection connection = plugin.getDataSource().getSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                        "UPDATE reventuxcore_users SET name = ? WHERE uuid = ?")) {
            ps.setString(1, name);
            ps.setString(2, uuid.toString());
        }
    }

    /**
     * Finds the appropriate uuid with the given username
     *
     * @param username The {@link String} which is needed to find the uuid
     * @return A {@link Optional} which is empty if no uuid could be found or the {@link UUID}
     */
    public Optional<UUID> findUniqueIdSync(String username) {
        try (Connection connection = plugin.getDataSource().getSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                             "SELECT uuid FROM reventuxcore_users WHERE name = ? LIMIT 1")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                String uuid = rs.getString(1);
                return Optional.of(UUID.fromString(uuid));
            } catch (SQLException e) {
                return Optional.empty();
            }
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    public Optional<String> findUsernameSync(UUID uuid) {
        try (Connection connection = plugin.getDataSource().getSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                        "SELECT name FROM reventuxcore_users WHERE uuid = ? LIMIT 1")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString(1);
                    return Optional.of(name);
                } else return Optional.empty();
            } catch (SQLException e) {
                plugin.getLogger().warning("Could not find username for uuid!" + e.getMessage());
                e.printStackTrace();
                return Optional.empty();
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Could not execute findUsernameSync statement!" + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public CompletableFuture<Optional<User>> createUser(UUID uuid, String name, double coins) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return createUserSync(uuid, name, coins);
            } catch (SQLException throwables) {
                plugin.getLogger().warning("Error while creating user! " + uuid);
                throwables.printStackTrace();
                return Optional.empty();
            }
        });
    }

    private Optional<User> createUserSync(UUID uuid, String name, double coins) throws SQLException {
        try (Connection connection = plugin.getDataSource().getSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO reventuxcore_users " +
                                "(uuid, name, coins) VALUES (?, ?, ?)")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.setDouble(3, coins);
            ps.executeUpdate();
            return Optional.of(getUser(uuid));
        }
    }

    public CompletableFuture<Optional<User>> saveUser(User user) {
        return CompletableFuture.supplyAsync(() -> saveUserSync(user));
    }

    public Optional<User> saveUserSync(User user) {
        try (Connection connection = plugin.getDataSource().getSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                        "UPDATE reventuxcore_users SET name = ?, coins = ? WHERE uuid = ?")) {
            ps.setString(1, user.getUsername());
            ps.setDouble(2, user.getCoins());
            ps.setString(3, user.getUuid().toString());
            ps.executeUpdate();
            return Optional.of(user);
        } catch (SQLException e) {
            plugin.getLogger().warning("Error saving user!" + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public CompletableFuture<Void> deleteUserSync(UUID uuid) {
        return null;
    }

    public User getUser(UUID uuid) {
        CompletableFuture<Optional<User>> cacheUser = userCache.getIfPresent(uuid);
        if (cacheUser != null) {
            try {
                Optional<User> optionalUser = cacheUser.get();

                if (optionalUser.isPresent()) {
                    return optionalUser.get();
                }
            } catch (InterruptedException | ExecutionException e) {
                plugin.getLogger().warning("Error while loading user " + uuid);
                e.printStackTrace();
            }
        }

        CompletableFuture<Optional<User>> loadUser = loadUser(uuid);
        userCache.put(uuid, loadUser);

        try {
            return loadUser.get().get();
        } catch (ExecutionException | InterruptedException e) {
            plugin.getLogger().warning("Error while loading user " + uuid);
            e.printStackTrace();
            return null;
        }
    }

    public void unloadUser(UUID uuid) {
        userCache.invalidate(uuid);
    }

    public void clearCache() {
        userCache.asMap().forEach((uuid, optionalCompletableFuture) -> {
            try {
                optionalCompletableFuture.get()
                        .ifPresent(user -> saveUser(user).thenAccept(user1 -> unloadUser(user1.get().getUuid())));
            } catch (InterruptedException | ExecutionException e) {
                plugin.getLogger().warning("Error while saving user " + uuid);
                e.printStackTrace();
            }
        });
    }

}
