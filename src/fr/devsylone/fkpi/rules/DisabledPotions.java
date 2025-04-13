package fr.devsylone.fkpi.rules;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import fr.devsylone.fkpi.api.event.RuleChangeEvent;
import fr.devsylone.fkpi.util.XPotionData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

public class DisabledPotions implements RuleValue
{
	private final Set<XPotionData> effects = new HashSet<>();

	public Set<XPotionData> getValue()
	{
		return effects;
	}

	public boolean isDisabled(XPotionData potionData)
	{
		return potionData != null && effects.contains(potionData);
	}

	public boolean disablePotion(XPotionData potionData)
	{
		if(effects.add(potionData))
		{
			Bukkit.getPluginManager().callEvent(new RuleChangeEvent<>(Rule.DISABLED_POTIONS, this));
			return true;
		}
		return false;
	}

	public boolean enablePotion(XPotionData potionData)
	{
		if(effects.remove(potionData))
		{
			Bukkit.getPluginManager().callEvent(new RuleChangeEvent<>(Rule.DISABLED_POTIONS, this));
			return true;
		}
		return false;
	}

	/**
	 * Change l'interdiction d'un effet de potion.
	 *
	 * @param data L'effet de potion à changer
	 * @return {@code false} si l'effet a été interdit, {@code true} s'il a été autorisé
	 */
	public boolean togglePotion(@NotNull XPotionData data) {
		if (disablePotion(data)) {
			return false;
		} else {
			return enablePotion(data);
		}
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
	public JsonElement toJSON()
	{
		Gson gson = new Gson();
		JsonArray jsonArray = new JsonArray();
		for (XPotionData potion : getValue()) {
			jsonArray.add(gson.toJsonTree(potion));
		}
		return jsonArray;
	}

	@Override
	public void save(ConfigurationSection config)
	{
		int i = 0;
		for(XPotionData potion : effects)
		{
			config.set(i + ".Type", potion.getType().name());
			config.set(i + ".Extended", potion.isExtended());
			config.set(i + ".Upgraded", potion.isUpgraded());
			i++;
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
