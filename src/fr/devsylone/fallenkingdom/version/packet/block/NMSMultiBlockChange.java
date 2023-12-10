package fr.devsylone.fallenkingdom.version.packet.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

class NMSMultiBlockChange implements MultiBlockChange {

    private final Set<SingleBlockChange> changes = new HashSet<>();

    @Override
    public void change(@NotNull Block block, @NotNull Material material) {
        this.changes.add(new SingleBlockChange(block, material));
    }

    @Override
    public void send(@NotNull Player player) {
        for (SingleBlockChange change : this.changes) {
            BlockChange.INSTANCE.send(player, change.position.getLocation(), change.material);
        }
    }

    @Override
    public void cancel(@NotNull Player player) {
        for (SingleBlockChange change : this.changes) {
            BlockChange.INSTANCE.send(player, change.position.getLocation(), change.position.getType());
        }
    }

    private static class SingleBlockChange {
        final Block position;
        final Material material; // normalement de type BlockData en 1.13+

        SingleBlockChange(@NotNull Block position, @NotNull Material material) {
            this.position = position;
            this.material = material;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof SingleBlockChange)) return false;
            SingleBlockChange that = (SingleBlockChange) o;
            return this.position.equals(that.position);
        }

        @Override
        public int hashCode() {
            return this.position.hashCode();
        }
    }
}
