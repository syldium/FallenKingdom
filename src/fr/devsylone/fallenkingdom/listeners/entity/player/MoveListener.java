package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.Base;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Random;

import static fr.devsylone.fallenkingdom.version.Environment.getMinHeight;

public class MoveListener implements Listener
{
	@EventHandler(ignoreCancelled = true)
	public void move(PlayerMoveEvent e)
	{
		if(e.getTo() == null || !Fk.getInstance().getWorldManager().isAffected(e.getTo().getWorld()))
			return;

		/*
		 * IF changé de block X-Y-Z
		 */

		FkPlayer fkp = Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer());
		fkp.getScoreboard().refresh(PlaceHolder.BASE_DIRECTION, PlaceHolder.BASE_DISTANCE, PlaceHolder.NEAREST_TEAM_BASE, PlaceHolder.NEAREST_BASE_DIRECTION);

		if(e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockZ() == e.getTo().getBlockZ() && e.getFrom().getBlockY() == e.getTo().getBlockY())
			return;

		/*
		 * A partir de maintenant, juste si le fromXYZ != toXYZ
		 */

		checkTnt(e);

		if(Fk.getInstance().getGame().getState().equals(GameState.PAUSE) && FkPI.getInstance().getRulesManager().getRule(Rule.DEEP_PAUSE) && e.getFrom().getBlockY() == e.getTo().getBlockY())
		{
			if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE) || e.getPlayer().getGameMode().equals(GameMode.SPECTATOR))
				return;
			
			fkp.sendMessage(Messages.PLAYER_PAUSE);
			Location tp = e.getFrom().getBlock().getLocation().clone().add(0.5, 0.5, 0.5);
			tp.setPitch(e.getFrom().getPitch());
			tp.setYaw(e.getFrom().getYaw());

			e.getPlayer().teleport(tp);

			return;
		}

		Team pTeam = FkPI.getInstance().getTeamManager().getPlayerTeam(e.getPlayer());

		for(Team team : FkPI.getInstance().getTeamManager().getTeams())
		{
			if(team.getBase() != null)
			{
				if(team.getBase().contains(e.getTo()) && !team.getBase().contains(e.getFrom()))
					if(team.equals(pTeam))
						fkp.sendMessage(Messages.PLAYER_SELF_BASE_ENTER);
					else
						fkp.sendMessage(Messages.PLAYER_BASE_ENTER.getMessage().replace("%team%", team.toString()));

				else if(team.getBase().contains(e.getFrom()) && !team.getBase().contains(e.getTo()))
					if(team.equals(pTeam))
						fkp.sendMessage(Messages.PLAYER_SELF_BASE_EXIT);
					else
						fkp.sendMessage(Messages.PLAYER_BASE_EXIT.getMessage().replace("%team%", team.toString()));

				if(team.getBase().getChestsRoom() != null && FkPI.getInstance().getChestsRoomsManager().isEnabled() && !e.getPlayer().getGameMode().equals(GameMode.SPECTATOR))
				{
					if(team.getBase().getChestsRoom().contains(e.getTo()) && !team.getBase().getChestsRoom().contains(e.getFrom()))
						if(team.equals(pTeam))
							fkp.sendMessage(Messages.PLAYER_SELF_CHEST_ROOM_ENTER);
						else
						{
							fkp.sendMessage(Messages.PLAYER_CHEST_ROOM_ENTER.getMessage().replace("%team%", team.toString()));
							team.getBase().getChestsRoom().addEnemyInside(e.getPlayer());
						}

					else if(team.getBase().getChestsRoom().contains(e.getFrom()) && !team.getBase().getChestsRoom().contains(e.getTo()))
						if(team.equals(pTeam))
							fkp.sendMessage(Messages.PLAYER_SELF_CHEST_ROOM_EXIT);
						else
						{
							team.getBase().getChestsRoom().removeEnemyInside(e.getPlayer());
							fkp.sendMessage(Messages.PLAYER_CHEST_ROOM_EXIT.getMessage().replace("%team%", team.toString()));
						}
				}
			}
		}
	}

	// SURTOUT PAS DE EVENTHANDLER
	private void checkTnt(PlayerMoveEvent e)
	{
		if(!FkPI.getInstance().getRulesManager().getRule(Rule.TNT_JUMP))
		{
			if(!e.getTo().clone().add(0, -1, 0).getBlock().getType().equals(Material.AIR) && !e.getTo().clone().add(0, -1, 0).getBlock().getType().equals(Material.TNT) && Fk.getInstance().getPlayerManager().wasOnTnt(e.getPlayer().getUniqueId()))
				Fk.getInstance().getPlayerManager().removeOnTnt(e.getPlayer().getUniqueId());

			else if(e.getTo().clone().add(0, -1, 0).getBlock().getType().equals(Material.TNT))
				for(Team t : FkPI.getInstance().getTeamManager().getTeams())
					if(FkPI.getInstance().getTeamManager().getPlayerTeam(e.getPlayer()) != null && !FkPI.getInstance().getTeamManager().getPlayerTeam(e.getPlayer()).equals(t) && t.getBase() != null)
						if(e.getTo().getBlockY() + 3 > t.getBase().getCenter().getBlockY())
						{
							if(t.getBase().contains(e.getTo(), -3))
							{
								if(Fk.getInstance().getPlayerManager().wasOnTnt(e.getPlayer().getUniqueId()))
									Fk.getInstance().getPlayerManager().removeOnTnt(e.getPlayer().getUniqueId());
								break;
							}

							else
							{
								if(Fk.getInstance().getPlayerManager().wasOnTnt(e.getPlayer().getUniqueId()) && t.getBase().contains(e.getTo(), 3))
								{
									Location tp = Fk.getInstance().getPlayerManager().getTntLoc(e.getPlayer().getUniqueId()).clone().add(0.5, 0.1, 0.5);
									tp.setPitch(e.getFrom().getPitch());
									tp.setYaw(e.getFrom().getYaw());
									e.getPlayer().teleport(tp);
									ChatUtils.sendMessage(e.getPlayer(), Messages.PLAYER_TNT_JUMP_DENIED);
									break;
								}
								else
								{
									Fk.getInstance().getPlayerManager().putOnTnt(e.getPlayer().getUniqueId(), e.getTo().getBlock().getLocation());
									break;
								}
							}
						}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent e) {
		PlayerTeleportEvent.TeleportCause chorus = Version.VersionType.V1_9_V1_12.isHigherOrEqual() ? PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT : null;
		if (!e.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL) && !e.getCause().equals(chorus))
			return;

		if (e.getTo() == null || e.getPlayer().getGameMode().equals(GameMode.CREATIVE) || FkPI.getInstance().getRulesManager().getRule(Rule.ENDERPEARL_ASSAULT))
			return;

		Team pTeam = FkPI.getInstance().getTeamManager().getPlayerTeam(e.getPlayer());
		for (Team team : FkPI.getInstance().getTeamManager().getTeams()) {
			if (team.getBase() == null)
				continue;

			if (!team.getBase().contains(e.getFrom(), -1) && team.getBase().contains(e.getTo()) && !team.equals(pTeam)) {
				ChatUtils.sendMessage(e.getPlayer(), Messages.PLAYER_TP_IN_BASE);
				e.setCancelled(true);

				if (e.getCause().equals(chorus))
					tpAsChorusFruit(e.getPlayer(), team.getBase()); // Pourquoi pas ?
			}
		}
	}

	private void tpAsChorusFruit(Player player, Base base)
	{
		Random random = new Random();
		for (int i = 0; i < 15; i++) {
			double x = player.getLocation().getX() + (random.nextDouble() - 0.5) * 16.0;
			double y = player.getLocation().getY() + (double) (random.nextInt(16) - 8);
			double z = player.getLocation().getZ() + (random.nextDouble() - 0.5) * 16.0;
			Location loc = new Location(player.getWorld(), x, y, z);
			if (!base.contains(loc, 1) && safeTp(player, loc)) {
				player.getWorld().playSound(player.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
				break;
			}
		}
	}

	private boolean safeTp(Player player, Location loc)
	{
		World world = loc.getWorld();
		if (world == null || !loc.getChunk().isLoaded())
			return false;

		if (!loc.getBlock().getType().isSolid() && !loc.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
			while (!loc.clone().add(0, -1, 0).getBlock().getType().isSolid() && loc.getY() > getMinHeight(world))
				loc.add(0, -1, 0);
			player.teleport(loc, PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT);
			return true;
		}
		return false;
	}
}
