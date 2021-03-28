package fr.devsylone.fkpi.managers;

import fr.devsylone.fkpi.lockedchests.LockedChest;
import fr.devsylone.fkpi.util.Saveable;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LockedChestsManager implements Saveable
{
	private final Map<Location, LockedChest> chests = new HashMap<>();

	public void addOrEdit(@NotNull LockedChest chest)
	{
		chests.put(chest.getLocation(), chest);
	}

	public @Nullable LockedChest getChestAt(@NotNull Location loc)
	{
		return chests.get(loc);
	}

	public boolean remove(@NotNull Location loc)
	{
		return chests.remove(loc) != null;
	}

	public @NotNull List<LockedChest> getChestList()
	{
		return new ArrayList<>(chests.values());
	}

	public @NotNull Collection<LockedChest> getChests()
	{
		return chests.values();
	}

	public @NotNull Map<Location, LockedChest> getChestMap()
	{
		return chests;
	}

	@Override
	public void load(ConfigurationSection config)
	{
		if(config.isConfigurationSection("LockedChests"))
			for(String chest : config.getConfigurationSection("LockedChests").getKeys(false))
			{
				LockedChest c = new LockedChest(null, 0, 0, "Empty");
				c.load(config.getConfigurationSection("LockedChests." + chest));
				if(c.getLocation().getWorld() != null)
					addOrEdit(c);
			}
	}

	@Override
	public void save(ConfigurationSection config)
	{
		for(LockedChest chest : chests.values())
		{
			chest.save(config.createSection("LockedChests." + chest.getLocation().getBlockX() + "-" + chest.getLocation().getBlockY() + "-" + chest.getLocation().getBlockZ()));
		}
	}
}
