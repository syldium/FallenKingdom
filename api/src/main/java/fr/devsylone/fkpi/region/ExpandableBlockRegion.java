package fr.devsylone.fkpi.region;

import org.jetbrains.annotations.NotNull;

public interface ExpandableBlockRegion extends BlockRegion {

    /**
     * Expand the region with coordinates to include.
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     * @param z The Z coordinate
     * @return A new region if modified, otherwise this.
     */
    @NotNull ExpandableBlockRegion expand(int x, int y, int z);
}
