package fr.devsylone.fkpi.managers;

import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fkpi.util.Saveable;

public class ChestsRoomsManager implements Saveable
{
	private int captureTime;
	private int offset;
	private boolean enabled;

	public ChestsRoomsManager()
	{
		captureTime = 0;
		offset = 0;
		enabled = true;
	}

	public int getCaptureTime()
	{
		return captureTime;
	}

	public int getOffset()
	{
		return offset;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void setCaptureTime(int arg)
	{
		captureTime = arg;
	}

	public void setOffset(int arg)
	{
		offset = arg;
	}
	
	public void setEnabled(boolean arg)
	{
		enabled = arg;
	}

	@Override
	public void load(ConfigurationSection config)
	{
		captureTime = config.getInt("CaptureTime");
		offset = config.getInt("Offset");
		enabled = config.getBoolean("Enabled");
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("CaptureTime", captureTime);
		config.set("Offset", offset);
		config.set("Enabled", enabled);
	}
}
