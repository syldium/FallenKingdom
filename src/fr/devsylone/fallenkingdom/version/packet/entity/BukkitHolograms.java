package fr.devsylone.fallenkingdom.version.packet.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

class BukkitHolograms implements Hologram {

    private final Int2ObjectMap<UUID> entitiesUUIDById = new Int2ObjectOpenHashMap<>();

    @Override
    public int createFloatingText(@NotNull String text, @NotNull Player p, @NotNull Location loc) {
        final ArmorStand armorStand = p.getWorld().spawn(loc, ArmorStand.class, false, entity -> {
            entity.setVisible(false);
            entity.setCustomName(text);
            entity.setCustomNameVisible(true);
        });
        this.entitiesUUIDById.put(armorStand.getEntityId(), armorStand.getUniqueId());
        return armorStand.getEntityId();
    }

    @Override
    public void updateFloatingText(@NotNull Player player, int id, @NotNull Location location) {
        final UUID uuid = requireNonNull(entitiesUUIDById.get(id), "Unknown custom entity id");
        final Entity entity = requireNonNull(player.getWorld().getEntity(uuid), "entity");
        entity.teleport(location);
    }

    @Override
    public void updateFloatingText(@NotNull Player player, int id, @NotNull String line) {
        final UUID uuid = requireNonNull(entitiesUUIDById.get(id), "Unknown custom entity id");
        final Entity entity = requireNonNull(player.getWorld().getEntity(uuid), "entity");
        entity.setCustomName(line);
    }

    @Override
    public int displayItem(@NotNull ItemSlot slot, @NotNull Player p, @NotNull Location loc, @NotNull Material item) {
        final ArmorStand armorStand = p.getWorld().spawn(loc, ArmorStand.class, false, entity -> {
            entity.setVisible(false);
            entity.setItem(EquipmentSlot.HAND, new ItemStack(item));
        });
        this.entitiesUUIDById.put(armorStand.getEntityId(), armorStand.getUniqueId());
        return armorStand.getEntityId();
    }

    @Override
    public void remove(@NotNull Player player, int id) {
        final UUID uuid = requireNonNull(entitiesUUIDById.get(id), "Unknown custom entity id");
        final Entity entity = requireNonNull(player.getWorld().getEntity(uuid), "entity");
        entity.remove();
    }
}
