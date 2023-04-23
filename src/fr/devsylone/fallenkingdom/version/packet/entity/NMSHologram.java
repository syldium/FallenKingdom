package fr.devsylone.fallenkingdom.version.packet.entity;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

abstract class NMSHologram implements Hologram {

    protected final IntSupplier entityIdSupplier;

    @SuppressWarnings("deprecation")
    public NMSHologram() {
        IntSupplier supplier;
        try {
            Bukkit.getUnsafe().getClass().getDeclaredMethod("nextEntityId");
            supplier = Bukkit.getUnsafe()::nextEntityId;
        } catch (NoSuchMethodException e) {
            supplier = new AtomicInteger(100000)::getAndIncrement;
        }
        entityIdSupplier = supplier;
    }

    protected abstract int sendSpawn(Player p, Location loc);

    protected abstract void sendMetadata(Player p, int id, boolean visible, String customName);

    protected abstract void sendTeleport(Player p, int id, Location newLoc);

    protected abstract void sendDestroy(Player p, int id);

    protected abstract void sendEquipment(Player p, int id, ItemSlot slot, Material material);

    @Override
    public int createFloatingText(@NotNull String text, @NotNull Player p, @NotNull Location loc) {
        int id = sendSpawn(p, loc);
        sendMetadata(p, id, false, text);
        return id;
    }

    @Override
    public void updateFloatingText(@NotNull Player p, int id, @NotNull String line) {
        sendMetadata(p, id, false, line);
    }

    @Override
    public void updateFloatingText(@NotNull Player p, int id, @NotNull Location loc) {
        sendTeleport(p, id, loc);
    }

    @Override
    public void remove(@NotNull Player player, int id) {
        sendDestroy(player, id);
    }

    @Override
    public int displayItem(@NotNull ItemSlot slot, @NotNull Player p, @NotNull Location loc, @NotNull Material item) {
        int id = sendSpawn(p, loc.clone().add(0, -1, 0));
        sendMetadata(p, id, false, "");
        sendEquipment(p, id, slot, item);
        return id;
    }
}
