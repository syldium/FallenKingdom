package fr.devsylone.fallenkingdom.commands.chests.chestscommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.chests.FkChestsCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.lockedchests.LockedChest;

public class Remove extends FkChestsCommand
{
	public Remove()
	{
		super("remove", Messages.CMD_MAP_CHEST_REMOVE.getMessage());
		permission = ADMIN_PERMISSION;
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Block target = sender.getTargetBlock(Sets.newHashSet(Material.AIR), 10);
		LockedChest chest = Fk.getInstance().getFkPI().getLockedChestsManager().getChestAt(target.getLocation());
		
		if(!Fk.getInstance().getFkPI().getLockedChestsManager().remove(target.getLocation()))
			throw new FkLightException(Messages.CMD_ERROR_NOT_LOCKED_CHEST);
		broadcast(Messages.CMD_LOCKED_CHEST_REMOVED.getMessage().replace("%name%", chest.getName()));

	}
}