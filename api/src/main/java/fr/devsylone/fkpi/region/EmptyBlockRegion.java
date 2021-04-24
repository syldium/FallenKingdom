package fr.devsylone.fkpi.region;

import org.jetbrains.annotations.NotNull;

final class EmptyBlockRegion implements ExpandableBlockRegion {

    @Override
    public boolean contains(int x, int y, int z, int offset) {
        return false;
    }

    @Override
    public @NotNull ExpandableBlockRegion expand(int x, int y, int z) {
        return new CuboidBlockRegion(x, y, z, 0);
    }

    @Override
    public String toString() {
        return "EmptyBlockRegion";
    }
}
