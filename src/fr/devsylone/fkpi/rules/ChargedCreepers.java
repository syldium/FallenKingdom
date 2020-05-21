package fr.devsylone.fkpi.rules;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import fr.devsylone.fkpi.api.event.RuleChangeEvent;
import org.bukkit.Bukkit;
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

	public void setValue(int spawn, int drop, int amount)
	{
		Bukkit.getPluginManager().callEvent(new RuleChangeEvent<>(Rule.CHARGED_CREEPERS, this));
		setSpawn(spawn);
		setDrop(drop);
		setTntAmount(amount);
	}

	@Override
	public String format()
	{
		return "§e" + spawn + "% spawn - " + drop + "% drop - " + tntAmount + " tnt(s)";
	}

	@Override
	public JsonElement toJSON()
	{
		return new Gson().toJsonTree(this);
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

	@Override
	public String toString()
	{
		return "[" + spawn + ", " + drop + ", " + tntAmount + "]";
	}
}
