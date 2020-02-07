package fr.devsylone.fallenkingdom.listeners.entity.player;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.particles.FastParticle;
import fr.devsylone.fallenkingdom.particles.ParticleType;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.players.FkPlayer.PlayerState;
import fr.devsylone.fkpi.teams.Team;

public class JoinListener implements Listener
{
    public int getRandomElement(List<Integer> list) { 
        Random rand = new Random(); 
        return list.get(rand.nextInt(list.size())); 
    } 
    
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
		Player player_bukkit = e.getPlayer();
		
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
		Fk.broadcast(e.getPlayer().getDisplayName() + ChatColor.GRAY + " a rejoint la partie !");
		Fk.getInstance().getScoreboardManager().refreshAllScoreboards();

		if(player.getState() == PlayerState.EDITING_SCOREBOARD)
			player.getSbDisplayer().display();

		if(Fk.getInstance().getGame().getState() == GameState.BEFORE_STARTING || Fk.getInstance().getGame().getState() == GameState.PAUSE) {
			/*
			List<Integer> EFFECTS = new ArrayList<Integer>();
			EFFECTS.add(4);
			EFFECTS.add(6);
			EFFECTS.add(9);
			EFFECTS.add(11);
			EFFECTS.add(17);
			EFFECTS.add(26);
			EFFECTS.add(28);
			EFFECTS.add(32);
			
			//https://github.com/kvq/Trails/wiki/API
			 int RANDOM_EFFECT = getRandomElement(EFFECTS);
			 //p.sendMessage(Integer.toString(RANDOM_EFFECT));
			 SuperTrailsAPI.setTrail(RANDOM_EFFECT, player_bukkit);
			 */
			 FastParticle.spawnParticle(player_bukkit, ParticleType.CLOUD, player_bukkit.getLocation(), 1000);
			}
		
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
