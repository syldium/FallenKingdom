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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static fr.devsylone.fallenkingdom.version.packet.entity.NMSHologram1_9.getEnumItemSlot;

class NMSHologram1_17 extends NMSHologram {

    private static final Object ARMOR_STAND;
    private static final Object ZERO_VEC3D;

    private static final Constructor<?> VEC3D;
    private static final Constructor<?> PACKET_SPAWN_ENTITY;
    private static final Constructor<?> PACKET_DESTROY_ENTITY;
    private static final Constructor<?> PACKET_ENTITY_EQUIPMENT;
    private static final @Nullable Constructor<?> PACKET_ENTITY_METADATA_CONSTRUCTOR;
    private static final @Nullable Constructor<?> PACKET_ENTITY_POSITION_CONSTRUCTOR;
    private static final @Nullable Constructor<?> DELTA_MOVEMENT_CONSTRUCTOR;
    private static final Class<?> PACKET_ENTITY_METADATA;
    private static final Class<?> PACKET_ENTITY_POSITION;
    private static final Field[] PACKET_ENTITY_POSITION_FIELDS;

    private static final boolean PACKET_DESTROY_ENTITY_LIST;
    private static final boolean PACKET_SPAWN_ENTITY_HEAD_YAW;

    static {
        try {
            final Class<?> entityTypesClass = NMSUtils.nmsClass("world.entity", "EntityTypes", "EntityType");
            final Class<?> vec3dClass = NMSUtils.nmsClass("world.phys", "Vec3D", "Vec3");
            ARMOR_STAND = ((Optional<?>) NMSUtils.getMethod(entityTypesClass, Optional.class, String.class).invoke(null, "armor_stand")).get();
            ZERO_VEC3D = NMSUtils.getField(vec3dClass, vec3dClass, field -> Modifier.isStatic(field.getModifiers())).get(null);
            VEC3D = vec3dClass.getConstructor(double.class, double.class, double.class);

            final String packetsPackage = "network.protocol.game";
            final Class<?> packetSpawnEntityClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutSpawnEntity", "ClientboundAddEntityPacket");
            final Class<?> packetDestroyEntityClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityDestroy", "ClientboundRemoveEntitiesPacket");
            final Class<?> packetEntityEquipment = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityEquipment", "ClientboundSetEquipmentPacket");

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
            PACKET_ENTITY_POSITION = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityTeleport", "ClientboundTeleportEntityPacket");
            Constructor<?> entityPositionConstructor = null;
            Constructor<?> deltaMovementConstructor = null;
            try {
                Class<?> deltaMovement = NMSUtils.nmsClass("world.entity", "PositionMoveRotation", "PositionMoveRotation");
                deltaMovementConstructor = deltaMovement.getConstructor(vec3dClass, vec3dClass, float.class, float.class);
                entityPositionConstructor = PACKET_ENTITY_POSITION.getConstructor(int.class, deltaMovement, Set.class, boolean.class);
            } catch (ClassNotFoundException | NoSuchMethodException ignored) {} // < 1.21.2
            PACKET_ENTITY_POSITION_CONSTRUCTOR = entityPositionConstructor;
            DELTA_MOVEMENT_CONSTRUCTOR = deltaMovementConstructor;
            PACKET_ENTITY_POSITION_FIELDS = Arrays.stream(PACKET_ENTITY_POSITION.getDeclaredFields())
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .toArray(Field[]::new);
            PACKET_ENTITY_METADATA = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutEntityMetadata", "ClientboundSetEntityDataPacket");
            for (Field field : PACKET_ENTITY_POSITION_FIELDS) {
                field.setAccessible(true);
            }
            Constructor<?> entityMetadataConstructor = null;
            try {
                entityMetadataConstructor = PACKET_ENTITY_METADATA.getConstructor(int.class, List.class);
            } catch (NoSuchMethodException ignored) {} // < 1.19.3
            PACKET_ENTITY_METADATA_CONSTRUCTOR = entityMetadataConstructor;
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
            Object packet;
            if (PACKET_ENTITY_METADATA_CONSTRUCTOR == null) {
                packet = Unsafety.allocateInstance(PACKET_ENTITY_METADATA);
                PacketUtils.setField("a", id, packet);
                PacketUtils.setField("b", tracker.trackedValues(), packet);
            } else {
                packet = PACKET_ENTITY_METADATA_CONSTRUCTOR.newInstance(id, tracker.serializedTrackedValues());
            }
            PacketUtils.sendPacket(p, packet);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void sendTeleport(Player p, int id, Location newLoc) {
        try {
            if (DELTA_MOVEMENT_CONSTRUCTOR != null && PACKET_ENTITY_POSITION_CONSTRUCTOR != null) {
                Object movement = DELTA_MOVEMENT_CONSTRUCTOR.newInstance(
                        VEC3D.newInstance(newLoc.getX(), newLoc.getY(), newLoc.getZ()),
                        ZERO_VEC3D,
                        newLoc.getYaw(),
                        newLoc.getPitch()
                );
                Object packet = PACKET_ENTITY_POSITION_CONSTRUCTOR.newInstance(
                        id,
                        movement,
                        Collections.emptySet(),
                        false
                );
                PacketUtils.sendPacket(p, packet);
                return;
            }
            Object packet = Unsafety.allocateInstance(PACKET_ENTITY_POSITION);
            PACKET_ENTITY_POSITION_FIELDS[0].set(packet, id);
            PACKET_ENTITY_POSITION_FIELDS[1].set(packet, newLoc.getX());
            PACKET_ENTITY_POSITION_FIELDS[2].set(packet, newLoc.getY());
            PACKET_ENTITY_POSITION_FIELDS[3].set(packet, newLoc.getZ());
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
