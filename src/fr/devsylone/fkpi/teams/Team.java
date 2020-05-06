package fr.devsylone.fkpi.teams;

import java.util.ArrayList;
import java.util.List;

import fr.devsylone.fallenkingdom.utils.Version;
import fr.devsylone.fkpi.api.ITeam;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.util.Color;
import fr.devsylone.fkpi.util.Saveable;

public class Team implements ITeam, Saveable
{
	private final String name;

	private Base base;

	private final org.bukkit.scoreboard.Team scoreboardTeam;
	private List<String> players;

	private Color color;

	public Team(String name)
	{
		this.name = name;
		players = new ArrayList<>();

		scoreboardTeam = FkPI.getInstance().getTeamManager().getScoreboard().registerNewTeam(name);
		setColor(Color.forName(name));
	}

	public void addPlayer(String p)
	{
		scoreboardTeam.addEntry(p);
		players.add(p);
	}

	public void removePlayer(String p)
	{
		players.remove(p);
		scoreboardTeam.removeEntry(p);
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

	public Color getColor()
	{
		return color;
	}

	public ChatColor getChatColor()
	{
		return color.getChatColor();
	}

	public void setColor(Color color)
	{
		this.color = color == null ? Color.NO_COLOR : color;
		if(Version.VersionType.V1_13.isHigherOrEqual())
			scoreboardTeam.setColor(this.color.getChatColor());
		else
			scoreboardTeam.setPrefix(String.valueOf(this.color.getChatColor()));
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
		return other instanceof Team && name.equals(((Team) other).getName());
	}

	@Override
	public void load(ConfigurationSection config)
	{
		for(String entr : config.getStringList("Members"))
			scoreboardTeam.addEntry(entr);

		players = config.getStringList("Members");
		setColor(Color.valueOf(config.getString("Color")));
		scoreboardTeam.setPrefix(this.color.getChatColor() + "");

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
		config.set("Color", color.name());

		if(base == null || base.getCenter().getWorld() == null)
		{
			config.set("Base", null);
			return;
		}

		base.save(config.createSection("Base"));
	}

	public String toString()
	{
		return color.getChatColor() + getName();
	}
}