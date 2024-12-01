package fr.devsylone.fallenkingdom.utils;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.teams.Base;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fallenkingdom.display.notification.RegionChange;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static java.util.Objects.requireNonNull;

public class PlaceHolderUtils
{
    public static final Supplier<Game> GAME_SUPPLIER = () -> Fk.getInstance().getGame();
	private static final double ANGLE_OFFSET = ((double) 360 / 16) * 13;

	private static @Nullable Location getPointingLocation(Player player)
	{
		// Vers la base
		Team pTeam = FkPI.getInstance().getTeamManager().getPlayerTeam(player);
		if (pTeam != null && requireNonNull(pTeam.getBase(), "player base").getCenter() != null && player.getWorld().equals(pTeam.getBase().getCenter().getWorld()))
			return pTeam.getBase().getCenter().clone();

		// Vers le portail
		Location portal = Fk.getInstance().getPlayerManager().getPlayer(player).getPortal();
		if (portal != null && player.getWorld().equals(portal.getWorld()))
			return portal.clone();

		return null;
	}

	private static Optional<Base> getNearestBase(Player player, int iteration)
	{
		return Fk.getInstance().getPlayerManager().getPlayer(player).getNearBases(player).find(iteration);
	}

	private static Optional<Player> getNearestAlly(Player player, int iteration)
	{
		return Fk.getInstance().getPlayerManager().getPlayer(player).getNearAllies(player).find(iteration);
	}

	public static String getBaseDistance(Player player)
	{
		Location pLoc = player.getLocation();
		Team pTeam = FkPI.getInstance().getTeamManager().getPlayerTeam(player);
		if (pTeam == null)
			return noTeam();
		if (pTeam.getBase() == null)
			return noBase();

		Location point = getPointingLocation(player);
		if (point != null)
			return String.valueOf((int) pLoc.distance(point));

		return Fk.getInstance().getDisplayService().text().noInfo(); // ?
	}

	public static String getBaseDirection(Player player)
	{
		Team pTeam = FkPI.getInstance().getTeamManager().getPlayerTeam(player);
		if (pTeam == null)
			return noTeam();
		if (pTeam.getBase() == null)
			return noBase();

		Location point = getPointingLocation(player);
		if (point != null)
			return getDirectionOf(player.getLocation(), point);

		return Fk.getInstance().getDisplayService().text().noInfo(); // ?
	}

	public static String getNearestTeamBase(Player player, int iteration)
	{
		if(FkPI.getInstance().getTeamManager().getTeams().size() < 1)
			return noTeam();

		Optional<Base> nearestBase = getNearestBase(player, iteration);
		return nearestBase
				.map(base -> base.getTeam().getChatColor() + base.getTeam().getName())
				.orElse(Fk.getInstance().getDisplayService().text().noInfo());
	}

	public static String getNearestBaseDirection(Player player, int iteration)
	{
		if(FkPI.getInstance().getTeamManager().getTeams().size() < 1)
			return noTeam();

		Optional<Base> nearestBase = getNearestBase(player, iteration);
		return nearestBase
				.map(base -> getDirectionOf(player.getLocation(), base.getCenter()))
				.orElse(Fk.getInstance().getDisplayService().text().noInfo());
	}

	public static String getNearestAllyName(Player player, int iteration)
	{
		Optional<Player> nearestPlayer = getNearestAlly(player, iteration);
		if (nearestPlayer.isPresent()) {
			return nearestPlayer.get().getDisplayName();
		}
		return noInfo();
	}

	public static String getNearestAllyDir(Player player, int iteration)
	{
		Optional<Player> nearestPlayer = getNearestAlly(player, iteration);
		if (nearestPlayer.isPresent()) {
			return getDirectionOf(player.getLocation(), nearestPlayer.get().getLocation());
		}
		return noInfo();
	}

	public static String getNearestAllyDist(Player player, int iteration)
	{
		Optional<Player> nearestPlayer = getNearestAlly(player, iteration);
		if (nearestPlayer.isPresent()) {
			return String.valueOf((int) player.getLocation().distance(nearestPlayer.get().getLocation()));
		}
		return noInfo();
	}

	public static String getTeamOf(Player p)
	{
		Team t = FkPI.getInstance().getTeamManager().getPlayerTeam(p);
		return t == null ? noTeam() : t.toString();
	}

