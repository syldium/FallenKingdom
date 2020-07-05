package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.Version;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.teams.Team;

public class RespawnListener implements Listener
{
	@EventHandler
	public void respawn(final PlayerRespawnEvent e)
	{
		if(!Fk.getInstance().getWorldManager().isAffected(e.getPlayer().getWorld()))
			return;
		final Team team = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getPlayer().getName());

		if(FkPI.getInstance().getRulesManager().getRule(Rule.DEATH_LIMIT) > 0)
			if(Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer().getName()).getDeaths() >= FkPI.getInstance().getRulesManager().getRule(Rule.DEATH_LIMIT))
				return;

		if(!e.isBedSpawn() && FkPI.getInstance().getRulesManager().getRule(Rule.RESPAWN_AT_HOME) && team != null && team.getBase() != null)
		{
			for(int x = -2; x < 2; x++)
				for(int z = -2; z < 2; z++)
					for(int y = -1; y < 2; y++)
					{
						if(e.getRespawnLocation().getWorld().getBlockAt(team.getBase().getCenter().add(x, y, z)).getType() == Material.AIR && e.getRespawnLocation().getWorld().getBlockAt(team.getBase().getCenter().add(x, y + 1, z)).getType() == Material.AIR)
						{
							final Location loc = team.getBase().getCenter().add((double) x + 0.5d, (double) y + 0.2d, (double) z + 0.5d).clone();
							e.getPlayer().setGameMode(GameMode.SPECTATOR);
							Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> {
								Version.teleportAsync(e.getPlayer(), loc);
								e.getPlayer().setGameMode(GameMode.SURVIVAL);
							}, 60L);
							return;
						}
					}
			Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> ChatUtils.sendMessage(e.getPlayer(), Messages.PLAYER_BASE_OBSTRUCTED), 20L);
		}
	}
	
}
