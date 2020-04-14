package fr.devsylone.fkpi.managers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.rules.RuleValue;
import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fkpi.util.Saveable;

public class RulesManager implements Saveable
{
	private final Map<Rule<?>, Object> rules = new LinkedHashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T getRule(Rule<T> rule)
	{
		return (T) rules.get(rule);
	}

	public <T> void setRule(Rule<T> rule, T value)
	{
		rules.put(rule, value);
	}

	public Map<Rule<?>, Object> getRulesList()
	{
		return rules;
	}

	@Override
	public void load(ConfigurationSection config)
	{
		Rule.values().forEach((Rule rule) -> {
			String configPath = "Rules." + rule.getName();

			if (rule.getDefaultValue() instanceof RuleValue) {
				RuleValue loaded = ((RuleValue) rule.getDefaultValue());
				if (config.contains(configPath))
					loaded.load(config.getConfigurationSection(configPath));
				else
					loaded.fillWithDefaultValue();
				rules.put(rule, loaded);
			} else {
				rules.put(rule, config.get(configPath + ".value", rule.getDefaultValue()));
			}
		});
	}

	@Override
	public void save(ConfigurationSection config)
	{
		rules.forEach((Rule rule, Object value) -> {
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