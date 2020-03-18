package fr.devsylone.fkpi.rules;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionType;

import fr.devsylone.fkpi.util.XPotionData;

public class DisabledPotions extends Rule
{
	public DisabledPotions()
	{
		super("DisabledPotions", new ArrayList<>());
	}

	public DisabledPotions(List<XPotionData> disabledEffects)
	{
		super("DisabledPotions", disabledEffects);
	}

	@Override
	public List<XPotionData> getValue()
	{
		return (List<XPotionData>) value;
	}

	public boolean isDisabled(XPotionData potionData)
	{
		return potionData != null && getValue().contains(potionData);
	}

	public boolean disablePotion(XPotionData potionData)
	{
		if(!isDisabled(potionData))
			getValue().add(potionData);
		else
			return false;
		return true;
	}

	public boolean enablePotion(XPotionData potionData)
	{
		if(isDisabled(potionData))
			getValue().remove(potionData);
		else
			return false;
		return true;
	}

	@Override
	public void save(ConfigurationSection config)
	{
		List<XPotionData> effects = getValue();
		for(int i = 0; i < effects.size(); i++)
		{
			config.set(i + ".Type", effects.get(i).getType().name());
			config.set(i + ".Extended", effects.get(i).isExtended());
			config.set(i + ".Upgraded", effects.get(i).isUpgraded());
		}
	}

	@Override
	public void load(ConfigurationSection config)
	{
		List<XPotionData> effects = new ArrayList<XPotionData>();
		for(String key : config.getKeys(false))
			effects.add(new XPotionData(PotionType.valueOf(config.getString(key + ".Type")), config.getBoolean(key + ".Extended"), config.getBoolean(key + ".Upgraded")));
		setValue(effects);
	}
}
