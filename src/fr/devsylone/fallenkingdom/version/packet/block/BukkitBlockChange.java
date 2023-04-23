package fr.devsylone.fallenkingdom.version.packet.block;

import fr.devsylone.fallenkingdom.utils.XBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class BukkitBlockChange implements BlockChange {

    private final boolean BLOCK_DATA = XBlock.isFlat();

    @Override
    public void send(@NotNull Player player, @NotNull Location location, @NotNull Material material) {
        if (BLOCK_DATA) {
            player.sendBlockChange(location, material.createBlockData());
        } else {
            player.sendBlockChange(location, material, (byte) 0);
        }
    }
}
