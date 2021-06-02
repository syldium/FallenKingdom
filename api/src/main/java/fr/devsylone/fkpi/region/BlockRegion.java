package fr.devsylone.fkpi.region;

import fr.devsylone.fkpi.pos.MutableBlockPos;
import fr.devsylone.fkpi.pos.Vector3i;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

/**
 * A region of blocks.
 */
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

    default boolean contains(@NotNull Vector3i pos, int offset) {
        return this.contains(pos.x(), pos.y(), pos.z(), offset);
    }

    default boolean contains(@NotNull Vector3i pos) {
        return this.contains(pos, 0);
    }

    /**
     * Create an iterator of positions around the region on a horizontal plane.
     *
     * @param y The height to turn from. The Y coordinate will not be changed later.
     * @param offsetX If applicable, the offset on the X axis. If less than 0, the size will be smaller.
     * @param offsetZ If applicable, the offset on the Z axis. If less than 0, the size will be smaller.
     * @return An iterator. Note that it is always the same instance of {@link MutableBlockPos}.
     */
    @NotNull Iterator<@NotNull MutableBlockPos> iterateOutwards(int y, int offsetX, int offsetZ);

    default @NotNull Iterator<@NotNull MutableBlockPos> iterateOutwards(int y) {
        return this.iterateOutwards(y, 0, 0);
    }

    static @NotNull ExpandableBlockRegion fromPositions(@NotNull Collection<? extends Vector3i> positions) {
        return positions.isEmpty() ? EMPTY : new CuboidBlockRegion(positions);
    }
}
