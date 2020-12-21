package fr.devsylone.fallenkingdom.listeners.block;

import java.util.Iterator;
import java.util.List;

import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.teams.Team;

public class BlockExplodeListener implements Listener
{
	@EventHandler
	public void explode(EntityExplodeEvent e)
	{
		protectLockedChests(e.blockList());
		removeExplodedChestsRoomChests(e.blockList());
	}

	@EventHandler
	public void explode(BlockExplodeEvent e)
	{
		protectLockedChests(e.blockList());
		removeExplodedChestsRoomChests(e.blockList());
	}

	public void protectLockedChests(List<Block> blockList)
	{
		for(Iterator<Block> iterator = blockList.iterator(); iterator.hasNext();)
		{
			Block block = iterator.next();
			if(block.getType().equals(Material.CHEST) && Fk.getInstance().getFkPI().getLockedChestsManager().getChestAt(block.getLocation()) != null)
			{
				final ArmorStand as = (ArmorStand) block.getWorld().spawnEntity(block.getLocation().add(0.5, -1, 0.5), EntityType.ARMOR_STAND);
				as.setVisible(false);
				if (Version.VersionType.V1_9_V1_12.isHigherOrEqual()) {
					as.setCustomName(ChatUtils.colorMessage(Messages.EASTER_EGG_CHEST_EXPLODE));
					as.setCustomNameVisible(true);
					as.setGravity(false);
				}
				as.setNoDamageTicks(10000);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), as::remove, 40L);
				iterator.remove();
			}
		}
	}

	public void removeExplodedChestsRoomChests(List<Block> blockList)
	{
		for(Team team : Fk.getInstance().getFkPI().getTeamManager().getTeams())
			if(team.getBase() != null && team.getBase().getChestsRoom() != null)
				for(Block b : blockList)
					team.getBase().getChestsRoom().removeChest(b.getLocation());
	}
}
