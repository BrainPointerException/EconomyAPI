package de.brainexception.reventuxcore.user;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.inject.Inject;
import de.brainexception.reventuxcore.ReventuxCorePlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UserManager {

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

    private Optional<User> loadUserSync(UUID uuid) throws SQLException {
        try (PreparedStatement ps =
                     plugin.getDataSource().getConnection().prepareStatement(
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

    public void updateUserSync(UUID uuid, String name) throws SQLException {
        try (PreparedStatement ps =
                plugin.getDataSource().getConnection().prepareStatement(
                        "UPDATE reventuxcore_users SET name = ? WHERE uuid = ?")) {
            ps.setString(1, name);
            ps.setString(2, uuid.toString());
        }
    }

    public Optional<UUID> findUniqueIdSync(String username) {
        try (PreparedStatement ps =
                     plugin.getDataSource().getConnection().prepareStatement(
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
        try (PreparedStatement ps =
                plugin.getDataSource().getConnection().prepareStatement(
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
        try (PreparedStatement ps =
                plugin.getDataSource().getConnection().prepareStatement(
                        "INSERT INTO reventuxcore_users " +
                                "(uuid, name, coins) VALUES (?, ?, ?)")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.setDouble(3, coins);
            ps.executeUpdate();
            return Optional.of(getUser(uuid, name));
        }
    }

    public CompletableFuture<Optional<User>> saveUser(User user) {
        return CompletableFuture.supplyAsync(() -> saveUserSync(user));
    }

    public Optional<User> saveUserSync(User user) {
        try (PreparedStatement ps =
                plugin.getDataSource().getConnection().prepareStatement(
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

    public User getUser(UUID uuid, String name) {
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

        CompletableFuture<Optional<User>> loadUser = loadAndUpdateUser(uuid, name);
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

}
