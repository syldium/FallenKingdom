package fr.devsylone.fkpi.region;

import fr.devsylone.fkpi.util.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A region of blocks.
 */
@FunctionalInterface
public interface BlockRegion {

    /**
     * A region that does not contain any blocks.
     */
    ExpandableBlockRegion EMPTY = new EmptyBlockRegion();

    /**
     * Tests if the region contains the point.
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     * @param z The Z coordinate
     * @param offset The offset to add: if greater than zero, the region is larger
     * @return {@code true} if the region contains the point
     */
    boolean contains(int x, int y, int z, int offset);

    default boolean contains(int x, int y, int z) {
        return this.contains(x, y, z, 0);
    }

    default boolean contains(double x, double y, double z, int offset) {
        return this.contains((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z), offset);
    }

    default boolean contains(double x, double y, double z) {
        return this.contains(x, y, z, 0);
    }

    default boolean contains(@NotNull BlockPos pos, int offset) {
        return this.contains(pos.x, pos.y, pos.z, offset);
    }

    default boolean contains(@NotNull BlockPos pos) {
        return this.contains(pos, 0);
    }

    static @NotNull ExpandableBlockRegion fromPositions(@NotNull Collection<BlockPos> positions) {
        return positions.isEmpty() ? EMPTY : new CuboidBlockRegion(positions);
    }
}
