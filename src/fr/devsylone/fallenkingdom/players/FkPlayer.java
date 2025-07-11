package fr.devsylone.fallenkingdom.players;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.display.GlobalDisplayService;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.DistanceTree;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.FkSound;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.teams.Base;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fallenkingdom.display.notification.RegionChange;
import fr.devsylone.fkpi.util.Saveable;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.scoreboard.FkScoreboard;
import fr.devsylone.fallenkingdom.scoreboard.ScoreboardDisplayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

public class FkPlayer implements Saveable
{
	private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\\n(?=(§.)*?[^(§.)\\n])");

	private final GlobalDisplayService displayService;
	private final String name;

	private boolean knowsSbEdit = false;
	private PlayerState state = PlayerState.INGAME;
	private RegionChange lastChange;
	private FkScoreboard board;
	private ScoreboardDisplayer sbDisplayer;
	private Location portal;
	private boolean formatted = true;

    public enum PlayerState
	{
		INGAME,
		EDITING_SCOREBOARD
	}

	private int kills = 0;
	private int deaths = 0;

	public FkPlayer(@NotNull String name, @NotNull GlobalDisplayService displayService) {
		this.name = name;
		this.displayService = displayService;
	}

	public String getName()
	{
		return name;
	}

	public int getKills()
	{
		return kills;
	}

	public int getDeaths()
	{
		return deaths;
	}

	public void addKill()
	{
		kills += 1;
	}

	public void addDeath()
	{
		deaths += 1;
	}

	public void clearDeaths()
	{
		deaths = 0;
	}

	public void clearKills()
	{
		kills = 0;
	}

	public PlayerState getState()
	{
		return state;
	}

	public void setState(PlayerState state)
	{
		this.state = state;
	}

	public @Nullable RegionChange getLastRegionChange() {
		return lastChange;
	}

	public void setLastChange(@Nullable RegionChange lastChange) {
		this.lastChange = lastChange;
	}

	public void sendMessage(String message)
	{
		sendMessage(message, "", null);
	}

	public void sendMessage(Messages message)
	{
		if (message.getMessage().isEmpty())
			return;
		sendMessage(message.getMessage(), "", null);
	}

	public void sendMessage(String message, String prefix)
	{
		sendMessage(message, prefix, null);
	}

	public void sendMessage(String message, String prefix, FkSound sound)
	{
		Player p = Bukkit.getPlayer(name);
		if(p != null)
		{
			if(sound != null)
				p.playSound(p.getLocation(), sound.key(), 1.0F, 1.0F);

			String full = message.length() < 4 ? "" : Messages.PREFIX_FK.getMessage() + prefix;

			message = NEW_LINE_PATTERN.matcher("\n" + message).replaceAll("\n" + full);
			message = message.substring(1);

			if (Fk.PAPI_ENABLED) {
				message = PlaceholderAPI.setPlaceholders(p, message);
			}

			p.sendMessage(message);
		}
	}

	public void updateDisplay(@NotNull Player player, @NotNull PlaceHolder... placeHolders)
	{
		this.displayService.update(player, this, placeHolders);
	}

	public void exitSbDisplayer()
	{
		if(sbDisplayer == null)
			throw new FkLightException(Messages.CMD_ERROR_SCOREBOARD_NOT_EDITING);

		setState(PlayerState.INGAME);
		sbDisplayer.exit();
		sbDisplayer = null;
	}

	public ScoreboardDisplayer getSbDisplayer()
	{
		if(sbDisplayer == null)
			throw new FkLightException(Messages.CMD_ERROR_SCOREBOARD_NOT_EDITING);
		return sbDisplayer;
	}

	public void newSbDisplayer()
	{
		if(sbDisplayer != null)
			throw new FkLightException(Messages.CMD_ERROR_SCOREBOARD_ALREADY_EDITING);

		setState(PlayerState.EDITING_SCOREBOARD);
		sbDisplayer = new ScoreboardDisplayer(this);

		if(Bukkit.getPlayer(name) != null)
			sbDisplayer.display();
	}

	public FkScoreboard getScoreboard()
	{
		if(board == null)
			board = new FkScoreboard(this, Bukkit.getPlayerExact(name));
		return board;
	}

	public @Nullable FkScoreboard getScoreboardIfExists()
	{
		return board;
	}

	/**
	 * @deprecated {@link #refreshScoreboard()}
	 */
	@Deprecated
	public void recreateScoreboard()
	{
		refreshScoreboard();
	}

	public void refreshScoreboard()
	{
		if (board == null)
			board = new FkScoreboard(this, Bukkit.getPlayerExact(name));
		else
			board.refreshAll();
	}

	public void removeScoreboard()
	{
		if(board != null)
			board.remove();
		board = null;
	}

	public @NotNull GlobalDisplayService getDisplayService()
	{
		return this.displayService;
	}

	public Location getPortal()
	{
		return portal;
	}

	public void setPortal(Location newLoc)
	{
		portal = newLoc;
	}

	public boolean hasAlreadyLearntHowToEditTheBeautifulScoreboard()
	{
		return knowsSbEdit;
	}

	public void knowNowSbEdit()
	{
		knowsSbEdit = true;
	}

	public @NotNull DistanceTree<Base> getNearBases(@NotNull Player player)
	{
		final DistanceTree<Base> nearBases = new DistanceTree<>(requireNonNull(player, "player is offline").getLocation());
		final Team playerTeam = FkPI.getInstance().getTeamManager().getPlayerTeam(player);
		for (Team team : FkPI.getInstance().getTeamManager().getTeams()) {
			final Base base = team.getBase();
			if (base == null || !player.getWorld().equals(base.getCenter().getWorld()) || team == playerTeam) {
				continue;
			}
			nearBases.add(base.getCenter(), base);
		}
		return nearBases;
	}

	public @NotNull DistanceTree<Player> getNearAllies(@NotNull Player player)
	{
		final DistanceTree<Player> nearAllies = new DistanceTree<>(requireNonNull(player, "player is offline").getLocation());
		final Team playerTeam = FkPI.getInstance().getTeamManager().getPlayerTeam(player);
		for (Player worldPlayer : player.getWorld().getPlayers()) {
			if (player == worldPlayer || playerTeam != null && FkPI.getInstance().getTeamManager().getPlayerTeam(worldPlayer) != playerTeam) {
				continue;
			}
			nearAllies.add(worldPlayer.getLocation(), worldPlayer);
		}
		return nearAllies;
	}

	public boolean useFormattedText()
	{
		return formatted;
	}

	public void setUseFormattedText(boolean formatted)
	{
		this.formatted = formatted;
		final Player player = Bukkit.getPlayerExact(this.name);
		if (player != null) {
			this.displayService.update(player, this);
		}
	}

	@Override
	public void load(ConfigurationSection config)
	{
		kills = config.getInt("Kills");
		deaths = config.getInt("Deaths");
		knowsSbEdit = config.getBoolean("KnowsSbEdit");

		if(config.isConfigurationSection("Portal"))
			portal = new Location(Bukkit.getWorld(config.getString("Portal.World")), config.getInt("Portal.X"), config.getInt("Portal.Y"), config.getInt("Portal.Z"));
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("Kills", kills);
		config.set("Deaths", deaths);
		config.set("KnowsSbEdit", knowsSbEdit);

		if(portal != null && portal.getWorld() != null)
		{
			config.set("Portal.World", portal.getWorld().getName());
			config.set("Portal.X", portal.getBlockX());
			config.set("Portal.Y", portal.getBlockY());
			config.set("Portal.Z", portal.getBlockZ());
		}
	}
}
