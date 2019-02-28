package fr.devsylone.fallenkingdom.utils;

import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.teams.Team;

public class PlaceHolderUtils
{

	public static String getBaseDistance(Player player)
	{
		String value = "";

		Location pLoc = player.getLocation().clone();

		if(player.getWorld().getEnvironment() == Environment.NORMAL)
		{
			if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(player.getName()) == null)
				value = "{noTeam}";

			else if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(player.getName()).getBase() == null)
				value = "{noBase}";

			else if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(player.getName()).getBase().getCenter().getWorld().equals(player.getWorld()))
				value = "0";
			else
			{
				Location base = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(player.getName()).getBase().getCenter().clone();
				base.setY(pLoc.getY());
				value = "" + (int) pLoc.distance(base);
			}
		}

		else if(player.getWorld().getEnvironment() == Environment.NETHER)
		{
			if(Fk.getInstance().getPlayerManager().getPlayer(player).getPortal() == null || !Fk.getInstance().getPlayerManager().getPlayer(player).getPortal().getWorld().equals(player.getWorld()))
				return "?";
			else
			{
				Location portal = Fk.getInstance().getPlayerManager().getPlayer(player).getPortal().clone();
				portal.setY(pLoc.getY());
				value = "" + (int) pLoc.distance(portal);
			}
		}

		else if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(player.getName()) == null)
			value = "{noTeam}";

		else if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(player.getName()).getBase() == null)
			value = "{noBase}";

		return value;
	}

	public static String getBaseDirection(Player player, String arrows)
	{
		Location ploc = player.getLocation().clone();
		if(player.getWorld().getEnvironment() == Environment.NORMAL)
		{
			if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(player.getName()) == null)
				return Fk.getInstance().getScoreboardManager().getNoTeam();

			else if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(player.getName()).getBase() == null)
				return Fk.getInstance().getScoreboardManager().getNoBase();

			else if(!Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(player.getName()).getBase().getCenter().getWorld().equals(player.getWorld()))
				return "?";

			else
			{
				Location base = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(player.getName()).getBase().getCenter().clone();
				String arrow = getDirectionOf(ploc, base, arrows);
				return arrow;
			}
		}
		
		else if(player.getWorld().getEnvironment() == Environment.NETHER)
		{
			if(Fk.getInstance().getPlayerManager().getPlayer(player).getPortal() == null || !Fk.getInstance().getPlayerManager().getPlayer(player).getPortal().getWorld().equals(player.getWorld()))
				return "?";

			Location to = Fk.getInstance().getPlayerManager().getPlayer(player).getPortal().clone();
			to.setY(ploc.getY());

			String arrow = getDirectionOf(ploc, to, arrows);
			return arrow;
		}
		else
			return "";
	}

	public static String getTeamOf(Player p)
	{
		Team t = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p.getName());
		return t == null ? Fk.getInstance().getScoreboardManager().getNoTeam() : t.toString();
	}

	private static String getDirectionOf(Location ploc, Location to, String arrows)
	{
		ploc.setY(0);
		to.setY(0);

		Vector d = ploc.getDirection();

		Vector v = to.subtract(ploc).toVector().normalize();

		double a = Math.toDegrees(Math.atan2(d.getX(), d.getZ()));
		a -= Math.toDegrees(Math.atan2(v.getX(), v.getZ()));

		a = (int) (a + 22.5) % 360;

		if(a < 0)
			a += 360;

		return "" + arrows.charAt((int) a / 45);
	}

	public static int getDeaths(Player p)
	{
		return Fk.getInstance().getPlayerManager().getPlayer(p).getDeaths();
	}

	public static int getKills(Player p)
	{
		return Fk.getInstance().getPlayerManager().getPlayer(p).getKills();
	}
}
