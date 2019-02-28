package fr.devsylone.fkpi.rules;

import org.bukkit.configuration.ConfigurationSection;

public class PlaceBlockInCave extends Rule
{
	private int minimumBlocks;

	public PlaceBlockInCave(Boolean value)
	{
		this(value, 3);
	}

	public PlaceBlockInCave(Boolean value, int blocs)
	{
		super("PlaceBlockInCave", value);
		minimumBlocks = blocs;
		this.value = value;
	}

	public PlaceBlockInCave()
	{
		this(null);
	}

	@Override
	public Boolean getValue()
	{
		return (Boolean) value;
	}

	public int getMinimumBlocks()
	{
		return minimumBlocks;
	}

	public void setMinimumBlocks(int value)
	{
		minimumBlocks = value;
	}

	@Override
	public void save(ConfigurationSection config)
	{
		super.save(config);
		config.set("MinimumBlocs", minimumBlocks);
	}

	@Override
	public void load(ConfigurationSection config)
	{
		super.load(config);
		minimumBlocks = config.getInt("MinimumBlocs");
	}

	@Override
	public String toString()
	{
		return super.toString() + ", MinBlocks [" + minimumBlocks + "]";
	}
}
