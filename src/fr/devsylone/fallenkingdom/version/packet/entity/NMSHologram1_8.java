package fr.devsylone.fallenkingdom.version.packet.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import fr.devsylone.fallenkingdom.utils.XItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;

class NMSHologram1_8 extends NMSHologram {

    private static final Constructor<?> PACKET_SPAWN_ENTITY;
    private static final Constructor<?> PACKET_ENTITY_POSITION;
    private static final Constructor<?> PACKET_ENTITY_METADATA;
    private static final Constructor<?> PACKET_DESTROY_ENTITY;
    private static final Constructor<?> PACKET_ENTITY_EQUIPMENT;
    private static final Constructor<?> PACKET_BLOCK_CHANGE;

    private static final Constructor<?> WATCHABLE_OBJECT;
    private static final Method WORLD_GET_BLOCK_TYPE;

    static {
        try {
            final Class<?> worldClass = NMSUtils.nmsClass("world", "World");

            final String packetsPackage = "network.protocol.game";
            final Class<?> packetSpawnEntityClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutSpawnEntity");
            final Class<?> packetTeleportEntityClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityTeleport");
            final Class<?> packetEntityMetadataClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityMetadata");
            final Class<?> packetDestroyEntityClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityDestroy");
            final Class<?> packetEntityEquipmentClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityEquipment");
            final Class<?> packetBlockChangeClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutBlockChange");
            final Class<?> watchableObjectClass = NMSUtils.nmsClass("network.syncher", "DataWatcher$WatchableObject");
            PACKET_SPAWN_ENTITY = packetSpawnEntityClass.getConstructor();
            PACKET_ENTITY_POSITION = packetTeleportEntityClass.getConstructor(int.class, int.class, int.class, int.class, byte.class, byte.class, boolean.class);
            PACKET_ENTITY_METADATA = packetEntityMetadataClass.getConstructor();
            PACKET_DESTROY_ENTITY = packetDestroyEntityClass.getConstructor(int[].class);
            PACKET_ENTITY_EQUIPMENT = packetEntityEquipmentClass.getConstructor(int.class, int.class, XItemStack.ITEM_STACK);
            PACKET_BLOCK_CHANGE = packetBlockChangeClass.getConstructor(worldClass, PacketUtils.MINECRAFT_BLOCK_POSITION);

            WATCHABLE_OBJECT = watchableObjectClass.getConstructor(int.class, int.class, Object.class);
            WORLD_GET_BLOCK_TYPE = worldClass.getMethod("getType", PacketUtils.MINECRAFT_BLOCK_POSITION);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    protected int sendSpawn(Player p, Location loc) {
        if (loc == null && p != null)
            loc = p.getLocation();

        int id = entityIdSupplier.getAsInt();
        try {
            Object spawn = PACKET_SPAWN_ENTITY.newInstance();
            PacketUtils.setField("a", id, spawn);
            PacketUtils.setField("b", (int) (loc.getX() * 32), spawn);
            PacketUtils.setField("c", (int) (loc.getY() * 32), spawn);
            PacketUtils.setField("d", (int) (loc.getZ() * 32), spawn);
            PacketUtils.setField("e", 0, spawn);
            PacketUtils.setField("f", 0, spawn);
            PacketUtils.setField("g", 0, spawn);
            PacketUtils.setField("h", 0, spawn);
            PacketUtils.setField("i", 0, spawn);
            PacketUtils.setField("j", 78, spawn);
            PacketUtils.setField("k", 0, spawn);

            PacketUtils.sendPacket(p, spawn);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
        return id;
    }

    @Override
    protected void sendMetadata(Player p, int id, boolean visible, String customName) {
        try {
            Object metadata = PACKET_ENTITY_METADATA.newInstance();

            PacketUtils.setField("a", id, metadata);
            List<Object> datas = new ArrayList<>();

            if (!visible) {
                datas.add(WATCHABLE_OBJECT.newInstance(0, 0, (byte) 32));
            }
            if (customName != null && !customName.isEmpty()) {
                datas.add(WATCHABLE_OBJECT.newInstance(4, 2, customName));
                datas.add(WATCHABLE_OBJECT.newInstance(0, 3, (byte) 1));
            }

            PacketUtils.setField("b", datas, metadata);

            PacketUtils.sendPacket(p, metadata);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void sendTeleport(Player p, int id, Location newLoc) {
        try {
            int x = (int) (newLoc.getX() * 32D);
            int y = (int) ((newLoc.getY() - 0.6) * 32D);
            int z = (int) (newLoc.getZ() * 32D);
            Object tp = PACKET_ENTITY_POSITION.newInstance(id, x, y, z, (byte) 0, (byte) 0, true);

            PacketUtils.sendPacket(p, tp);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void sendDestroy(Player p, int id) {
        try {
            Object destroy = PACKET_DESTROY_ENTITY.newInstance(new int[]{id});
            PacketUtils.sendPacket(p, destroy);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void sendEquipment(Player p, int id, ItemSlot slot, Material material) {
        try {
            Object nmsItem = XItemStack.asCraftItem(new ItemStack(material));
            Object armors = PACKET_ENTITY_EQUIPMENT.newInstance(id, getItemSlot(slot), nmsItem);

            PacketUtils.sendPacket(p, armors);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    private int getItemSlot(ItemSlot slot) {
        return slot.ordinal() > 0 ? slot.ordinal() - 1 : 0;
    }
}
