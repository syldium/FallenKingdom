package fr.devsylone.fallenkingdom.listeners.block;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class BucketListener implements Listener
{
	private final Fk plugin;

	public BucketListener(Fk plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler
	public void event(PlayerBucketEmptyEvent e)
	{
		Player p = e.getPlayer();
		Location bloc = e.getBlockClicked().getRelative(e.getBlockFace()).getLocation();

		if(p.getGameMode() == GameMode.CREATIVE || !plugin.getWorldManager().isWorldWithBase(e.getPlayer().getWorld()))
			return;

		if(plugin.getFkPI().getTeamManager().getPlayerTeam(p) == null || plugin.getGame().getState().equals(GameState.BEFORE_STARTING))
			return;

		if(plugin.getGame().getState().equals(GameState.PAUSE))
		{
			ChatUtils.sendMessage(p, Messages.PLAYER_PAUSE);
			e.setCancelled(true);
			return;
		}

		for(Team team : Fk.getInstance().getFkPI().getTeamManager().getTeams())
			if(!Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p).equals(team))
				if(team.getBase() != null && team.getBase().contains(bloc, 1))
				{
					ChatUtils.sendMessage(p, Messages.PLAYER_PLACE_WATER_NEXT);
					e.setCancelled(true);
					break;
				}
	}
}
