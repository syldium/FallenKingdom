package fr.devsylone.fkpi.rules;

import org.bukkit.configuration.ConfigurationSection;

public class ChargedCreepers implements RuleValue
{
	private int spawn = 10;
	private int drop = 50;
	private int tntAmount = 1;

	public int getSpawn()
	{
		return spawn;
	}

	public int getDrop()
	{
		return drop;
	}

	public int getTntAmount()
	{
		return tntAmount;
	}

	public void setValue(int spawn, int drop, int amount)
	{
		setSpawn(spawn);
		setDrop(drop);
		setTntAmount(amount);
	}

	public void setSpawn(int spawn)
	{
		this.spawn = spawn;
	}

	public void setDrop(int drop)
	{
		this.drop = drop;
	}

	public void setTntAmount(int amount)
	{
		this.tntAmount = amount;
	}

	@Override
	public String format()
	{
		return "§e" + spawn + "% spawn - " + drop + "% drop - " + tntAmount + " tnt(s)";
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("value", spawn * 1000000 + drop * 1000 + tntAmount);
	}

	@Override
	public void load(ConfigurationSection config)
	{
		int value = config.getInt("value", 10050001);
		spawn = value / 1000000;
		drop = (value / 1000) - (value / 1000000) * 1000;
		tntAmount = value - ((value / 1000) * 1000);
	}
}
