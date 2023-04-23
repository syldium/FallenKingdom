package fr.devsylone.fallenkingdom.version.packet.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Hologram {

    Hologram INSTANCE = Provider.HOLOGRAM;

    int createFloatingText(@NotNull String text, @NotNull Player player, @NotNull Location location);

    void updateFloatingText(@NotNull Player player, int id, @NotNull Location location);

    void updateFloatingText(@NotNull Player player, int id, @NotNull String line);

    int displayItem(@NotNull ItemSlot slot, @NotNull Player player, @NotNull Location location, @NotNull Material item);

    void remove(@NotNull Player player, int id);
}
