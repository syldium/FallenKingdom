package fr.devsylone.fallenkingdom.listeners.entity.player;

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
		final Team team = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getPlayer().getName());

		if(!e.isBedSpawn() && (boolean) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("RespawnAtHome").getValue() && team != null && team.getBase() != null)
		{

			for(int x = -2; x < 2; x++)
				for(int z = -2; z < 2; z++)
					for(int y = -1; y < 2; y++)
					{
						if(e.getRespawnLocation().getWorld().getBlockAt(team.getBase().getCenter().add(x, y, z)).getType() == Material.AIR && e.getRespawnLocation().getWorld().getBlockAt(team.getBase().getCenter().add(x, y + 1, z)).getType() == Material.AIR)
						{
							final Location loc = team.getBase().getCenter().add((double) x + 0.5d, (double) y + 0.2d, (double) z + 0.5d).clone();
							e.getPlayer().setGameMode(GameMode.SPECTATOR);
							Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
							{
								@Override
								public void run()
								{
									e.getPlayer().teleport(loc);
									e.getPlayer().setGameMode(GameMode.SURVIVAL);
								}
							}, 60l);
							return;
						}
					}
			Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer()).sendMessage("§cLa zone de spawn de votre base est obstruée ! Pensez à dégager le centre de la base.");
				}
			}, 20l);
		}
	}
	
}
