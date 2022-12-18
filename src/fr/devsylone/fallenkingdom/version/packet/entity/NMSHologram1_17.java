package fr.devsylone.fallenkingdom.version.packet.entity;

import com.mojang.datafixers.util.Pair;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import fr.devsylone.fallenkingdom.utils.Unsafety;
import fr.devsylone.fallenkingdom.utils.XItemStack;
import fr.devsylone.fallenkingdom.version.tracker.DataTracker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fr.devsylone.fallenkingdom.version.packet.entity.NMSHologram1_9.getEnumItemSlot;

class NMSHologram1_17 extends NMSHologram {

    private static final Object ARMOR_STAND;
    private static final Object ZERO_VEC3D;

    private static final Constructor<?> PACKET_SPAWN_ENTITY;
    private static final Constructor<?> PACKET_DESTROY_ENTITY;
    private static final Constructor<?> PACKET_ENTITY_EQUIPMENT;
    private static final Class<?> PACKET_ENTITY_METADATA;
    private static final Class<?> PACKET_ENTITY_POSITION;

    private static final boolean PACKET_DESTROY_ENTITY_LIST;
    private static final boolean PACKET_SPAWN_ENTITY_HEAD_YAW;

    static {
        try {
            final Class<?> entityTypesClass = NMSUtils.nmsClass("world.entity", "EntityTypes");
            final Class<?> vec3dClass = NMSUtils.nmsClass("world.phys", "Vec3D");
            ARMOR_STAND = ((Optional<?>) NMSUtils.getMethod(entityTypesClass, Optional.class, String.class).invoke(null, "armor_stand")).get();
            ZERO_VEC3D = NMSUtils.getField(vec3dClass, vec3dClass, field -> Modifier.isStatic(field.getModifiers())).get(null);

            final String packetsPackage = "network.protocol.game";
            final Class<?> packetSpawnEntityClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutSpawnEntity");
            final Class<?> packetDestroyEntityClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityDestroy");
            final Class<?> packetEntityEquipment = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityEquipment");

            Constructor<?> entityDestroy;
            try {
                entityDestroy = packetDestroyEntityClass.getConstructor(int.class);
            } catch (NoSuchMethodException e) { // 1.17.1
                entityDestroy = packetDestroyEntityClass.getConstructor(int[].class);
            }
            PACKET_DESTROY_ENTITY_LIST = entityDestroy.getParameterTypes()[0].equals(int[].class);

            Constructor<?> packetSpawnEntity;
            try {
                packetSpawnEntity = packetSpawnEntityClass.getConstructor(int.class, UUID.class, double.class, double.class, double.class, float.class, float.class, entityTypesClass, int.class, vec3dClass);
            } catch (NoSuchMethodException e) { // 1.19
                packetSpawnEntity = packetSpawnEntityClass.getConstructor(int.class, UUID.class, double.class, double.class, double.class, float.class, float.class, entityTypesClass, int.class, vec3dClass, double.class);
            }
            PACKET_SPAWN_ENTITY = packetSpawnEntity;
            PACKET_SPAWN_ENTITY_HEAD_YAW = packetSpawnEntity.getParameterCount() == 11;

            PACKET_DESTROY_ENTITY = entityDestroy;
            PACKET_ENTITY_EQUIPMENT = packetEntityEquipment.getConstructor(int.class, List.class);
            PACKET_ENTITY_POSITION = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityTeleport");
            PACKET_ENTITY_METADATA = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityMetadata");
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    protected int sendSpawn(Player p, Location loc) {
        int id = entityIdSupplier.getAsInt();

        try {
            Object packet;
            if (PACKET_SPAWN_ENTITY_HEAD_YAW) {
                packet = PACKET_SPAWN_ENTITY.newInstance(
                        id,                         // Entity id
                        UUID.randomUUID(),
                        loc.getX(), loc.getY(), loc.getZ(), // Position
                        loc.getPitch(), loc.getYaw(),       // Rotation
                        ARMOR_STAND,                        // Entity type
                        0,                                  // Entity data
                        ZERO_VEC3D,                         // Velocity
                        0D                                  // Head yaw
                );
            } else {
                packet = PACKET_SPAWN_ENTITY.newInstance(
                        id,                         // Entity id
                        UUID.randomUUID(),
                        loc.getX(), loc.getY(), loc.getZ(), // Position
                        loc.getPitch(), loc.getYaw(),       // Rotation
                        ARMOR_STAND,                        // Entity type
                        0,                                  // Entity data
                        ZERO_VEC3D                          // Velocity
                );
            }
            PacketUtils.sendPacket(p, packet);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
        return id;
    }

    @Override
    protected void sendMetadata(Player p, int id, boolean visible, String customName) {
        try {
            DataTracker tracker = new DataTracker()
                    .invisible()
                    .customName(customName)
                    .customNameVisible(true);
            Object packet = Unsafety.allocateInstance(PACKET_ENTITY_METADATA);
            PacketUtils.setField("a", id, packet);
            PacketUtils.setField("b", tracker.trackedValues(), packet);
            PacketUtils.sendPacket(p, packet);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void sendTeleport(Player p, int id, Location newLoc) {
        try {
            Object packet = Unsafety.allocateInstance(PACKET_ENTITY_POSITION);
            PacketUtils.setField("a", id, packet);
            PacketUtils.setField("b", newLoc.getX(), packet);
            PacketUtils.setField("c", newLoc.getY(), packet);
            PacketUtils.setField("d", newLoc.getZ(), packet);
            PacketUtils.sendPacket(p, packet);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void sendDestroy(Player p, int id) {
        try {
            final Object packet = PACKET_DESTROY_ENTITY_LIST ? PACKET_DESTROY_ENTITY.newInstance(new int[]{id}) : PACKET_DESTROY_ENTITY.newInstance(id);
            PacketUtils.sendPacket(p, packet);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void sendEquipment(Player p, int id, ItemSlot slot, Material material) {
        try {
            ItemStack bukkitItem = new ItemStack(material);
            Object armors = PACKET_ENTITY_EQUIPMENT.newInstance(id, Collections.singletonList(Pair.of(getEnumItemSlot(slot), XItemStack.asCraftItem(bukkitItem))));
            PacketUtils.sendPacket(p, armors);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }
}
