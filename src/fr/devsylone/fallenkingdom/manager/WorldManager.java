package fr.devsylone.fallenkingdom.manager;

import com.google.common.collect.ImmutableSet;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.managers.TeamManager;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WorldManager {

    private final boolean perWorld;
    private final Set<UUID> affectedWorlds;
    private final Set<UUID> baseWorldsCache = new HashSet<>();

    public WorldManager(Fk plugin) {
        perWorld = plugin.getConfig().getBoolean("world-check", false);
        if (!perWorld) {
            affectedWorlds = Collections.emptySet();
            return;
        }
        ImmutableSet.Builder<UUID> builder = ImmutableSet.builder();
        plugin.getConfig().getStringList("affected-worlds")
                .forEach(current -> {
                    World world = Bukkit.getWorld(current);
                    if (world == null) {
                        plugin.getLogger().warning("World " + current + " not found!");
                    } else {
                        builder.add(world.getUID());
                    }
                });
        affectedWorlds = builder.build();
    }

    public Set<UUID> getAffectedWorlds() {
        return affectedWorlds;
    }

    public boolean isAffected(World world) {
        if (!perWorld) {
            return true;
        }
        return affectedWorlds.contains(world.getUID());
    }

    public boolean isWorldWithBase(World world) {
        return isAffected(world) && baseWorldsCache.contains(world.getUID());
    }

    public void invalidateBaseWorldsCache(TeamManager teamManager) {
        baseWorldsCache.clear();
        for (Team team : teamManager.getTeams()) {
            if (team.getBase() != null && team.getBase().getCenter().getWorld() != null) {
                baseWorldsCache.add(team.getBase().getCenter().getWorld().getUID());
            }
        }
    }
}
