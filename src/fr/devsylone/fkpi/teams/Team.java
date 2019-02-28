package fr.devsylone.fkpi.teams;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.util.Color;
import fr.devsylone.fkpi.util.CrossversionTeam;
import fr.devsylone.fkpi.util.Saveable;

public class Team implements Saveable
{
	private final static boolean IS_BUKKIT_PLUGIN;
	private String name;

	private Base base;

	private org.bukkit.scoreboard.Team scoreboardTeam;
	private List<String> players;

	static
	{
		IS_BUKKIT_PLUGIN = FkPI.getInstance().isBukkitPlugin();
	}

	public Team(String name)
	{
		this.name = name;

		players = new ArrayList<String>();

		if(IS_BUKKIT_PLUGIN)
		{
			scoreboardTeam = FkPI.getInstance().getTeamManager().getScoreboard().registerNewTeam(name);
			scoreboardTeam.setPrefix(getChatColor() + "");
		}

	}

	public void addPlayer(String p)
	{
		if(IS_BUKKIT_PLUGIN)
			CrossversionTeam.addEntry(p, scoreboardTeam);

		players.add(p);
	}

	public void removePlayer(String p)
	{
		String pl = "";
		for(String s : players)
			if(s.equalsIgnoreCase(p))
			{
				pl = s;
				break;
			}

		players.remove(pl);
		if(IS_BUKKIT_PLUGIN)
			CrossversionTeam.removeEntry(pl, scoreboardTeam);
	}

	public void setBase(Base base)
	{
		this.base = base;
	}

	public List<String> getPlayers()
	{
		return players;
	}

	public String getName()
	{
		return name;
	}

	public Base getBase()
	{
		return base;
	}

	public ChatColor getChatColor()
	{
		return Color.getChatColor(name);
	}

	public org.bukkit.scoreboard.Team getScoreboardTeam()
	{
		return scoreboardTeam;
	}
	
	public void balance(List<Team> teams, int playerPerTeams)
	{
		if(players.size() >= playerPerTeams)// On a assez de joueurs
			return;

		for(Team t : teams) // On cherche dans toutes les teams
		{
			while(t.getPlayers().size() - 1 > playerPerTeams && getPlayers().size() < playerPerTeams) // Tant que l'autre team a trop de joueur et qu'on en a pas assez
			{
				final String p = t.getPlayers().get(t.getPlayers().size() - 1); // Le dernier joueur ajouté
				t.removePlayer(p);
				addPlayer(p);
			}
			if(players.size() >= playerPerTeams)
				break;
		}

		if(players.size() < playerPerTeams) // C'est toujours pas bon ?
			for(Team t : teams) // On recommence cette fois ci
			{
				while(t.getPlayers().size() > playerPerTeams && getPlayers().size() < playerPerTeams) // On ne tolère plus playerPerTeams+1
				{
					final String p = t.getPlayers().get(t.getPlayers().size() - 1); // Le dernier joueur ajouté
					t.removePlayer(p);
					addPlayer(p);
				}
				if(getPlayers().size() - 1 < playerPerTeams)
					break;
			}
	}

	@Override
	public boolean equals(Object other)
	{
		return  other != null  && other instanceof Team? name.equals(((Team) other).getName()) : false;
	}

	@Override
	public void load(ConfigurationSection config)
	{
		for(String entr : config.getStringList("Members"))
			CrossversionTeam.addEntry(entr, scoreboardTeam);

		players = config.getStringList("Members");

		if(!config.isConfigurationSection("Base"))
			return;

		base = new Base(this, null, 0, null, (byte) 0);
		base.load(config.getConfigurationSection("Base"));
		if(base.getCenter().getWorld() == null)
			base = null;
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("Members", players);

		if(base == null || base.getCenter().getWorld() == null)
		{
			config.set("Base", null);
			return;
		}
		base.save(config.createSection("Base"));
	}

	public String toString()
	{
		return getChatColor() + getName();
	}
}