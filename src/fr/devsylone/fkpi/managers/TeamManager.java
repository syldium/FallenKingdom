package fr.devsylone.fkpi.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scoreboard.Scoreboard;

import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fkpi.util.Color;
import fr.devsylone.fkpi.util.Saveable;

public class TeamManager implements Saveable
{
	private final List<Team> teams = new ArrayList<>();
	private final Scoreboard board = FkPI.getInstance().getPlugin().getServer().getScoreboardManager().getNewScoreboard();

	public boolean createTeam(String name)
	{
		if(getTeam(name) != null)
			throw new FkLightException(Messages.CMD_ERROR_TEAM_ALREADY_EXIST);

		if(name.isEmpty() || name.contains(" "))
			throw new FkLightException(Messages.CMD_ERROR_TEAM_INVALID_NAME);

		if(name.length() > 25)
			throw new FkLightException(Messages.CMD_ERROR_TEAM_NAME_TOO_LONG);

		Team team = new Team(name);
		team.setColor(Color.forName(name));
		teams.add(team);

		return Color.forName(name) != null;
	}

	public Scoreboard getScoreboard()
	{
		return board;
	}

	public void removeTeam(String name)
	{
		if(getTeam(name) == null)
			throw new FkLightException(Messages.CMD_ERROR_UNKNOWN_TEAM.getMessage().replace("%team%", name));

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
		return teams.stream().map(Team::getName).collect(Collectors.toList());
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
			throw new FkLightException(Messages.CMD_ERROR_PLAYER_ALREADY_HAS_TEAM);

		if(getTeam(teamName) == null)
			throw new FkLightException(Messages.CMD_ERROR_UNKNOWN_TEAM.getMessage().replace("%team%", teamName));

		if(player.isEmpty() || player.contains(" "))
			throw new FkLightException(Messages.CMD_ERROR_INVALID_PLAYER.getMessage());

		getTeam(teamName).addPlayer(player);
	}

	public void removePlayerOfHisTeam(String player)
	{
		if(getPlayerTeam(player) == null)
			throw new FkLightException(Messages.CMD_ERROR_PLAYER_NOT_IN_TEAM);

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
		if(getTeams().isEmpty())
			throw new FkLightException(Messages.CMD_ERROR_NO_TEAM);

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
