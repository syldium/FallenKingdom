package fr.devsylone.fallenkingdom.listeners.block;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
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
		Block block = e.getBlockClicked();

		if(p.getGameMode() == GameMode.CREATIVE || !plugin.getWorldManager().isWorldWithBase(e.getPlayer().getWorld()))
			return;

		Team playerTeam = plugin.getFkPI().getTeamManager().getPlayerTeam(p);
		if(playerTeam == null || plugin.getGame().getState() == GameState.BEFORE_STARTING)
			return;

		if (plugin.getFkPI().getRulesManager().getRule(Rule.BUCKET_ASSAULT))
			return;

		if(plugin.getGame().getState().equals(GameState.PAUSE))
		{
			ChatUtils.sendMessage(p, Messages.PLAYER_PAUSE);
			e.setCancelled(true);
			return;
		}

		for(Team team : Fk.getInstance().getFkPI().getTeamManager().getTeams())
			if(!playerTeam.equals(team))
				if(team.getBase() != null && team.getBase().contains(block, 2))
				{
					ChatUtils.sendMessage(p, Messages.PLAYER_PLACE_WATER_NEXT);
					e.setCancelled(true);
					break;
				}
	}
}
