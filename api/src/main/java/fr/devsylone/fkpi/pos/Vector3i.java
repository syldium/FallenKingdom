package fr.devsylone.fkpi.pos;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import static fr.devsylone.fkpi.pos.MathHelper.square;

public interface Vector3i extends Serializable {

    int x();

    int y();

    int z();

    @NotNull Vector3i set(int x, int y, int z);

    @NotNull Vector3i setX(int x);

    @NotNull Vector3i setY(int y);

    @NotNull Vector3i setZ(int z);

    @NotNull Vector3i add(int x, int y, int z);

    default long chunkKey() {
        return (long) this.x() & 0xffffffffL | ((long) this.z() & 0xffffffffL) << 32;
    }

    default int distanceSquared(@NotNull Vector3i other) {
        return square(this.x() - other.x()) + square(this.y() - other.y()) + square(this.z() - other.z());
    }

    default int distanceSquared(@NotNull Block block) {
        return square(this.x() - block.getX()) + square(this.y() - block.getY()) + square(this.z() - block.getZ());
    }

    default double distanceSquared(@NotNull Location location) {
        return square(this.x() + 0.5d - location.getX()) + square(this.y() + 0.5d - location.getY()) + square(this.z() + 0.5d - location.getZ());
    }

    @Contract(pure = true)
    @NotNull BlockPos asImmutable();

    @Contract(pure = true)
    @NotNull MutableBlockPos asMutable();
}
