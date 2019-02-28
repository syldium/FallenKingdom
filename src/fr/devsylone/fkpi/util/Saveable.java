package fr.devsylone.fkpi.util;

import org.bukkit.configuration.ConfigurationSection;

public interface Saveable
{	
	public void load(ConfigurationSection config);
	
	public void save(ConfigurationSection config);
}