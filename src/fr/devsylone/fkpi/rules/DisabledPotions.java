package fr.devsylone.fkpi.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.devsylone.fkpi.api.event.RuleChangeEvent;
import fr.devsylone.fkpi.util.XPotionData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionType;

public class DisabledPotions implements RuleValue
{
	private final List<XPotionData> effects = new ArrayList<>();

	public boolean isDisabled(XPotionData potionData)
	{
		return potionData != null && effects.contains(potionData);
	}

	public boolean disablePotion(XPotionData potionData)
	{
		if(!isDisabled(potionData))
		{
			Bukkit.getPluginManager().callEvent(new RuleChangeEvent<>(Rule.DISABLED_POTIONS, this));
			effects.add(potionData);
		}
		else
			return false;
		return true;
	}

	public boolean enablePotion(XPotionData potionData)
	{
		if(isDisabled(potionData))
		{
			Bukkit.getPluginManager().callEvent(new RuleChangeEvent<>(Rule.DISABLED_POTIONS, this));
			effects.remove(potionData);
		}
		else
			return false;
		return true;
	}

	@Override
	public String format()
	{
		StringBuilder formatted = new StringBuilder();
		for(XPotionData data : effects)
			formatted.append("\n" + "§c✘ ").append(data.getType().name()).append(data.isExtended() ? " + redstone" : data.isUpgraded() ? " + glowstone" : "");
		return formatted.toString();
	}

	@Override
	public void save(ConfigurationSection config)
	{
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
		for(String key : config.getKeys(false))
			effects.add(new XPotionData(PotionType.valueOf(config.getString(key + ".Type")), config.getBoolean(key + ".Extended"), config.getBoolean(key + ".Upgraded")));
	}

	@Override
	public String toString()
	{
		return "[" + effects.stream()
				.map(XPotionData::toString)
				.collect(Collectors.joining(", ")) + "]";
	}
}
