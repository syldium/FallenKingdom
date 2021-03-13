package fr.devsylone.fkpi.rules;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.api.event.RuleChangeEvent;
import org.bukkit.Bukkit;
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
		if (active != this.active)
			Bukkit.getPluginManager().callEvent(new RuleChangeEvent<>(Rule.PLACE_BLOCK_IN_CAVE, this));
		this.active = active;
	}

	public void setMinimumBlocks(int value)
	{
		if (value != this.minimumBlocks)
			Bukkit.getPluginManager().callEvent(new RuleChangeEvent<>(Rule.PLACE_BLOCK_IN_CAVE, this));
		minimumBlocks = value;
	}

	@Override
	public String format()
	{
		return active ? "§e" + minimumBlocks + " " + Messages.UNIT_BLOCKS.getMessage() : "§4✘";
	}

	@Override
	public JsonElement toJSON()
	{
		return new Gson().toJsonTree(this);
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

	@Override
	public String toString()
	{
		return active + "(" + minimumBlocks + ")";
	}
}