	private static String getDirectionOf(Location location, Location target)
	{
		double yaw = location.getYaw();
		double z = target.getZ() - location.getZ();
		double x = target.getX() - location.getX();

		double theta = Math.toDegrees(Math.atan2(z, x));
		int angle = Math.floorMod((int) (ANGLE_OFFSET + theta - yaw), 360);
		return String.valueOf(Fk.getInstance().getDisplayService().text().arrowAt(angle));
	}
	
	public static String getBaseOrPortal(Player player)
	{
		Team pTeam = FkPI.getInstance().getTeamManager().getPlayerTeam(player);
		if (pTeam != null && pTeam.getBase() != null && player.getWorld().equals(pTeam.getBase().getCenter().getWorld()))
			return Messages.SCOREBOARD_BASE.getMessage();
		Location portal = Fk.getInstance().getPlayerManager().getPlayer(player).getPortal();
		if (portal != null && player.getWorld().equals(portal.getWorld()))
			return Messages.SCOREBOARD_PORTAL.getMessage();
		return Messages.SCOREBOARD_BASE.getMessage(); // Même si on pointe vers rien
	}

	public static String getRegion(Player player)
	{
		Team pTeam = FkPI.getInstance().getTeamManager().getPlayerTeam(player);
		Location loc = player.getLocation();
		Base nearest = null;
		double distance = Double.MAX_VALUE;
		for (Team team : FkPI.getInstance().getTeamManager().getTeams()) {
			Base base = team.getBase();
			if (base == null || base.getCenter().getWorld() != loc.getWorld()) {
				continue;
			}
			if (base.contains(loc)) {
				boolean inChestsRoom = FkPI.getInstance().getChestsRoomsManager().isEnabled() && base.getNexus().contains(loc);
				Messages message;
				if (team.equals(pTeam)) {
					message = inChestsRoom ? Messages.PLAYER_SELF_CHEST_ROOM_INSIDE : Messages.PLAYER_SELF_BASE_INSIDE;
				} else {
					message = inChestsRoom ? Messages.PLAYER_CHEST_ROOM_INSIDE : Messages.PLAYER_BASE_INSIDE;
				}
				return message.getMessage().replace("%team%", team.toString());
			}
			double d = base.getCenter().distanceSquared(loc);
			if (d < distance) {
				distance = d;
				nearest = base;
			}
		}

		if (nearest == null) {
			return noBase();
		}
		Messages message;
		if (nearest.getTeam().equals(pTeam)) {
			message = Messages.PLAYER_SELF_BASE_NEAR;
		} else {
			message = Messages.PLAYER_BASE_NEAR;
		}
		return message.getMessage().replace("%team%", nearest.getTeam().toString());
	}

	public static String getRegionChange(Player player)
	{
		RegionChange change = Fk.getInstance().getPlayerManager().getPlayer(player).getLastRegionChange();
		if (change == null || change.timestamp() < System.currentTimeMillis() - 2000L) {
			return "";
		}
		return change.message(player);
	}

	public static int getDeaths(Player p)
	{
		return Fk.getInstance().getPlayerManager().getPlayer(p).getDeaths();
	}

	public static int getKills(Player p)
	{
		return Fk.getInstance().getPlayerManager().getPlayer(p).getKills();
	}

	private static String getTeamStats(Player p, ToIntFunction<FkPlayer> stats)
	{
		final Team team = FkPI.getInstance().getTeamManager().getPlayerTeam(p);
		if (team == null) {
			return noTeam();
		}
		int count = 0;
		for (String playerName : team.getPlayers()) {
			count += stats.applyAsInt(Fk.getInstance().getPlayerManager().getPlayer(playerName));
		}
		return String.valueOf(count);
	}

	public static String getTeamDeaths(Player p)
	{
		return getTeamStats(p, FkPlayer::getDeaths);
	}

	public static String getTeamKills(Player p)
	{
		return getTeamStats(p, FkPlayer::getKills);
	}

	private static String noTeam()
	{
		return Messages.CMD_SCOREBOARD_NO_TEAM.getMessage();
	}

	private static String noBase()
	{
		return Messages.CMD_SCOREBOARD_NO_BASE.getMessage();
	}

	private static String noInfo()
	{
		return Fk.getInstance().getDisplayService().text().noInfo();
	}
}
