package fr.devsylone.fkpi.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fkpi.rules.AllowedBlocks;
import fr.devsylone.fkpi.rules.ChargedCreepers;
import fr.devsylone.fkpi.rules.DisabledPotions;
import fr.devsylone.fkpi.rules.PlaceBlockInCave;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.util.Saveable;
import fr.devsylone.fkpi.util.XPotionData;

public class RulesManager implements Saveable
{
	private List<Rule> rules;

	public RulesManager()
	{
		rules = new ArrayList<Rule>();
		
		loadDefaultConfig();
	}

	public Rule getRuleByName(String name)
	{
		for(int i = 0; i < rules.size(); i++)
			if(rules.get(i).getName().equalsIgnoreCase(name))
				return rules.get(i);

		throw new FkLightException("Cette règle n'existe pas.");
	}

	public List<Rule> getRulesList()
	{
		return rules;
	}

	private void registerNewRule(Rule rule)
	{
		if(isRule(rule.getName()))
			rules.remove(getRuleByName(rule.getName()));

		rules.add(rule);
	}

	public void loadDefaultConfig()
	{
		registerNewRule(new Rule("PvpCap", new Integer(3)));
		registerNewRule(new Rule("TntCap", new Integer(6)));
		registerNewRule(new Rule("NetherCap", new Integer(1)));
		registerNewRule(new Rule("EndCap", new Integer(1)));
		registerNewRule(new Rule("DeathLimit", new Integer(0)));
		registerNewRule(new Rule("ChestLimit", new Integer(20)));
		registerNewRule(new Rule("FriendlyFire", new Boolean(true)));
		registerNewRule(new Rule("EternalDay", new Boolean(false)));
		registerNewRule(new Rule("DayDuration", new Integer(24000)));
		registerNewRule(new Rule("DoPauseAfterDay", new Boolean(false)));
		registerNewRule(new Rule("DeepPause", new Boolean(true)));
		registerNewRule(new ChargedCreepers(10, 50, 1));
		registerNewRule(new PlaceBlockInCave(new Boolean(false), 3));
		registerNewRule(new Rule("TntJump", new Boolean(true)));
		registerNewRule(new Rule("RespawnAtHome", new Boolean(false)));
		registerNewRule(new Rule("HealthBelowName", new Boolean(true)));
		registerNewRule(new DisabledPotions(new ArrayList<XPotionData>()));

		AllowedBlocks allowedBlocks = new AllowedBlocks();
		allowedBlocks.fillWithDefaultValues();
		registerNewRule(allowedBlocks);
	}

	public void load(ConfigurationSection config)
	{
		for(String key : config.getConfigurationSection("Rules").getKeys(false))
		{
			Rule r = new Rule(key);
			if(key.equalsIgnoreCase("ChargedCreepers"))
				r = new ChargedCreepers();

			else if(key.equalsIgnoreCase("PlaceBlockInCave"))
				r = new PlaceBlockInCave();

			else if(key.equalsIgnoreCase("AllowedBlocks"))
				r = new AllowedBlocks();
			
			else if(key.equalsIgnoreCase("DisabledPotions"))
				r = new DisabledPotions();

			r.load(config.getConfigurationSection("Rules." + key));
			registerNewRule(r);
		}
	}

	public void save(ConfigurationSection config)
	{
		for(Rule r : getRulesList())
			r.save(config.createSection("Rules." + r.getName()));

	}

	private boolean isRule(String name)
	{
		try
		{
			getRuleByName(name);
		}catch(FkLightException ignore)
		{
			return false;
		}
		return true;
	}

}