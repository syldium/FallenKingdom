package fr.devsylone.fkpi.team;

import fr.devsylone.fkpi.region.BlockRegion;
import fr.devsylone.fkpi.pos.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Set;

public interface ChestRoom extends BlockRegion {

    @NotNull Base base();

    boolean isDefined();

    boolean register(@NotNull BlockPos chestPos);

    boolean unregister(@NotNull BlockPos chestPos);

    @NotNull @UnmodifiableView Set<BlockPos> chests();

    default @NotNull FkTeam team() {
        return this.base().team();
    }
}
