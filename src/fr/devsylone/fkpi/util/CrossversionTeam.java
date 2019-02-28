package fr.devsylone.fkpi.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Team;

public class CrossversionTeam
{
	private static boolean bizard = false;
	private static Method get;
	private static Method add;
	private static Method remove;

	static
	{
		try
		{
			get = Team.class.getMethod("getEntries");
			add = Team.class.getMethod("addEntry", String.class);
			remove = Team.class.getMethod("removeEntry", String.class);
			bizard = false;
		}catch(NoSuchMethodException e)
		{
			bizard = true;
			try
			{
				get = Team.class.getMethod("getPlayers");
				add = Team.class.getMethod("addPlayer", OfflinePlayer.class);
				remove = Team.class.getMethod("removePlayer", OfflinePlayer.class);
			}catch(NoSuchMethodException | SecurityException e1)
			{
				e1.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Set<String> getEntries(Team team)
	{
		Set<String> entries = null;
		try
		{
			if(bizard)
			{
				Set<OfflinePlayer> players = (Set<OfflinePlayer>) get.invoke(team);
				entries = new HashSet<String>();
				for(OfflinePlayer p : players)
					entries.add(p.getName());
			}
			else
				entries = (Set<String>) get.invoke(team);

		}catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return entries;
	}

	@SuppressWarnings("deprecation")
	public static void addEntry(String player, Team team)
	{
		try
		{
			if(bizard)
			{
				add.invoke(team, Bukkit.getOfflinePlayer(player));
			}
			else
				add.invoke(team, player);

		}catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static void removeEntry(String player, Team team)
	{
		try
		{
			if(bizard)
			{
				remove.invoke(team, Bukkit.getOfflinePlayer(player));
			}
			else
				remove.invoke(team, player);

		}catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

}
