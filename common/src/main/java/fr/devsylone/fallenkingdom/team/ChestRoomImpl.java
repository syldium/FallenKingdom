package fr.devsylone.fallenkingdom.team;

import fr.devsylone.fkpi.region.ExpandableBlockRegion;
import fr.devsylone.fkpi.team.Base;
import fr.devsylone.fkpi.team.ChestRoom;
import fr.devsylone.fkpi.util.BlockPos;
import fr.devsylone.fkpi.region.BlockRegion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChestRoomImpl implements ChestRoom {

    private final Base base;
    private final Set<BlockPos> chests;
    private ExpandableBlockRegion region;

    public ChestRoomImpl(@NotNull Base base, @NotNull Collection<BlockPos> chests) {
        this.base = base;
        this.chests = new HashSet<>(chests);
        this.region = BlockRegion.fromPositions(chests);
    }

    @TestOnly
    ChestRoomImpl(@NotNull Collection<BlockPos> chests) {
        this.base = null;
        this.chests = new HashSet<>(chests);
        this.region = BlockRegion.fromPositions(chests);
    }

    @Override
    public boolean register(final @NotNull BlockPos chestPos) {
        if (!this.chests.add(chestPos)) {
            return false;
        }
        this.region = this.region.expand(chestPos.x, chestPos.y, chestPos.z);
        return true;
    }

    @Override
    public boolean unregister(@NotNull BlockPos chestPos) {
        if (!this.chests.remove(chestPos)) {
            return false;
        }
        this.region = BlockRegion.fromPositions(this.chests);
        return true;
    }

    @Override
    public @NotNull Set<BlockPos> chests() {
        return Collections.unmodifiableSet(this.chests);
    }

    @Override
    public @NotNull Base base() {
        return this.base;
    }

    @Override
    public boolean isDefined() {
        return !this.chests.isEmpty();
    }

    @Override
    public boolean contains(int x, int y, int z, int offset) {
        return this.region.contains(x, y, z, offset);
    }

    @Override
    public String toString() {
        return "ChestRoom{" +
                "team=" + this.base.team() +
                ", chests=" + this.chests.size() +
                ", region=" + this.region +
                '}';
    }
}
