package fr.devsylone.fallenkingdom.version.packet.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import fr.devsylone.fallenkingdom.utils.XItemStack;
import fr.devsylone.fallenkingdom.version.tracker.ChatMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import io.netty.buffer.ByteBuf;

class NMSHologram1_8 extends NMSHologram {

    private static final Constructor<?> PACKET_SPAWN_ENTITY;
    private static final Constructor<?> PACKET_ENTITY_POSITION;
    private static final Constructor<?> PACKET_ENTITY_METADATA;
    private static final Constructor<?> PACKET_DESTROY_ENTITY;
    private static final Constructor<?> PACKET_ENTITY_EQUIPMENT;
    private static final Constructor<?> PACKET_BLOCK_CHANGE;
    private static final Constructor<?> PACKET_CUSTOM_PAYLOAD;

    private static final Constructor<?> PACKET_DATA_SERIALIZER;
    private static final Constructor<?> WATCHABLE_OBJECT;
    private static final Method WORLD_GET_BLOCK_TYPE;

    private static final Constructor<?> PACKET_TITLE_TIMES;
    private static final Constructor<?> PACKET_TITLE_TEXT;
    private static final Class<?> TITLE_ACTION;

    static {
        try {
            final Class<?> worldClass = NMSUtils.nmsClass("world", "World");

            final String packetsPackage = "network.protocol.game";
            final Class<?> packetSpawnEntityClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutSpawnEntity");
            final Class<?> packetTeleportEntityClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityTeleport");
            final Class<?> packetEntityMetadataClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityMetadata");
            final Class<?> packetDestroyEntityClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityDestroy");
            final Class<?> packetEntityEquipmentClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityEquipment");
            final Class<?> packetTitleClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutTitle");
            final Class<?> packetBlockChangeClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutBlockChange");
            final Class<?> packetCustomPayloadClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutCustomPayload");
            final Class<?> watchableObjectClass = NMSUtils.nmsClass("network.syncher", "DataWatcher$WatchableObject");
            final Class<?> packetSerializerClass = NMSUtils.nmsClass("PacketDataSerializer");
            PACKET_SPAWN_ENTITY = packetSpawnEntityClass.getConstructor();
            PACKET_ENTITY_POSITION = packetTeleportEntityClass.getConstructor(int.class, int.class, int.class, int.class, byte.class, byte.class, boolean.class);
            PACKET_ENTITY_METADATA = packetEntityMetadataClass.getConstructor();
            PACKET_DESTROY_ENTITY = packetDestroyEntityClass.getConstructor(int[].class);
            PACKET_ENTITY_EQUIPMENT = packetEntityEquipmentClass.getConstructor(int.class, int.class, XItemStack.ITEM_STACK);
            PACKET_BLOCK_CHANGE = packetBlockChangeClass.getConstructor(worldClass, PacketUtils.MINECRAFT_BLOCK_POSITION);
            PACKET_CUSTOM_PAYLOAD = packetCustomPayloadClass.getConstructor(String.class, packetSerializerClass);

            PACKET_DATA_SERIALIZER = packetSerializerClass.getConstructor(ByteBuf.class);
            WATCHABLE_OBJECT = watchableObjectClass.getConstructor(int.class, int.class, Object.class);
            WORLD_GET_BLOCK_TYPE = worldClass.getMethod("getType", PacketUtils.MINECRAFT_BLOCK_POSITION);

            TITLE_ACTION = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutTitle$EnumTitleAction");
            PACKET_TITLE_TIMES = packetTitleClass.getConstructor(int.class, int.class, int.class);
            PACKET_TITLE_TEXT = packetTitleClass.getConstructor(TITLE_ACTION, ChatMessage.CHAT_BASE_COMPONENT);
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

    /*@Override
    public void sendBlockChange(Player p, Location loc, Material newBlock) {
        Material oldMat = loc.getWorld().getBlockAt(loc.getBlockX(), 0, loc.getBlockZ()).getType();
        loc.getWorld().getBlockAt(loc.getBlockX(), 0, loc.getBlockZ()).setType(newBlock);
        try {
            Object blockPositionSet = PacketUtils.getNMSBlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            Object blockPositionGet = PacketUtils.getNMSBlockPos(loc.getBlockX(), 0, loc.getBlockZ());

            Object change = PACKET_BLOCK_CHANGE.newInstance(PacketUtils.getNMSWorld(p.getWorld()), blockPositionSet);
            PacketUtils.setField("block", WORLD_GET_BLOCK_TYPE.invoke(PacketUtils.getNMSWorld(p.getWorld()), blockPositionGet), change);

            PacketUtils.sendPacket(p, change);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
        loc.getWorld().getBlockAt(loc.getBlockX(), 0, loc.getBlockZ()).setType(oldMat);
    }*/

    /*@Override
    protected void sendTitlePacket(Player p, TitleType type, String text, int fadeIn, int stay, int fadeOut) {
        try {
            Object title;
            if (type == TitleType.TIMES) {
                title = PACKET_TITLE_TIMES.newInstance(fadeIn, stay, fadeOut);
            } else {
                title = PACKET_TITLE_TEXT.newInstance(TITLE_ACTION.getDeclaredField(type.name()).get(null), ChatMessage.legacyTextComponentString(text));
            }

            PacketUtils.sendPacket(p, title);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }

    }*/

    /*@Override
    public void openBook(final Player p, FkBook book) {
        final int slot = p.getInventory().getHeldItemSlot();
        final ItemStack original = p.getInventory().getItem(slot);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "replaceitem entity " + p.getName() + " slot.hotbar." + slot + " minecraft:written_book 1 0 " + book.nbt());

        Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> {
            try {
                if (original != null && original.getType() != Material.AIR)
                    p.getWorld().dropItem(p.getLocation(), original).setPickupDelay(0);

                p.getInventory().setHeldItemSlot(slot);

                ByteBuf buf = Unpooled.buffer(256);
                buf.setByte(0, (byte) 0);
                buf.writerIndex(1);
                Object payload = PACKET_CUSTOM_PAYLOAD.newInstance("MC|BOpen", PACKET_DATA_SERIALIZER.newInstance(buf));

                PacketUtils.sendPacket(p, payload);
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        }, 5L);
    }*/

    private int getItemSlot(ItemSlot slot) {
        return slot.ordinal() > 0 ? slot.ordinal() - 1 : 0;
    }
}
