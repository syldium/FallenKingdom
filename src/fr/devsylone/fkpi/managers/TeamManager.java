package fr.devsylone.fkpi.managers;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.Environment;
import fr.devsylone.fkpi.api.ITeam;
import fr.devsylone.fkpi.api.event.PlayerTeamChangeEvent;
import fr.devsylone.fkpi.api.event.TeamUpdateEvent;
import fr.devsylone.fkpi.teams.Base;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fkpi.util.Color;
import fr.devsylone.fkpi.util.Saveable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class TeamManager implements Saveable
{
	private final List<Team> teams = Collections.synchronizedList(new ArrayList<>());
	private final Map<UUID, Team> teamByPlayerUUID = new HashMap<>();
	private final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

	public boolean createTeam(String name)
	{
		if(getTeam(name) != null)
			throw new FkLightException(Messages.CMD_ERROR_TEAM_ALREADY_EXIST);

		if(name.isEmpty() || name.contains(" "))
			throw new FkLightException(Messages.CMD_ERROR_TEAM_INVALID_NAME);

		if(name.length() > 16)
			throw new FkLightException(Messages.CMD_ERROR_TEAM_NAME_TOO_LONG);

		Team team = new Team(name);
		Bukkit.getPluginManager().callEvent(new TeamUpdateEvent(team, TeamUpdateEvent.TeamUpdate.CREATION)); // EVENT
		teams.add(team);

		return !team.getColor().equals(Color.NO_COLOR);
	}

	public Scoreboard getScoreboard()
	{
		return board;
	}

	public void removeTeam(String name)
	{
		Team team = getTeam(name);
		if(team == null)
			throw new FkLightException(Messages.CMD_ERROR_UNKNOWN_TEAM.getMessage().replace("%team%", name));

		Bukkit.getPluginManager().callEvent(new TeamUpdateEvent(team, TeamUpdateEvent.TeamUpdate.DELETION)); // EVENT

		for (String player : team.getPlayers()) {
			teamByPlayerUUID.remove(Environment.getPlayerUniqueId(player));
		}
		team.getPlayers().clear();

		team.getScoreboardTeam().unregister();
		teams.remove(team);
		Fk.getInstance().getWorldManager().invalidateBaseWorldsCache(this);
	}

	public Team getTeam(String name)
	{
		for(Team t : teams)
			if(t.getName().equals(name))
				return t;

		return null;
	}

	public @NotNull Optional<@NotNull Base> getBase(@NotNull Location location, int lag) {
		requireNonNull(location, "location");

		// N'arrondir qu'une seule fois
		World world = location.getWorld();
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();

		for (Team team : teams) {
			Base base = team.getBase();
			if (base != null && base.contains(world, x, y, z, lag)) {
				return Optional.of(base);
			}
		}
		return Optional.empty();
	}

	public @NotNull Optional<@NotNull Base> getBase(@NotNull Location location) {
		return getBase(location, 0);
	}

	public @NotNull Optional<@NotNull Base> getBase(@NotNull Block block, int lag) {
		requireNonNull(block, "block");
		for (Team team : teams) {
			Base base = team.getBase();
			if (base != null && base.contains(block, lag)) {
				return Optional.of(base);
			}
		}
		return Optional.empty();
	}

	public @NotNull Optional<@NotNull Base> getBase(@NotNull Block block) {
		return getBase(block, 0);
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
			if (t.getPlayers().contains(player))
				return t;

		return null;
	}

	public Team getPlayerTeam(Player player)
	{
		if (player == null) {
			return null;
		}
		return teamByPlayerUUID.computeIfAbsent(player.getUniqueId(), s -> getPlayerTeam(player.getName()));
	}

	public ITeam addPlayer(String player, String teamName)
	{
		if(getPlayerTeam(player) != null)
			throw new FkLightException(Messages.CMD_ERROR_PLAYER_ALREADY_HAS_TEAM);

		if(getTeam(teamName) == null)
			throw new FkLightException(Messages.CMD_ERROR_UNKNOWN_TEAM.getMessage().replace("%team%", teamName));

		if(player.isEmpty() || player.contains(" "))
			throw new FkLightException(Messages.CMD_ERROR_INVALID_PLAYER.getMessage());

		PlayerTeamChangeEvent event = new PlayerTeamChangeEvent(player, getPlayerTeam(player), getTeam(teamName)); // EVENT
		Bukkit.getPluginManager().callEvent(event);
		event.getTeam().addPlayer(player);
		return event.getTeam();
	}

	public void removePlayerOfHisTeam(String player)
	{
		Team team = getPlayerTeam(player);
		if(team == null)
			throw new FkLightException(Messages.CMD_ERROR_PLAYER_NOT_IN_TEAM);

		Bukkit.getPluginManager().callEvent(new PlayerTeamChangeEvent(player, team, null)); // EVENT
		team.removePlayer(player);
		teamByPlayerUUID.remove(Environment.getPlayerUniqueId(player));
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
		for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
			if (!createTeam(entry.getKey())) {
				continue;
			}
			if (entry.getValue() instanceof ConfigurationSection) {
				getTeam(entry.getKey()).load((ConfigurationSection) entry.getValue());
			}
		}

		Fk.getInstance().getWorldManager().invalidateBaseWorldsCache(this);
	}

	@Override
	public void save(ConfigurationSection config)
	{
		for(Team t : teams)
			t.save(config.createSection(t.getName()));
	}
}
