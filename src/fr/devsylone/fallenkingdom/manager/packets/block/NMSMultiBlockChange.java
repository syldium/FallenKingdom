package fr.devsylone.fallenkingdom.manager.packets.block;

import fr.devsylone.fallenkingdom.Fk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

class NMSMultiBlockChange implements MultiBlockChange {

    private final Set<BlockChange> changes = new HashSet<>();

    @Override
    public void change(@NotNull Block block, @NotNull Material material) {
        this.changes.add(new BlockChange(block, material));
    }

    @Override
    public void send(@NotNull Player player) {
        for (BlockChange change : this.changes) {
            Fk.getInstance().getPacketManager().sendBlockChange(player, change.position.getLocation(), change.material);
        }
    }

    @Override
    public void cancel(@NotNull Player player) {
        for (BlockChange change : this.changes) {
            Fk.getInstance().getPacketManager().sendBlockChange(player, change.position.getLocation(), change.position.getType());
        }
    }

    private static class BlockChange {
        final Block position;
        final Material material; // normalement de type BlockData en 1.13+

        BlockChange(@NotNull Block position, @NotNull Material material) {
            this.position = position;
            this.material = material;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BlockChange that = (BlockChange) o;
            return this.position.equals(that.position);
        }

        @Override
        public int hashCode() {
            return this.position.hashCode();
        }
    }
}
