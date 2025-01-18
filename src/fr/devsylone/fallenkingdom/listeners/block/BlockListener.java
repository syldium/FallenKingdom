package fr.devsylone.fallenkingdom.listeners.block;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.XBlock;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.ChestsRoom;
import fr.devsylone.fkpi.teams.Nexus;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener
{

	private final Fk plugin;

	public BlockListener(Fk pl)
	{
		plugin = pl;
	}

	@EventHandler
	public void event(BlockPlaceEvent e)
	{
		if (!Fk.getInstance().getWorldManager().isWorldWithBase(e.getPlayer().getWorld()))
			return;

		Player p = e.getPlayer();
		FkPlayer fkp = Fk.getInstance().getPlayerManager().getPlayer(p);
		Team team = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p);

		if(p.getGameMode() == GameMode.CREATIVE) {
			if (FkPI.getInstance().getChestsRoomsManager().isEnabled()
					&& XBlock.canBePartOfChestRoom(e.getBlock().getType())
					&& FkPI.getInstance().getTeamManager().getBase(p.getLocation()).map(base -> base.getNexus() instanceof ChestsRoom).orElse(false)) {
				ChatUtils.sendMessage(p, Messages.PLAYER_CREATIVE_CHEST);
			}
			return;
		}

		if(team == null || plugin.getGame().isPreStart())
			return;

		if(e.getBlock().getType() == Material.TNT)
		{
			if(!plugin.getGame().isAssaultsEnabled())
			{
				fkp.sendMessage(Messages.PLAYER_TNT_NOT_ACTIVE);
				e.setCancelled(true);
			}
			else if(!FkPI.getInstance().getRulesManager().getRule(Rule.TNT_JUMP))
			{
				Location bLoc = e.getBlock().getLocation();
				if(e.getPlayer().getLocation().getBlockX() == e.getBlock().getLocation().getBlockX() && e.getPlayer().getLocation().getBlockY() == e.getBlock().getLocation().getBlockY() + 1 && e.getPlayer().getLocation().getBlockZ() == e.getBlock().getLocation().getBlockZ() && e.getBlock().getType().equals(Material.TNT))

					for(Team t : Fk.getInstance().getFkPI().getTeamManager().getTeams())
						if(!team.equals(t) && t.getBase() != null)
							if(bLoc.getBlockY() + 3 > t.getBase().getCenter().getBlockY())
							{
								if(t.getBase().contains(bLoc, -3))
								{
									if(Fk.getInstance().getPlayerManager().wasOnTnt(e.getPlayer().getUniqueId()))
										Fk.getInstance().getPlayerManager().removeOnTnt(e.getPlayer().getUniqueId());
								}
								else
								{
									if(Fk.getInstance().getPlayerManager().wasOnTnt(e.getPlayer().getUniqueId()) && t.getBase().contains(bLoc, 3))
									{
										fkp.sendMessage(Messages.PLAYER_TNT_JUMP_DENIED);
										Location tp = e.getBlock().getLocation().clone().add(0.5, 0.1, 0.5);
										tp.setYaw(e.getPlayer().getLocation().getYaw());
										tp.setPitch(e.getPlayer().getLocation().getPitch());
										e.getPlayer().teleport(tp);
										e.setCancelled(true);
									}
									else
									{
										Fk.getInstance().getPlayerManager().putOnTnt(e.getPlayer().getUniqueId(), e.getPlayer().getLocation().getBlock().getLocation());
									}
								}
								break;
							}

			}
			return;
		}

		if(FkPI.getInstance().getRulesManager().getRule(Rule.ALLOWED_BLOCKS).isAllowed(e.getBlock()))
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
				if(!enemyBase && FkPI.getInstance().getRulesManager().getRule(Rule.PLACE_BLOCK_IN_CAVE).isActive())
					for(int y = block.getBlockY() + 1; y < block.getWorld().getMaxHeight(); y++)
						if(XBlock.isBlockInCave(block.getWorld().getBlockAt(block.getBlockX(), y, block.getBlockZ()).getType()))
						{
							if(++stones >= FkPI.getInstance().getRulesManager().getRule(Rule.PLACE_BLOCK_IN_CAVE).getMinimumBlocks())
								return;
						}
						else
							stones = 0;

				fkp.sendMessage(Messages.PLAYER_BLOCK_NOT_ALLOWED);
				e.setCancelled(true);
			}
			else if(XBlock.canBePartOfChestRoom(e.getBlock().getType()))
			{
				int limit = FkPI.getInstance().getRulesManager().getRule(Rule.CHEST_LIMIT);
				int baseY = team.getBase().getCenter().getBlockY();
				if(limit > 0 && Math.abs(baseY - block.getBlockY()) > limit)
				{
					ChatUtils.sendMessage(e.getPlayer(), Messages.PLAYER_CHEST_TOO_FAR);
					e.setCancelled(true);
				}
				else if(team.getBase() != null && !e.isCancelled() && team.getBase().contains(e.getBlock().getLocation()) && Fk.getInstance().getFkPI().getChestsRoomsManager().isEnabled())
				{
					final Nexus nexus = team.getBase().getNexus();
					if (nexus instanceof ChestsRoom)
						((ChestsRoom) nexus).newChest(e.getBlock().getLocation());
				}
			}
		}

	}

	@EventHandler
	public void event(BlockBreakEvent e)
	{
		if (!Fk.getInstance().getWorldManager().isWorldWithBase(e.getPlayer().getWorld()))
			return;

		Player p = e.getPlayer();
		Team team = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p);

		if(team == null || plugin.getGame().isPreStart())
			return;

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
				ChatUtils.sendMessage(p, Messages.PLAYER_BLOCK_BREAK_ENEMY);
				e.setCancelled(true);
				return;
			}
		}

		if(XBlock.canBePartOfChestRoom(e.getBlock().getType()) && team.getBase() != null && !e.isCancelled() && team.getBase().contains(e.getBlock().getLocation()) && Fk.getInstance().getFkPI().getChestsRoomsManager().isEnabled())
		{
			Nexus nexus = team.getBase().getNexus();
			if(nexus instanceof ChestsRoom)
				((ChestsRoom) nexus).removeChest(e.getBlock().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void lockedchests(BlockBreakEvent e)
	{
		if(e.getBlock().getType().equals(Material.CHEST) && Fk.getInstance().getFkPI().getLockedChestsManager().getChestAt(e.getBlock().getLocation()) != null)
		{
			e.setCancelled(true);
			ChatUtils.sendMessage(e.getPlayer(), Messages.PLAYER_BLOCK_BREAK_LOCKED);
		}
	}
}
