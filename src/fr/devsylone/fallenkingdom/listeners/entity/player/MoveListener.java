package fr.devsylone.fallenkingdom.listeners.entity.player;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fkpi.teams.Team;

public class MoveListener implements Listener
{
	@EventHandler
	public void move(PlayerMoveEvent e)
	{
		/*
		 * IF changé de block X-Y-Z
		 */

		Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer().getName()).getScoreboard().refresh(PlaceHolder.BASE_DIRECTION, PlaceHolder.BASE_DISTANCE, PlaceHolder.NEAREST_TEAM_BASE, PlaceHolder.NEAREST_BASE_DIRECTION);

		if(e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockZ() == e.getTo().getBlockZ() && e.getFrom().getBlockY() == e.getTo().getBlockY())
			return;

		FkPlayer fkp = Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer());

		/*
		 * A partir de maintenant, juste si le fromXYZ != toXYZ
		 */

		checkTnt(e);

		if(Fk.getInstance().getGame().getState().equals(GameState.PAUSE) && (Boolean) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("DeepPause").getValue() && e.getFrom().getBlockY() == e.getTo().getBlockY())
		{
			if(e.getPlayer().getGameMode() == GameMode.CREATIVE)
				return;
			
			fkp.sendMessage(ChatColor.RED + "La partie est en pause.");
			Location tp = e.getFrom().getBlock().getLocation().clone().add(0.5, 0.5, 0.5);
			tp.setPitch(e.getFrom().getPitch());
			tp.setYaw(e.getFrom().getYaw());

			e.getPlayer().teleport(tp);

			return;
		}

		Team pTeam = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getPlayer().getName());

		for(Team team : Fk.getInstance().getFkPI().getTeamManager().getTeams())
		{
			if(team.getBase() != null)
			{
				if(team.getBase().contains(e.getTo()) && !team.getBase().contains(e.getFrom()))
					if(pTeam != null && team.equals(pTeam))
						fkp.sendMessage(ChatColor.GREEN + "Vous entrez dans votre base");
					else
						fkp.sendMessage("Vous entrez dans la base de l'equipe " + team.toString());

				else if(team.getBase().contains(e.getFrom()) && !team.getBase().contains(e.getTo()))
					if(pTeam != null && team.equals(pTeam))
						fkp.sendMessage(ChatColor.RED + "Vous sortez de votre base");
					else
						fkp.sendMessage("Vous sortez de la base de l'equipe " + team.toString());

				if(team.getBase().getChestsRoom() != null && Fk.getInstance().getFkPI().getChestsRoomsManager().isEnabled() && !e.getPlayer().getGameMode().equals(GameMode.SPECTATOR))
				{
					if(team.getBase().getChestsRoom().contains(e.getTo()) && !team.getBase().getChestsRoom().contains(e.getFrom()))
						if(team.equals(pTeam))
							fkp.sendMessage(ChatColor.DARK_GREEN + "Vous entrez dans votre salle des coffres (§e/fk team ChestsRoom Show§2)");
						else
						{
							fkp.sendMessage("§rVous entrez dans la salle des coffres de l'équipe " + team.toString());
							team.getBase().getChestsRoom().addEnemyInside(e.getPlayer().getName());
						}

					else if(team.getBase().getChestsRoom().contains(e.getFrom()) && !team.getBase().getChestsRoom().contains(e.getTo()))
						if(team.equals(pTeam))
							fkp.sendMessage(ChatColor.DARK_RED + "Vous sortez de votre salle des coffres");
						else
						{
							team.getBase().getChestsRoom().removeEnemyInside(e.getPlayer().getName());
							fkp.sendMessage("§rVous sortez de la salle des coffres de l'équipe " + team.toString());
						}
				}
			}
		}
	}

	// SURTOUT PAS DE EVENTHANDLER
	private void checkTnt(PlayerMoveEvent e)
	{
		if(!(boolean) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("tntjump").getValue())
		{
			if(!e.getTo().clone().add(0, -1, 0).getBlock().getType().equals(Material.AIR) && !e.getTo().clone().add(0, -1, 0).getBlock().getType().equals(Material.TNT) && Fk.getInstance().getPlayerManager().wasOnTnt(e.getPlayer().getName()))
				Fk.getInstance().getPlayerManager().removeOnTnt(e.getPlayer().getName());

			else if(Fk.getInstance().getPlayerManager().wasOnTnt(e.getPlayer().getName()) && e.getTo().getBlock().getLocation().equals(Fk.getInstance().getPlayerManager().getTntLoc(e.getPlayer().getName())))
				;

			else if(e.getTo().clone().add(0, -1, 0).getBlock().getType().equals(Material.TNT))
				for(Team t : Fk.getInstance().getFkPI().getTeamManager().getTeams())
					if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getPlayer().getName()) != null && !Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getPlayer().getName()).equals(t) && t.getBase() != null)
						if(e.getTo().getBlockY() + 3 > t.getBase().getCenter().getBlockY())
						{
							if(t.getBase().contains(e.getTo(), -3))
							{
								if(Fk.getInstance().getPlayerManager().wasOnTnt(e.getPlayer().getName()))
									Fk.getInstance().getPlayerManager().removeOnTnt(e.getPlayer().getName());
								break;
							}

							else
							{
								if(Fk.getInstance().getPlayerManager().wasOnTnt(e.getPlayer().getName()) && t.getBase().contains(e.getTo(), 3))
								{
									Location tp = Fk.getInstance().getPlayerManager().getTntLoc(e.getPlayer().getName()).clone().add(0.5, 0.1, 0.5);
									tp.setPitch(e.getFrom().getPitch());
									tp.setYaw(e.getFrom().getYaw());
									e.getPlayer().teleport(tp);
									e.getPlayer().sendMessage(ChatUtils.PREFIX + "§cVous n'avez pas le droit d'entrer dans une base en sautant/marchant sur de la TNT");
									break;
								}
								else
								{
									Fk.getInstance().getPlayerManager().putOnTnt(e.getPlayer().getName(), e.getTo().getBlock().getLocation());
									break;
								}
							}
						}
		}
	}
}
