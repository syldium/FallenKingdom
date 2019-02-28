package fr.devsylone.fallenkingdom.manager.saveable;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.util.Saveable;

public class PortalsManager implements Saveable
{
	private List<Location> portals;

	public PortalsManager()
	{
		portals = new ArrayList<Location>();
	}

	public void addPortal(Location loc)
	{
		portals.add(loc);
	}

	public void enablePortals()
	{
		for(final Location loc : portals)
		{
			if(loc != null && loc.getBlock() != null && loc.getBlock().getType().equals(Material.AIR))
			{
				loc.getBlock().setType(Material.FIRE);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
				{

					@Override
					public void run()
					{
						if(loc.getBlock().getType().equals(Material.FIRE))
							loc.getBlock().setType(Material.AIR);
					}
				}, 20l);

			}
		}
	}

	@Override
	public void load(ConfigurationSection config)
	{
		if(config.contains("Portals"))
			for(String loc : config.getStringList("Portals"))
			{
				String[] split = loc.split(":");
				addPortal(new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])));
			}
	}

	@Override
	public void save(ConfigurationSection config)
	{
		List<String> sList = new ArrayList<String>();

		for(Location l : portals)
			if(l != null && l.getWorld() != null)
				sList.add(l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ());

		config.set("Portals", sList);
	}
}
