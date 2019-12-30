package fr.devsylone.fallenkingdom.listeners.block;

import fr.devsylone.fallenkingdom.utils.XBlock;
import fr.devsylone.fkpi.util.BlockDescription;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fkpi.rules.AllowedBlocks;
import fr.devsylone.fkpi.rules.PlaceBlockInCave;
import fr.devsylone.fkpi.teams.Team;

public class BlockListener implements Listener
{

	Fk plugin;

	public BlockListener(Fk pl)
	{
		plugin = pl;
	}

	@EventHandler
	public void event(BlockPlaceEvent e)
	{
		Player p = e.getPlayer();
		FkPlayer fkp = Fk.getInstance().getPlayerManager().getPlayer(p);
		Team team = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p.getName());

		if(p.getGameMode() == GameMode.CREATIVE)
			return;

		if(p.getWorld().getEnvironment() != Environment.NORMAL || team == null || plugin.getGame().getState().equals(GameState.BEFORE_STARTING))
			return;

		if(plugin.getGame().getState().equals(GameState.PAUSE))
		{
			fkp.sendMessage(ChatUtils.PREFIX + ChatColor.RED + "La partie est en pause.");
			e.setCancelled(true);
			return;
		}

		if(e.getBlock().getType() == Material.TNT)
		{
			if(!plugin.getGame().isAssaultsEnabled())
			{
				fkp.sendMessage(ChatUtils.PREFIX + ChatColor.RED + "Les assauts ne sont pas actifs !");
				e.setCancelled(true);
			}
			else if(!(boolean) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("tntjump").getValue())
			{
				Location bLoc = e.getBlock().getLocation();
				if(e.getPlayer().getLocation().getBlockX() == e.getBlock().getLocation().getBlockX() && e.getPlayer().getLocation().getBlockY() == e.getBlock().getLocation().getBlockY() + 1 && e.getPlayer().getLocation().getBlockZ() == e.getBlock().getLocation().getBlockZ() && e.getBlock().getType().equals(Material.TNT))

					for(Team t : Fk.getInstance().getFkPI().getTeamManager().getTeams())
						if(team != null && !team.equals(t) && t.getBase() != null)
							if(bLoc.getBlockY() + 3 > t.getBase().getCenter().getBlockY())
							{
								if(t.getBase().contains(bLoc, -3))
								{
									if(Fk.getInstance().getPlayerManager().wasOnTnt(e.getPlayer().getName()))
										Fk.getInstance().getPlayerManager().removeOnTnt(e.getPlayer().getName());
									break;
								}
								else
								{
									if(Fk.getInstance().getPlayerManager().wasOnTnt(e.getPlayer().getName()) && t.getBase().contains(bLoc, 3))
									{
										e.getPlayer().sendMessage(ChatUtils.PREFIX + "§cVous n'avez pas le droit d'entrer dans une base en sautant sur de la TNT");
										Location tp = e.getBlock().getLocation().clone().add(0.5, 0.1, 0.5);
										tp.setYaw(e.getPlayer().getLocation().getYaw());
										tp.setPitch(e.getPlayer().getLocation().getPitch());
										e.getPlayer().teleport(tp);
										e.setCancelled(true);
										break;
									}
									else
									{
										Fk.getInstance().getPlayerManager().putOnTnt(e.getPlayer().getName(), e.getPlayer().getLocation().getBlock().getLocation());
										break;
									}
								}
							}

			}
			return;
		}

		if(((AllowedBlocks) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("AllowedBlocks")).isAllowed(new BlockDescription(e.getBlock())))
			return;

		Location block = e.getBlock().getLocation();

		if(team.getBase() != null)
		{
			if(!team.getBase().contains(block))
			{
				boolean enemyBase = false;
				for(Team t : Fk.getInstance().getFkPI().getTeamManager().getTeams())
					if(!t.equals(team) && t.getBase() != null && t.getBase().contains(block))
						enemyBase = true;

				int stones = 0;
				if(!enemyBase && (boolean) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("PlaceBlockInCave").getValue())
					for(int y = block.getBlockY() + 1; y < 256; y++)
						if(XBlock.isBlockInCave(block.getWorld().getBlockAt(block.getBlockX(), y, block.getBlockZ()).getType()))
						{
							if(++stones >= ((PlaceBlockInCave) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("PlaceBlockInCave")).getMinimumBlocks())
								return;
						}
						else
							stones = 0;

				p.sendMessage(ChatUtils.PREFIX + ChatColor.RED + "Vous ne pouvez pas poser ce bloc !");
				e.setCancelled(true);
			}
			else if(XBlock.canBePartOfChestRoom(e.getBlock().getType()))
			{
				int limit = (Integer) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("ChestLimit").getValue();
				int baseY = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getPlayer().getName()).getBase().getCenter().getBlockY();
				if(limit > 0 && Math.abs(baseY - block.getBlockY()) > limit)
				{
					p.sendMessage(ChatUtils.PREFIX + ChatColor.RED + "Ce coffre est en dessous/au dessus de la limite de profondeur/hauteur de la salle des coffres !");
					e.setCancelled(true);
				}
				else if(team != null && team.getBase() != null && !e.isCancelled() && team.getBase().contains(e.getBlock().getLocation()) && Fk.getInstance().getFkPI().getChestsRoomsManager().isEnabled())
				{
					team.getBase().getChestsRoom().newChest(e.getBlock().getLocation());
				}
			}
		}

	}

	@EventHandler
	public void event(BlockBreakEvent e)
	{
		Player p = e.getPlayer();
		Team team = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p.getName());

		if(p.getWorld().getEnvironment() != Environment.NORMAL || team == null || plugin.getGame().getState().equals(GameState.BEFORE_STARTING))
			return;

		if(plugin.getGame().getState().equals(GameState.PAUSE) && p.getGameMode() != GameMode.CREATIVE)
		{
			p.sendMessage(ChatUtils.PREFIX + ChatColor.RED + "La partie est en pause.");
			e.setCancelled(true);
			return;
		}

		//AVANT check creative

		if(p.getGameMode() == GameMode.CREATIVE)
			return;

		if(e.getBlock().getType() == Material.TNT)
			return;

		Block b = e.getBlock();
		Location bloc = b.getLocation();

		for(Team t : Fk.getInstance().getFkPI().getTeamManager().getTeams())
		{
			if(!t.equals(team) && t.getBase() != null && t.getBase().contains(bloc))
			{
				p.sendMessage(ChatUtils.PREFIX + ChatColor.RED + "Vous ne pouvez pas casser de blocs en territoire ennemi !");
				e.setCancelled(true);
				return;
			}
		}

		if(XBlock.canBePartOfChestRoom(e.getBlock().getType()) && team.getBase() != null && !e.isCancelled() && team.getBase().contains(e.getBlock().getLocation()) && Fk.getInstance().getFkPI().getChestsRoomsManager().isEnabled())
		{
			team.getBase().getChestsRoom().removeChest(e.getBlock().getLocation());
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void lockedchests(BlockBreakEvent e)
	{
		if(e.getBlock().getType().equals(Material.CHEST) && Fk.getInstance().getFkPI().getLockedChestsManager().getChestAt(e.getBlock().getLocation()) != null)
		{
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cCe coffre est un coffre à crocheter, vous ne pouvez pas le casser. Pour le casser, utilisez la commande §e/fk chests remove");
		}
	}
}
