package fr.devsylone.fkpi.region;

import fr.devsylone.fkpi.pos.MutableBlockPos;
import fr.devsylone.fkpi.pos.Vector3i;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CuboidBlockRegion implements ExpandableBlockRegion {

    private final int minX, minY, minZ, maxX, maxY, maxZ;

    public CuboidBlockRegion(final Vector3i pos, final int radius) {
        this(pos.x(), pos.y(), pos.z(), radius);
    }

    public CuboidBlockRegion(final int centerX, final int centerY, final int centerZ, final int radius) {
        this.minX = centerX - radius;
        this.minY = centerY - radius;
        this.minZ = centerZ - radius;
        this.maxX = centerX + radius;
        this.maxY = centerY + radius;
        this.maxZ = centerZ + radius;
    }

    public CuboidBlockRegion(final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    protected CuboidBlockRegion(final @NotNull Iterable<@NotNull ? extends Vector3i> positions) {
        final Iterator<? extends Vector3i> iterator = positions.iterator();
        final Vector3i first = iterator.next();
        int minX, minY, minZ, maxX, maxY, maxZ;
        minX = maxX = first.x();
        minY = maxY = first.y();
        minZ = maxZ = first.z();

        while (iterator.hasNext()) {
            Vector3i pos = iterator.next();
            minX = Math.min(minX, pos.x());
            minY = Math.min(minY, pos.y());
            minZ = Math.min(minZ, pos.z());
            maxX = Math.max(maxX, pos.x());
            maxY = Math.max(maxY, pos.y());
            maxZ = Math.max(maxZ, pos.z());
        }

        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    @Override
    public boolean contains(final int x, final int y, final int z, final int offset) {
        return x >= this.minX - offset && x <= this.maxX + offset
                && y >= this.minY - offset && y <= this.maxY + offset
                && z >= this.minZ - offset && z <= this.maxZ + offset;
    }

    @Override
    public @NotNull Iterator<@NotNull MutableBlockPos> iterateOutwards(int y, int offsetX, int offsetZ) {
        return new OutlineIterator(this, y, offsetX, offsetZ);
    }

    @Override
    public @NotNull ExpandableBlockRegion expand(final int x, final int y, final int z) {
        if (this.contains(x, y, z)) {
            return this;
        }
        return new CuboidBlockRegion(
                Math.min(this.minX, x), Math.min(this.minY, y), Math.min(this.minZ, z),
                Math.max(this.maxX, x), Math.max(this.maxY, y), Math.max(this.maxZ, z)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CuboidBlockRegion that = (CuboidBlockRegion) o;
        return this.minX == that.minX && this.minY == that.minY && this.minZ == that.minZ && this.maxX == that.maxX && this.maxY == that.maxY && this.maxZ == that.maxZ;
    }

    @Override
    public int hashCode() {
        int result = this.minX;
        result = 31 * result + this.minY;
        result = 31 * result + this.minZ;
        result = 31 * result + this.maxX;
        result = 31 * result + this.maxY;
        result = 31 * result + this.maxZ;
        return result;
    }

    @Override
    public String toString() {
        return "CuboidBlockRegion{(" + this.minX + ", " + this.minY + ", " + this.minZ + ")-(" + this.maxX + ", " + this.maxY + ", " + this.maxZ + ")}";
    }

    private static class OutlineIterator implements Iterator<MutableBlockPos> {

        private int index;
        private int side;
        private int dx = 1;
        private int dz;
        private final int lengthX;
        private final int lengthZ;
        private final MutableBlockPos pos;

        OutlineIterator(@NotNull CuboidBlockRegion region, int y, int offsetX, int offsetZ) {
            this.lengthX = region.maxX - region.minX + (offsetX << 1);
            this.lengthZ = region.maxZ - region.minZ + (offsetZ << 1);
            this.pos = new MutableBlockPos(region.minX, y, region.minZ);
        }

        @Override
        public boolean hasNext() {
            return this.side < 4;
        }

        @Override
        public MutableBlockPos next() {
            if (this.side > 3) {
                throw new NoSuchElementException();
            }
            this.pos.setX(this.pos.x() + this.dx);
            this.pos.setZ(this.pos.z() + this.dz);

            final int length = (this.side & 1) == 0 ? this.lengthX : this.lengthZ;
            if (++this.index >= length) {
                this.side++;
                final int temp = this.dx;
                this.dx = -this.dz;
                this.dz = temp;
                this.index = 0;
            }

            return this.pos;
        }
    }
}
