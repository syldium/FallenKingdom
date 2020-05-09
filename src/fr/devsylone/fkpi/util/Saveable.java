package fr.devsylone.fkpi.util;

import org.bukkit.configuration.ConfigurationSection;

public interface Saveable
{	
	void load(ConfigurationSection config);
	
	void save(ConfigurationSection config);
}