package fr.devsylone.fallenkingdom.manager;

import com.google.common.collect.ImmutableList;
import fr.devsylone.fallenkingdom.Fk;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WorldManager {

    private final boolean perWorld;
    private final List<UUID> affectedWorlds;

    public WorldManager(Fk plugin) {
        perWorld = plugin.getConfig().getBoolean("world-check", false);
        if (!perWorld) {
            affectedWorlds = Collections.emptyList();
            return;
        }
        ImmutableList.Builder<UUID> builder = ImmutableList.builder();
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

    public List<UUID> getAffectedWorlds() {
        return affectedWorlds;
    }

    public boolean isAffected(World world) {
        if (!perWorld) {
            return true;
        }
        return affectedWorlds.contains(world.getUID());
    }
}
