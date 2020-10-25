package fr.devsylone.fallenkingdom.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Version {

    public static final VersionType VERSION_TYPE;

    private static final boolean HAS_ASYNC_TELEPORT;
    private static final boolean HAS_UUID_BY_PLAYER_NAME;

    static {
        if (classExists("org.bukkit.block.data.BlockData")) {
            if (classExists("org.bukkit.event.inventory.TradeSelectEvent")) {
                VERSION_TYPE = classExists("org.bukkit.entity.Hoglin") ? VersionType.V1_16 : VersionType.V1_14_V1_15;
            } else {
                VERSION_TYPE = VersionType.V1_13;
            }
        } else if (Bukkit.getVersion().contains("1.8")) {
            VERSION_TYPE = VersionType.V1_8;
        } else {
            VERSION_TYPE = VersionType.V1_9_V1_12;
        }

        boolean hasAsyncTeleport;
        try {
            Entity.class.getMethod("teleportAsync", Location.class);
            hasAsyncTeleport = true;
        } catch (NoSuchMethodException e) {
            hasAsyncTeleport = false;
        }
        HAS_ASYNC_TELEPORT = hasAsyncTeleport;

        boolean hasUuidByPlayerName;
        try {
            Bukkit.class.getMethod("getPlayerUniqueId", String.class);
            hasUuidByPlayerName = true;
        } catch (NoSuchMethodException e) {
            hasUuidByPlayerName = false;
        }
        HAS_UUID_BY_PLAYER_NAME = hasUuidByPlayerName;
    }

    public static boolean hasSpigotApi() {
        return classExists("org.spigotmc.SpigotConfig");
    }

    public static boolean hasPaperApi() {
        return classExists("com.destroystokyo.paper.PaperConfig");
    }

    public static boolean isTooOldApi() {
        return !NMSUtils.nmsOptionalClass("IScoreboardCriteria$EnumScoreboardHealthDisplay").isPresent();
    }

    public static boolean isBrigadierSupported() {
        return classExists("com.mojang.brigadier.CommandDispatcher");
    }

    public static boolean isAsyncTabCompleteSupported() {
        return classExists("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent");
    }

    public static boolean isAsyncPlayerSendCommandsEventSupported() {
        return classExists("com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent");
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

    public static boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException notFound) {
            return false;
        }
    }

    public enum VersionType {
        V1_8,
        V1_9_V1_12,
        V1_13,
        V1_14_V1_15,
        V1_16;

        public boolean isHigherOrEqual() {
            return VERSION_TYPE.ordinal() >= ordinal();
        }
    }
}
