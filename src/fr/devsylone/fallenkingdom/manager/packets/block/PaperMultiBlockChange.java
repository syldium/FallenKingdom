package fr.devsylone.fallenkingdom.manager.packets.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

class PaperMultiBlockChange implements MultiBlockChange {

    private final Map<Location, BlockData> changes = new HashMap<>();

    @Override
    public void change(@NotNull Block block, @NotNull Material material) {
        this.changes.put(block.getLocation(), material.createBlockData());
    }

    @Override
    public void send(@NotNull Player player) {
        player.sendMultiBlockChange(this.changes);
    }

    @Override
    public void cancel(@NotNull Player player) {
        final Map<Location, BlockData> changes = new HashMap<>(this.changes.size());
        for (Location location : this.changes.keySet()) {
            changes.put(location, location.getBlock().getBlockData());
        }
        player.sendMultiBlockChange(changes);
    }
}
