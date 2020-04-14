package fr.devsylone.fkpi.rules;

import org.bukkit.configuration.ConfigurationSection;

public class PlaceBlockInCave implements RuleValue
{
	private boolean active = false;
	private int minimumBlocks = 3;

	public boolean isActive()
	{
		return active;
	}

	public int getMinimumBlocks()
	{
		return minimumBlocks;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public void setMinimumBlocks(int value)
	{
		minimumBlocks = value;
	}

	@Override
	public String format()
	{
		return active ? "§e" + minimumBlocks + " blocs" : "§4✘";
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("value", active);
		config.set("MinimumBlocs", minimumBlocks);
	}

	@Override
	public void load(ConfigurationSection config)
	{
		active = config.getBoolean("value", this.active);
		minimumBlocks = config.getInt("MinimumBlocs", this.minimumBlocks);
	}
}
