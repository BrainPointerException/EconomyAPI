package de.brainexception.reventuxcore.user;

import com.google.inject.Inject;
import de.brainexception.reventuxcore.ReventuxCorePlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserManager {

    private final ReventuxCorePlugin plugin;

    @Inject
    public UserManager(ReventuxCorePlugin plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<Optional<User>> loadUser(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return loadUserSync(uuid);
            } catch (SQLException throwables) {
                plugin.getLogger().warning("Error while loading user " + uuid);
                return Optional.empty();
            }
        });
    }

    public Optional<User> loadUserSync(UUID uuid) throws SQLException {
        try (PreparedStatement ps =
                     plugin.getDataSource().getConnection().prepareStatement(
                             "SELECT name, coins, FROM reventuxcore_users WHERE uuid = ? LIMIT 1")) {
            ps.setString(1, uuid.toString());

            try (ResultSet rs = ps.executeQuery()) {
                String name = rs.getString(1);
                double coins = rs.getDouble(2);
                return Optional.of(new User(uuid, name, coins));
            }
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

    public Optional<User> createUserSync(UUID uuid, String name, double coins) {
        try (PreparedStatement ps =
                plugin.getDataSource().getConnection().prepareStatement(
                        "INSERT INTO reventuxcore_users " +
                                "(uuid, name, coins) VALUES (?, ?, ?)")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.setDouble(3, coins);
            ps.executeUpdate();
            return Optional.of(new User(uuid, name, coins));
        } catch (SQLException e) {
            plugin.getLogger().warning("Error creating user!" + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public CompletableFuture<Void> saveUser(User user) {
        return null;
    }

    public CompletableFuture<Void> deleteUser(UUID uuid) {
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
