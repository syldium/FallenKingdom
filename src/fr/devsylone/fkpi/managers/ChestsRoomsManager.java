package fr.devsylone.fkpi.managers;

import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fkpi.util.Saveable;

public class ChestsRoomsManager implements Saveable
{
	private int captureTime = 60;
	private int coreHealth = 500;
	private int offset = 2;
	private boolean enabled = true;

	public int getCaptureTime()
	{
		return captureTime;
	}

	public int getCoreHealth()
	{
		return coreHealth;
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
		captureTime = config.getInt("CaptureTime", 60);
		coreHealth = config.getInt("CoreHealth", 500);
		offset = config.getInt("Offset", 2);
		enabled = config.getBoolean("Enabled", true);
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("CaptureTime", captureTime);
		config.set("CoreHealth", coreHealth);
		config.set("Offset", offset);
		config.set("Enabled", enabled);
	}
}
