package fr.devsylone.fkpi.rule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public final class PlaceBlockInCaveRule {

    private final boolean active;
    private final int minimumBlocks;

    private PlaceBlockInCaveRule(boolean active, int minimumBlocks) {
        this.active = active;
        this.minimumBlocks = minimumBlocks;
    }

    public static @NotNull PlaceBlockInCaveRule active() {
        return new PlaceBlockInCaveRule(true, 3);
    }

    public static @NotNull PlaceBlockInCaveRule active(@Range(from = 1, to = 256) int minimumBlocks) {
        // noinspection ConstantConditions
        if (minimumBlocks < 1 || minimumBlocks > 256) {
            throw new IllegalArgumentException("The number of blocks to check is invalid.");
        }
        return new PlaceBlockInCaveRule(true, minimumBlocks);
    }

    public static @NotNull PlaceBlockInCaveRule notActive() {
        return new PlaceBlockInCaveRule(false, 3);
    }

    public boolean isActive() {
        return this.active;
    }

    public int minimumBlocks() {
        return this.minimumBlocks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaceBlockInCaveRule that = (PlaceBlockInCaveRule) o;
        return this.active == that.active && this.minimumBlocks == that.minimumBlocks;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(this.active) * this.minimumBlocks;
    }

    @Override
    public String toString() {
        String result = "PlaceBlockInCaveRule{";
        if (this.active) {
            return result + "active[" + this.minimumBlocks + "]}";
        } else {
            return result + "inactive}";
        }
    }
}
