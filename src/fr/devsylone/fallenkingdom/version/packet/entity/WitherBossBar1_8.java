package fr.devsylone.fallenkingdom.version.packet.entity;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static fr.devsylone.fallenkingdom.utils.PacketUtils.getNMSWorld;

class WitherBossBar1_8 implements BossBar {

    private static final Constructor<?> PACKET_SPAWN_LIVING_ENTITY;
    private static final Constructor<?> PACKET_TELEPORT;
    private static final Constructor<?> PACKET_SET_METADATA;
    private static final Constructor<?> ENTITY_WITHER;
    private static final Method ENTITY_GET_BUKKIT_HANDLE;
    private static final Method ENTITY_GET_DATA_WATCHER;
    private static final Method ENTITY_SET_INVISIBLE;
    private static final Method ENTITY_SET_LOCATION;
    private static final Method DATA_WATCHER_WATCH;

    private static final int OFFSET_PITCH = 30;
    private static final int OFFSET_YAW = 0;
    private static final int OFFSET_MAGNITUDE = 40;
    private static final int INVULNERABLE_KEY = 20;
    private static final int INVULNERABLE_TICKS = 890;

    static {
        try {
            final Class<?> worldClass = NMSUtils.nmsClass("world", "World");
            final Class<?> entityClass = NMSUtils.nmsClass("entity", "Entity");
            final Class<?> livingEntityClass = NMSUtils.nmsClass("entity", "EntityLiving");
            final Class<?> dataWatcherClass = NMSUtils.nmsClass("entity", "DataWatcher");

            final String packetsPackage = "network.protocol.game";
            final Class<?> packetSpawnLivingEntityClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutSpawnEntityLiving");
            final Class<?> packetTeleportEntityClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityTeleport");
            final Class<?> packetEntityMetadataClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityMetadata");
            PACKET_SPAWN_LIVING_ENTITY = packetSpawnLivingEntityClass.getConstructor(livingEntityClass);
            PACKET_TELEPORT = packetTeleportEntityClass.getConstructor(entityClass);
            PACKET_SET_METADATA = packetEntityMetadataClass.getConstructor(int.class, dataWatcherClass, boolean.class);

            ENTITY_WITHER = NMSUtils.nmsClass("entity", "EntityWither").getConstructor(worldClass);
            ENTITY_GET_BUKKIT_HANDLE = entityClass.getMethod("getBukkitEntity");
            ENTITY_GET_DATA_WATCHER = entityClass.getMethod("getDataWatcher");
            ENTITY_SET_INVISIBLE = entityClass.getMethod("setInvisible", boolean.class);
            ENTITY_SET_LOCATION = entityClass.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
            DATA_WATCHER_WATCH = dataWatcherClass.getMethod("watch", int.class, Object.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private double progress = 1.0D;
    private String name;
    private @Nullable Wither entity;
    private Object entityNms;
    private final Set<Player> viewers = new HashSet<>();

    WitherBossBar1_8(@NotNull String name) {
        this.name = name;
    }

    @Override
    public void addPlayer(@NotNull Player player) {
        if (this.entity == null) {
            this.entity = spawnWither(player.getLocation().getWorld(), this.name);
        }
        if (!this.viewers.add(player)) {
            return;
        }
        try {
            teleport(player, offsetPosition(player.getLocation()));
            final Object packet = PACKET_SPAWN_LIVING_ENTITY.newInstance(this.entityNms);
            PacketUtils.sendPacket(player, packet);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void removePlayer(@NotNull Player player) {
        if (this.viewers.remove(player) && this.entity != null) {
            Hologram.INSTANCE.remove(player, this.entity.getEntityId());
        }
    }

    @Override
    public void setTitle(@NotNull String message) {
        this.name = message;
        if (this.entity == null) {
            return;
        }
        this.entity.setCustomName(message);
        this.syncMetadata();
    }

    @Override
    public void setProgress(double progress) {
        this.progress = progress;
        if (this.entity == null) {
            return;
        }
        this.entity.setHealth(progress * (this.entity.getMaxHealth() - 0.1f) + 0.1f);
        this.syncMetadata();
    }

    @Override
    public @NotNull Set<@NotNull Player> getPlayers() {
        return this.viewers;
    }

    @Override
    public void removeAll() {
        if (this.entity == null) {
            return;
        }
        for (Player viewer : this.viewers) {
            Hologram.INSTANCE.remove(viewer, this.entity.getEntityId());
        }
        this.viewers.clear();
    }

    private @NotNull Wither spawnWither(@NotNull World world, @NotNull String customName) {
        try {
            Object nmsWither = ENTITY_WITHER.newInstance(getNMSWorld(world));
            ENTITY_SET_INVISIBLE.invoke(nmsWither, true);
            Object dataWatcher = ENTITY_GET_DATA_WATCHER.invoke(nmsWither);
            DATA_WATCHER_WATCH.invoke(dataWatcher, INVULNERABLE_KEY, INVULNERABLE_TICKS);
            this.entityNms = nmsWither;
            Wither wither = (Wither) ENTITY_GET_BUKKIT_HANDLE.invoke(nmsWither);
            wither.setCustomName(customName);
            wither.setCustomNameVisible(true);
            wither.setHealth(this.progress * (wither.getMaxHealth() - 0.1f) + 0.1f);
            return wither;
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void teleport(@NotNull Player player, @NotNull Location loc) {
        try {
            ENTITY_SET_LOCATION.invoke(this.entityNms, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            final Object packet = PACKET_TELEPORT.newInstance(this.entityNms);
            PacketUtils.sendPacket(player, packet);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void syncMetadata() {
        try {
            final Object dataWatcher = ENTITY_GET_DATA_WATCHER.invoke(this.entityNms);
            final Object packet = PACKET_SET_METADATA.newInstance(this.entity.getEntityId(), dataWatcher, false);
            for (Player player : this.viewers) {
                PacketUtils.sendPacket(player, packet);
                teleport(player, offsetPosition(player.getLocation()));
            }
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private @NotNull Location offsetPosition(@NotNull Location location) {
        location.setPitch(location.getPitch() - OFFSET_PITCH);
        location.setYaw(location.getYaw() + OFFSET_YAW);
        location.add(location.getDirection().multiply(OFFSET_MAGNITUDE));
        return location;
    }
}
