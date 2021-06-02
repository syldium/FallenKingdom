package fr.devsylone.fkpi.region;

import fr.devsylone.fkpi.pos.MutableBlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;

final class EmptyBlockRegion implements ExpandableBlockRegion {

    @Override
    public boolean contains(int x, int y, int z, int offset) {
        return false;
    }

    @Override
    public @NotNull Iterator<@NotNull MutableBlockPos> iterateOutwards(int y, int offsetX, int offsetZ) {
        return Collections.emptyIterator();
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
