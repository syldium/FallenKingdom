package fr.devsylone.fkpi;

import fr.devsylone.fallenkingdom.Fk;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import fr.devsylone.fkpi.managers.ChestsRoomsManager;
import fr.devsylone.fkpi.managers.LockedChestsManager;
import fr.devsylone.fkpi.managers.RulesManager;
import fr.devsylone.fkpi.managers.TeamManager;
import fr.devsylone.fkpi.util.Saveable;

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
