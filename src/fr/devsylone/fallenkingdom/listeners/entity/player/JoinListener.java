package fr.devsylone.fallenkingdom.listeners.entity.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.players.FkPlayer.PlayerState;
import fr.devsylone.fkpi.teams.Team;

public class JoinListener implements Listener
{
	@EventHandler
	public void prelogin(final AsyncPlayerPreLoginEvent e)
	{
		if(!Fk.getInstance().getError().isEmpty())
		{
			e.setLoginResult(Result.KICK_OTHER);
			e.setKickMessage("§d§m----------§5 Fallenkingdom §d§m----------\n"
					+ "\n"
					+ "§6Le plugin a rencontré une erreur\n\n"
					+ "§7Erreur : §c" + Fk.getInstance().getError());
		}
	}

	@EventHandler
	public void join(final PlayerJoinEvent e)
	{
		FkPlayer player = Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer());
		
		if(e.getPlayer().isOp())
			for(String s : Fk.getInstance().getOnConnectWarnings())
				e.getPlayer().sendMessage(s);

		player.recreateScoreboard();

		final Team pTeam = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getPlayer().getName());
		if(pTeam != null) //REFRESH LES TEAMS SCOREBOARD (MC=CACA)
		{
			Bukkit.dispatchCommand(e.getPlayer(), "fk team removePlayer " + e.getPlayer().getName() + " nobroadcast");
			Bukkit.dispatchCommand(e.getPlayer(), "fk team addPlayer " + e.getPlayer().getName() + " " + pTeam.getName() + " nobroadcast");
		}

		e.setJoinMessage(null);
		Fk.broadcast(e.getPlayer().getDisplayName() + ChatColor.GRAY + " a rejoint la partie !");
		Fk.getInstance().getScoreboardManager().refreshAllScoreboards();

		if(player.getState() == PlayerState.EDITING_SCOREBOARD)
			player.getSbDisplayer().display();

	}

	@EventHandler
	public void quit(PlayerQuitEvent e)
	{
		if(Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer().getName()).getState() == PlayerState.EDITING_SCOREBOARD)
			Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer().getName()).getSbDisplayer().exit();

		e.setQuitMessage(null);
		Fk.broadcast(e.getPlayer().getDisplayName() + ChatColor.GRAY + " a quitté la partie !");
	}
}
