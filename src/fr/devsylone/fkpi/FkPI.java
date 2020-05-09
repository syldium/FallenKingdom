package fr.devsylone.fkpi;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.rules.AllowedBlocks;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.util.BlockDescription;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import fr.devsylone.fkpi.managers.ChestsRoomsManager;
import fr.devsylone.fkpi.managers.LockedChestsManager;
import fr.devsylone.fkpi.managers.RulesManager;
import fr.devsylone.fkpi.managers.TeamManager;
import fr.devsylone.fkpi.util.Saveable;

import java.util.List;

public class FkPI implements Saveable
{
	private TeamManager teamManager;
	private RulesManager rulesManager;
	private LockedChestsManager lcManager;
	private ChestsRoomsManager crManager;

	private Fk plugin;

	private static FkPI instance;

	public static FkPI getInstance()
	{
		return instance;
	}

	public FkPI()
	{
		instance = this;

		teamManager = new TeamManager();
		rulesManager = new RulesManager();
		lcManager = new LockedChestsManager();
		crManager = new ChestsRoomsManager();
	}

	public FkPI(Fk plugin)
	{
		instance = this;

		this.plugin = plugin;

		teamManager = new TeamManager();
		rulesManager = new RulesManager();
		lcManager = new LockedChestsManager();
		crManager = new ChestsRoomsManager();

	}

	public RulesManager getRulesManager()
	{
		return rulesManager;
	}

	public TeamManager getTeamManager()
	{
		return teamManager;
	}

	public LockedChestsManager getLockedChestsManager()
	{
		return lcManager;
	}

	public ChestsRoomsManager getChestsRoomsManager()
	{
		return crManager;
	}

	/**
	 * @deprecated
	 */
	public void fromStringArray(List<String> strings)
	{
		for(String s : teamManager.getTeamNames())
			teamManager.removeTeam(s);

		rulesManager.setRule(Rule.ALLOWED_BLOCKS, new AllowedBlocks());

		for(String s : strings)
		{
			String[] args = s.split(" ");

			if(args[0].equalsIgnoreCase("rules"))
			{
				if(args[1].equalsIgnoreCase("AllowedBlocks"))
				{
					rulesManager.getRule(Rule.ALLOWED_BLOCKS).getValue().add(new BlockDescription(args[2]));
				}

				else if(args[1].equalsIgnoreCase("PlaceBlockInCave"))
				{
					if(args[2].equalsIgnoreCase("value"))
						rulesManager.getRule(Rule.PLACE_BLOCK_IN_CAVE).setActive(Boolean.parseBoolean(args[3]));
					else
						rulesManager.getRule(Rule.PLACE_BLOCK_IN_CAVE).setMinimumBlocks(Integer.parseInt(args[3]));
				}

				else if(isInteger(args[2]))
					rulesManager.setRuleByName(args[1], Integer.parseInt(args[2]));

				else if(isBoolean(args[2]))
					rulesManager.setRuleByName(args[1], Boolean.parseBoolean(args[2]));

				else
					rulesManager.setRuleByName(args[1], args[2]);
			}
			else if(args[0].equalsIgnoreCase("teams"))
			{
				if(args[1].equalsIgnoreCase("create"))
				{
					String name = "";
					for(int i = 2; i < args.length; i++)
						name += " " + args[i];

					teamManager.createTeam(name.substring(1));
				}

				else if(args[1].equalsIgnoreCase("addPlayer"))
					teamManager.addPlayer(args[2], args[3]);
			}

		}
	}

	private boolean isInteger(String s)
	{
		try
		{
			Integer.parseInt(s);
			return true;
		}catch(NumberFormatException e)
		{
			return false;
		}
	}

	private boolean isBoolean(String s)
	{
		return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
	}

	public void reset()
	{
		teamManager = new TeamManager();
		rulesManager = new RulesManager();
		lcManager = new LockedChestsManager();
		crManager = new ChestsRoomsManager();
		//rulesManager.loadDefaultConfig();
	}

	@Override
	public void load(ConfigurationSection config)
	{
		crManager.load(config.getConfigurationSection("ChestsRoomsManager")); //AVANT TEAMMANAGER

		rulesManager.load(config.getConfigurationSection("RulesManager"));
		teamManager.load(config.getConfigurationSection("TeamManager"));
		lcManager.load(config.getConfigurationSection("LockedChestsManager"));
	}

	@Override
	public void save(ConfigurationSection config)
	{
		rulesManager.save(config.createSection("RulesManager"));
		teamManager.save(config.createSection("TeamManager"));
		lcManager.save(config.createSection("LockedChestsManager"));
		crManager.save(config.createSection("ChestsRoomsManager"));

	}

	public JavaPlugin getPlugin()
	{
		return plugin;
	}
}
