package fr.devsylone.fkpi.team;

import fr.devsylone.fkpi.util.BlockPos;
import fr.devsylone.fkpi.region.BlockRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface Base extends BlockRegion {

    @NotNull BlockPos center();

    @NotNull String worldName();

    default @NotNull Location bukkitCenterLoc() {
        final BlockPos pos = this.center();
        return new Location(Bukkit.getWorld(this.worldName()), pos.x, pos.y, pos.z);
    }

    int radius();

    @NotNull ChestRoom chestRoom();

    @NotNull FkTeam team();
}
