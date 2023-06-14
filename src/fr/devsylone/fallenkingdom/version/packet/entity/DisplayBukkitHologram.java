package fr.devsylone.fallenkingdom.version.packet.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

class DisplayBukkitHologram implements Hologram {

    private final Int2ObjectMap<UUID> entitiesUUIDById = new Int2ObjectOpenHashMap<>();
    
    @Override
    public int createFloatingText(@NotNull String text, @NotNull Player player, @NotNull Location location) {
        final TextDisplay display = player.getWorld().spawn(location, TextDisplay.class);
        display.setText(text);
        display.setBillboard(Display.Billboard.CENTER);
        this.entitiesUUIDById.put(display.getEntityId(), display.getUniqueId());
        return display.getEntityId();
    }

    @Override
    public void updateFloatingText(@NotNull Player player, int id, @NotNull Location location) {
        final UUID uuid = this.entitiesUUIDById.get(id);
        final Entity entity = player.getWorld().getEntity(uuid);
        entity.teleport(location);
    }

    @Override
    public void updateFloatingText(@NotNull Player player, int id, @NotNull String line) {
        final UUID uuid = this.entitiesUUIDById.get(id);
        final Entity entity = player.getWorld().getEntity(uuid);
        if (entity instanceof TextDisplay) {
            ((TextDisplay) entity).setText(line);
        } else {
            entity.setCustomName(line);
        }
    }

    @Override
    public int displayItem(@NotNull ItemSlot slot, @NotNull Player player, @NotNull Location location, @NotNull Material item) {
        final ItemDisplay display = player.getWorld().spawn(location, ItemDisplay.class, false, entity -> entity.setItemStack(new ItemStack(item)));
        this.entitiesUUIDById.put(display.getEntityId(), display.getUniqueId());
        return display.getEntityId();
    }

    @Override
    public void remove(@NotNull Player player, int id) {
        final UUID uuid = this.entitiesUUIDById.remove(id);
        final Entity entity = player.getWorld().getEntity(uuid);
        if (entity != null) {
            entity.remove();
        }
    }
}
