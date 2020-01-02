package fr.devsylone.fkpi;

import java.util.ArrayList;
import java.util.List;

import fr.devsylone.fkpi.util.BlockDescription;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import fr.devsylone.fkpi.managers.ChestsRoomsManager;
import fr.devsylone.fkpi.managers.LockedChestsManager;
import fr.devsylone.fkpi.managers.RulesManager;
import fr.devsylone.fkpi.managers.TeamManager;
import fr.devsylone.fkpi.rules.AllowedBlocks;
import fr.devsylone.fkpi.rules.PlaceBlockInCave;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.util.Saveable;

public class FkPI implements Saveable
{
	private TeamManager teamManager;
	private RulesManager rulesManager;
	private LockedChestsManager lcManager;
	private ChestsRoomsManager crManager;

	private JavaPlugin plugin;

	private boolean IS_BUKKIT_PLUGIN;

	private static FkPI instance;

	public static FkPI getInstance()
	{
		return instance;
	}

	public FkPI()
	{
		IS_BUKKIT_PLUGIN = false;
		instance = this;

		teamManager = new TeamManager();
		rulesManager = new RulesManager();
		lcManager = new LockedChestsManager();
		crManager = new ChestsRoomsManager();
	}

	public FkPI(JavaPlugin plugin)
	{
		IS_BUKKIT_PLUGIN = true;
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

	public boolean isBukkitPlugin()
	{
		return IS_BUKKIT_PLUGIN;
	}

	public ArrayList<String> toStringArray()
	{
		ArrayList<String> response = new ArrayList<String>();

		response.add("START_FKPI");

		for(Rule rule : rulesManager.getRulesList())
		{
			if(rule instanceof AllowedBlocks)
				for(BlockDescription b : ((AllowedBlocks) rule).getValue())
					response.add("rules " + rule.getName() + " " + b.toString());

			else if(rule instanceof PlaceBlockInCave)
			{
				response.add("rules " + rule.getName() + " value " + rule.getValue());
				response.add("rules " + rule.getName() + " minBlocks " + ((PlaceBlockInCave) rule).getMinimumBlocks());
			}
			else
			{
				response.add("rules " + rule.getName() + " " + rule.getValue());
			}
		}

		for(fr.devsylone.fkpi.teams.Team t : teamManager.getTeams())
		{
			response.add("teams create " + t.getName());
			if(t.getPlayers() != null && !t.getPlayers().isEmpty())
				for(String s : t.getPlayers())
					response.add("teams addPlayer " + s + " " + t.getName());
		}

		response.add("END");

		return response;
	}

	public void fromStringArray(List<String> strings)
	{
		for(String s : teamManager.getTeamNames())
			teamManager.removeTeam(s);

		rulesManager.getRuleByName("AllowedBlocks").setValue(new ArrayList<String>());

		for(String s : strings)
		{
			String[] args = s.split(" ");

			if(args[0].equalsIgnoreCase("rules"))
			{
				if(args[1].equalsIgnoreCase("AllowedBlocks"))
				{
					List<BlockDescription> blocks = ((AllowedBlocks) rulesManager.getRuleByName("AllowedBlocks")).getValue();
					blocks.add(new BlockDescription(args[2]));
					rulesManager.getRuleByName("AllowedBlocks").setValue(blocks);
				}

				else if(args[1].equalsIgnoreCase("PlaceBlockInCave"))
				{
					if(args[2].equalsIgnoreCase("value"))
						rulesManager.getRuleByName(args[1]).setValue(Boolean.parseBoolean(args[3]));
					else
						((PlaceBlockInCave) rulesManager.getRuleByName(args[1])).setMinimumBlocks(Integer.parseInt(args[3]));
				}

				else if(isInteger(args[2]))
					rulesManager.getRuleByName(args[1]).setValue(Integer.parseInt(args[2]));

				else if(isBoolean(args[2]))
					rulesManager.getRuleByName(args[1]).setValue(Boolean.parseBoolean(args[2]));

				else
					rulesManager.getRuleByName(args[1]).setValue(args[2]);
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
		if(s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false"))
			return true;
		return false;
	}

	public void reset()
	{
		teamManager = new TeamManager();
		rulesManager = new RulesManager();
		lcManager = new LockedChestsManager();
		crManager = new ChestsRoomsManager();
		rulesManager.loadDefaultConfig();
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
