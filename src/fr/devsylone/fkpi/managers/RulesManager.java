package fr.devsylone.fkpi.managers;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import fr.devsylone.fkpi.api.event.RuleChangeEvent;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.rules.RuleValue;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fkpi.util.Saveable;

public class RulesManager implements Saveable
{
	private final Map<Rule<?>, Object> rules = new LinkedHashMap<>();

	public RulesManager()
	{
		Rule.values().forEach((Rule<?> rule) -> rules.put(rule, rule.getDefaultValue()));
	}

	@SuppressWarnings("unchecked")
	public <T> T getRule(Rule<T> rule)
	{
		Preconditions.checkArgument(rules.containsKey(rule), "The rule doesn't seem to be loaded. Has the manager been initialized?");
		return (T) rules.get(rule);
	}

	public <T> void setRule(Rule<T> rule, T value)
	{
		Bukkit.getPluginManager().callEvent(new RuleChangeEvent<>(rule, value));
		rules.put(rule, value);
	}

	public Map<Rule<?>, Object> getRulesList()
	{
		return rules;
	}

	@Override
	public void load(ConfigurationSection config)
	{
		for (Map.Entry<Rule<?>, Object> entry : rules.entrySet()) {
			String configPath = "Rules." + entry.getKey().getName();

			if (entry.getKey().getDefaultValue() instanceof RuleValue) {
				RuleValue loaded = ((RuleValue) entry.getKey().getDefaultValue());
				if (config != null && config.contains(configPath))
					loaded.load(config.getConfigurationSection(configPath));
				else
					loaded.fillWithDefaultValue();
				rules.put(entry.getKey(), loaded);
			} else {
				Object defaultValue = entry.getKey().getDefaultValue();
				rules.put(entry.getKey(), config != null ? config.get(configPath + ".value", defaultValue) : defaultValue);
			}
		}
	}

	@Override
	public void loadNullable(ConfigurationSection config)
	{
		load(config); // Null-safe :sunglasses:
	}

	@Override
	public void save(ConfigurationSection config)
	{
		rules.forEach((Rule<?> rule, Object value) -> {
			if (value instanceof RuleValue) {
				((RuleValue) value).save(config.createSection("Rules." + rule.getName()));
			} else {
				config.set("Rules." + rule.getName() + ".value", value);
			}
		});
	}

	/**
	 * @deprecated
	 */
	public Object getRuleByName(String name)
	{
		return getRule(Rule.getByName(name));
	}

	/**
	 * @deprecated
	 */
	public void setRuleByName(String name, Object value)
	{
		setRule(Rule.getByName(name), value);
	}
}