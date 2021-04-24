package fr.devsylone.fallenkingdom.team;

import fr.devsylone.fkpi.region.CuboidBlockRegion;
import fr.devsylone.fkpi.team.Base;
import fr.devsylone.fkpi.team.ChestRoom;
import fr.devsylone.fkpi.team.FkTeam;
import fr.devsylone.fkpi.util.BlockPos;
import fr.devsylone.fkpi.region.BlockRegion;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class BaseImpl implements Base {

    private final FkTeam team;
    private final ChestRoom chestRoom;
    private final BlockPos center;
    private final String worldName;
    private final BlockRegion region;
    private final int radius;

    public BaseImpl(@NotNull FkTeam team, @NotNull BlockPos center, @NotNull String worldName, int radius) {
        this.team = team;
        this.chestRoom = new ChestRoomImpl(this, Collections.emptySet());
        this.center = center;
        this.worldName = worldName;
        this.region = new CuboidBlockRegion(center, radius);
        this.radius = radius;
    }

    @Override
    public @NotNull BlockPos center() {
        return this.center;
    }

    @Override
    public @NotNull String worldName() {
        return this.worldName;
    }

    @Override
    public int radius() {
        return this.radius;
    }

    @Override
    public @NotNull ChestRoom chestRoom() {
        return this.chestRoom;
    }

    @Override
    public @NotNull FkTeam team() {
        return this.team;
    }

    @Override
    public boolean contains(int x, int y, int z, int offset) {
        return this.region.contains(x, y, z, offset);
    }

    @Override
    public String toString() {
        return "BaseImpl{" +
                "chestRoom=" + this.chestRoom +
                ", region=" + this.region +
                '}';
    }
}
