package fr.devsylone.fallenkingdom.version.packet.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface BlockChange {

    BlockChange INSTANCE = Provider.BLOCK_CHANGE;

    void send(@NotNull Player player, @NotNull Location location, @NotNull Material material);
}
