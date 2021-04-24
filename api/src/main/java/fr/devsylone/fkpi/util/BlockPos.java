package fr.devsylone.fkpi.util;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class BlockPos {

    public static final Vec3d ZERO = new Vec3d(0, 0, 0);

    public final int x, y, z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos(double x, double y, double z) {
        this((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }

    public BlockPos(@NotNull Block block) {
        this(block.getX(), block.getY(), block.getZ());
    }

    public long chunkKey() {
        return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPos blockPos = (BlockPos) o;
        return x == blockPos.x && y == blockPos.y && z == blockPos.z;
    }

    @Override
    public int hashCode() {
        return (this.x * 211 + this.y) * 97 + this.z;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}
