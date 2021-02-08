package fr.devsylone.fallenkingdom.manager.saveable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.cryptomorin.xseries.XMaterial;
import fr.devsylone.fallenkingdom.utils.ConfigHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.util.Saveable;

public class PortalsManager implements Saveable
{
	private final Set<Location> portals = new HashSet<>();

	public void addPortal(Location loc)
	{
		portals.add(loc);
	}

	public void enablePortals()
	{
		Material caveAir = XMaterial.CAVE_AIR.parseMaterial();
		for(final Location loc : portals)
		{
			if(loc != null && loc.getBlock() != null && (loc.getBlock().getType().equals(Material.AIR) || loc.getBlock().getType().equals(caveAir)))
			{
				loc.getBlock().setType(Material.FIRE);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> {
					if(loc.getBlock().getType().equals(Material.FIRE))
						loc.getBlock().setType(Material.AIR);
				}, 20L);

			}
		}
	}

	@Override
	public void load(ConfigurationSection config)
	{
		for(String serialized : config.getStringList("Portals"))
		{
			Location location = ConfigHelper.getLocation(serialized);
			if(location != null)
				addPortal(location);
		}
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("Portals", portals.stream().map(ConfigHelper::serializeBlockPos).collect(Collectors.toList()));
	}
}
