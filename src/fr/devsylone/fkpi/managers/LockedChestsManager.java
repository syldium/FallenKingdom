package fr.devsylone.fkpi.managers;

import fr.devsylone.fkpi.lockedchests.LockedChest;
import fr.devsylone.fkpi.util.Saveable;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class LockedChestsManager implements Saveable
{
	private List<LockedChest> chests;

	public LockedChestsManager()
	{
		chests = new ArrayList<LockedChest>();
	}

	public void addOrEdit(LockedChest chest)
	{
		if(getChestAt(chest.getLocation()) != null)
			chests.remove(getChestAt(chest.getLocation()));

		chests.add(chest);
	}

	public LockedChest getChestAt(Location loc)
	{
		for(LockedChest chest : chests)
			if(chest.getLocation().equals(loc))
				return chest;
		return null;
	}

	public boolean remove(Location loc)
	{
		if(getChestAt(loc) == null)
			return false;

		chests.remove(getChestAt(loc));
		return true;
	}

	public List<LockedChest> getChestList()
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
		for(LockedChest chest : chests)
		{
			chest.save(config.createSection("LockedChests." + chest.getLocation().getBlockX() + "-" + chest.getLocation().getBlockY() + "-" + chest.getLocation().getBlockZ()));
		}
	}
}
