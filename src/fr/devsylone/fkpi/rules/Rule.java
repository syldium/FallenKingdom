package fr.devsylone.fkpi.rules;

import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fkpi.util.Saveable;


public class Rule implements Saveable
{
	protected String name;
	protected Object value;

	public Rule(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}

	public Rule(String name)
	{
		this.name = name;
	}

	public void setValue(Object value)
	{
		if(!value.getClass().isAssignableFrom(getValueType()))
			throw new FkLightException("Le type de valeur ne convient pas à la règle");

		this.value = value;
	}

	public Object getValue()
	{
		return value;
	}

	public String getName()
	{
		return name;
	}

	public Class<?> getValueType()
	{
		return value.getClass();
	}

	@Override
	public void load(ConfigurationSection config)
	{
		value = config.get("value");
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("value", value);
	}

	@Override
	public String toString()
	{
		return "Name [" + name + "], Value [" + value+"]";
	}
}
