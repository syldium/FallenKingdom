package fr.devsylone.fkpi.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scoreboard.Scoreboard;

import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fkpi.util.Color;
import fr.devsylone.fkpi.util.Saveable;

public class TeamManager implements Saveable
{
	private List<Team> teams;

	private Scoreboard board;

	public TeamManager()
	{
		if(FkPI.getInstance().isBukkitPlugin())
			board = FkPI.getInstance().getPlugin().getServer().getScoreboardManager().getNewScoreboard();

		teams = new ArrayList<Team>();
	}

	public boolean createTeam(String name)
	{
		if(getTeam(name) != null)
			throw new FkLightException("Cette équipe existe déjà !");

		if(name.isEmpty())
			throw new FkLightException("Nom de l'équipe invalide !");

		if(name.contains(" "))
			throw new FkLightException("L'équipe ne peut pas contenir d'espace dans son nom !");

		if(name.length() > 25)
			throw new FkLightException("Le nom de l'équipe ne peut exceder 25 caractères !");

		Team team = new Team(name);
		team.setColor(Color.forName(name));
		teams.add(team);

		if(FkPI.getInstance().isBukkitPlugin())
			return Color.forName(name) != null;

		else
			return true;
	}

	public Scoreboard getScoreboard()
	{
		return board;
	}

	public void removeTeam(String name)
	{
		if(getTeam(name) == null)
			throw new FkLightException("Cette équipe n'existe pas !");

		if(FkPI.getInstance().isBukkitPlugin())
			getTeam(name).getScoreboardTeam().unregister();

		teams.remove(getTeam(name));
	}

	public Team getTeam(String name)
	{
		for(Team t : teams)
			if(t.getName().equalsIgnoreCase(name))
				return t;

		return null;
	}

	public List<Team> getTeams()
	{
		return teams;
	}

	public List<String> getTeamNames()
	{
		List<String> names = new ArrayList<String>();
		for(Team t : teams)
			names.add(t.getName());

		return names;
	}

	public Team getPlayerTeam(String player)
	{
		if(player == null)
			return null;

		for(Team t : teams)
			for(String s : t.getPlayers())
				if(s.equalsIgnoreCase(player))
					return t;

		return null;
	}

	public void addPlayer(String player, String teamName)
	{
		if(getPlayerTeam(player) != null)
			throw new FkLightException("Le joueur fait déjà partie d'une équipe !");

		if(getTeam(teamName) == null)
			throw new FkLightException("Cette équipe n'existe pas !");

		if(player.isEmpty() || player.contains(" "))
			throw new FkLightException("Pseudo invalide !");

		getTeam(teamName).addPlayer(player);
	}

	public void removePlayerOfHisTeam(String player)
	{
		if(getPlayerTeam(player) == null)
			throw new FkLightException("Le joueur ne fait pas partie d'une équipe !");

		for(Team t : teams)
			for(String s : t.getPlayers())
				if(s.equalsIgnoreCase(player))
				{
					t.removePlayer(player);
					break;
				}
	}

	public void random(List<String> players)
	{
		//		if(!FkPI.getInstance().isBukkitPlugin())
		//			return;
		//
		if(getTeams().isEmpty())
			throw new FkLightException("Il n'y a pas d'équipes !");

		Random rdm = new Random();
		final int originalSize = players.size();
		for(int i = 0; i < originalSize; i++)
		{
			int rdmi = rdm.nextInt(players.size());
			String p = players.get(rdmi);
			if(getPlayerTeam(p) != null)
				removePlayerOfHisTeam(p);

			teams.get(0).addPlayer(p);
			players.remove(rdmi);
		}

		int playerPerTeams = getTotalPlayers() / teams.size();

		for(Team t : teams)
			t.balance(teams, playerPerTeams);
	}

	public int getTotalPlayers()
	{
		int ret = 0;
		for(Team t : teams)
			ret += t.getPlayers().size();
		return ret;
	}

	@Override
	public void load(ConfigurationSection config)
	{
		for(String teamS : config.getKeys(false))
			createTeam(teamS);

		for(Team t : teams)
			t.load(config.getConfigurationSection(t.getName()));

	}

	@Override
	public void save(ConfigurationSection config)
	{
		for(Team t : teams)
			t.save(config.createSection(t.getName()));
	}
}
