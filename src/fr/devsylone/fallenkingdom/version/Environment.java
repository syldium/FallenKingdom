package fr.devsylone.fallenkingdom.version;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Environment {

    private static final boolean HAS_ASYNC_TELEPORT;
    private static final boolean HAS_UUID_BY_PLAYER_NAME;
    private static final boolean HAS_ASYNC_CHUNK_LOAD;

    static {
        boolean hasAsyncTeleport = false;
        try {
            Entity.class.getMethod("teleportAsync", Location.class);
            hasAsyncTeleport = true;
        } catch (NoSuchMethodException ignored) { }
        HAS_ASYNC_TELEPORT = hasAsyncTeleport;

        boolean hasUuidByPlayerName = false;
        try {
            Bukkit.class.getMethod("getPlayerUniqueId", String.class);
            hasUuidByPlayerName = true;
        } catch (NoSuchMethodException ignored) { }
        HAS_UUID_BY_PLAYER_NAME = hasUuidByPlayerName;

        boolean hasAsyncChunkLoad = false;
        try {
            World.class.getMethod("getChunkAtAsync", int.class, int.class);
            hasAsyncChunkLoad = true;
        } catch (NoSuchMethodException ignored) { }
        HAS_ASYNC_CHUNK_LOAD = hasAsyncChunkLoad;
    }

    public static CompletableFuture<Boolean> teleportAsync(Entity entity, Location location) {
        if (HAS_ASYNC_TELEPORT) {
            return entity.teleportAsync(location);
        }
        return CompletableFuture.completedFuture(entity.teleport(location)); // Sinon synchrone
    }

    public static UUID getPlayerUniqueId(String playerName) {
        if (HAS_UUID_BY_PLAYER_NAME) {
            return Bukkit.getPlayerUniqueId(playerName);
        }
        Player player = Bukkit.getPlayerExact(playerName);
        return player == null ? null : player.getUniqueId();
    }

    public static CompletableFuture<Chunk> getChunkAtAsync(World world, int x, int z) {
        if (HAS_ASYNC_CHUNK_LOAD) {
            return world.getChunkAtAsync(x, z);
        }
        return CompletableFuture.completedFuture(world.getChunkAt(x, z));
    }
}
