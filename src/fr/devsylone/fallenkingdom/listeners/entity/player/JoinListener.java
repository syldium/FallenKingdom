package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.utils.Messages;
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
			e.setKickMessage(kickMessage());
		}
	}

	@EventHandler
	public void join(final PlayerJoinEvent e)
	{
		if(!Fk.getInstance().getError().isEmpty()) // Bukkit n'a pas l'air d'invoquer l'AsyncPlayerPreLoginEvent
			e.getPlayer().kickPlayer(kickMessage());

		FkPlayer player = Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer());
		
		if(e.getPlayer().isOp())
			for(String s : Fk.getInstance().getOnConnectWarnings())
				e.getPlayer().sendMessage(s);

		player.recreateScoreboard();

		final Team pTeam = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getPlayer().getName());
		if(pTeam != null) //REFRESH LES TEAMS SCOREBOARD (MC=CACA)
		{
			e.getPlayer().setDisplayName(pTeam.getChatColor() + e.getPlayer().getName());
			Fk.getInstance().getScoreboardManager().refreshNicks();
		}

		e.setJoinMessage(null);
		Fk.broadcast(Messages.CHAT_JOIN.getMessage().replace("%player%", e.getPlayer().getDisplayName()));
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
		Fk.broadcast(Messages.CHAT_QUIT.getMessage().replace("%player%", e.getPlayer().getDisplayName()));
	}

	private String kickMessage()
	{
		return "§d§m----------§5 Fallenkingdom §d§m----------\n"
				+ "\n"
				+ "§6Le plugin a rencontré une erreur\n\n"
				+ "§7Erreur : §c" + Fk.getInstance().getError();
	}
}
