package fr.devsylone.fallenkingdom.version.packet.block;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class NMSBlockChange implements BlockChange {

    private static final Constructor<?> PACKET_BLOCK_CHANGE;
    private static final Method GET_BLOCK_BY_ID;

    static {
        try {
            final Class<?> worldClass = NMSUtils.nmsClass("world", "World");
            final String packetsPackage = "network.protocol.game";
            final Class<?> packetBlockChangeClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutBlockChange");
            PACKET_BLOCK_CHANGE = packetBlockChangeClass.getConstructor(worldClass, PacketUtils.MINECRAFT_BLOCK_POSITION);

            final Class<?> blockClass = NMSUtils.nmsClass("world", "Block");
            GET_BLOCK_BY_ID = blockClass.getMethod("getByCombinedId", int.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public void send(@NotNull Player player, @NotNull Location location, @NotNull Material material) {
        try {
            Object blockPos = PacketUtils.getNMSBlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Object packet = PACKET_BLOCK_CHANGE.newInstance(PacketUtils.getNMSWorld(player.getWorld()), blockPos);
            PacketUtils.setField("block", GET_BLOCK_BY_ID.invoke(null, material.getId()), packet);
            PacketUtils.sendPacket(player, packet);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }
}
