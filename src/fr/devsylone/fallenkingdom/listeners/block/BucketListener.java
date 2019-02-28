package fr.devsylone.fallenkingdom.listeners.block;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fkpi.teams.Team;

public class BucketListener implements Listener
{
	private Fk plugin;

	public BucketListener(Fk mc)
	{
		this.plugin = mc;
	}

	@EventHandler
	public void event(PlayerBucketEmptyEvent e)
	{
		Player p = e.getPlayer();
		Location bloc = e.getBlockClicked().getRelative(e.getBlockFace()).getLocation();

		if(p.getGameMode() == GameMode.CREATIVE)
			return;

		if(p.getWorld().getEnvironment() != Environment.NORMAL || Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p.getName()) == null || plugin.getGame().getState().equals(GameState.BEFORE_STARTING))
			return;

		if(plugin.getGame().getState().equals(GameState.PAUSE))
		{
			p.sendMessage(ChatUtils.PREFIX + ChatColor.RED + "La partie est en pause.");
			e.setCancelled(true);
			return;
		}

		for(Team team : Fk.getInstance().getFkPI().getTeamManager().getTeams())
			if(!Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p.getName()).equals(team))
				if(team.getBase() != null && team.getBase().contains(bloc, 1))
				{
					p.sendMessage(ChatUtils.PREFIX + ChatColor.RED + "Vous ne pouvez pas vider ce seau !");
					e.setCancelled(true);
					break;
				}
	}
}
