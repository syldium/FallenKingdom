package fr.devsylone.fkpi.managers;

import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fkpi.util.Saveable;

import static fr.devsylone.fallenkingdom.display.tick.TimerTickFormatter.TICKS_PER_MINUTE;

public class ChestsRoomsManager implements Saveable
{
	private int captureTime = 60;
	private int coreHealth = 500;
	private int regenerationPerMinute = 20;
	private int regenerationPerMinutePerAlly = 10;
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

	public int getRegenerationPerMinute()
	{
		return regenerationPerMinute;
	}

	public int getRegenerationPerMinutePerAlly()
	{
		return regenerationPerMinutePerAlly;
	}

	public int getRegenerationForTicks(int ticks, int allies)
	{
		return ticks * (regenerationPerMinute + allies * regenerationPerMinutePerAlly) / TICKS_PER_MINUTE;
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

	public void setCoreHealth(int coreHealth)
	{
		this.coreHealth = coreHealth;
	}

	public void setRegenerationPerMinute(int regenerationPerMinute)
	{
		this.regenerationPerMinute = regenerationPerMinute;
	}

	public void setRegenerationPerMinutePerAlly(int regenerationPerMinutePerAlly)
	{
		this.regenerationPerMinutePerAlly = regenerationPerMinutePerAlly;
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
		regenerationPerMinute = config.getInt("RegenerationPerMinute", 20);
		regenerationPerMinutePerAlly = config.getInt("RegenerationPerMinutePerAlly", 10);
		offset = config.getInt("Offset", 2);
		enabled = config.getBoolean("Enabled", true);
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("CaptureTime", captureTime);
		config.set("CoreHealth", coreHealth);
		config.set("RegenerationPerMinute", regenerationPerMinute);
		config.set("RegenerationPerMinutePerAlly", regenerationPerMinutePerAlly);
		config.set("Offset", offset);
		config.set("Enabled", enabled);
	}
}
