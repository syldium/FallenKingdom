package fr.devsylone.fallenkingdom.commands.chests.chestscommands;

import com.google.common.collect.Sets;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class Remove extends FkPlayerCommand
{
	public Remove()
	{
		super("remove", Messages.CMD_MAP_CHEST_REMOVE, CommandRole.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label)
	{
		Block target = sender.getTargetBlock(Sets.newHashSet(Material.AIR), 10);
		LockedChest chest = Fk.getInstance().getFkPI().getLockedChestsManager().getChestAt(target.getLocation());

		if (!Fk.getInstance().getFkPI().getLockedChestsManager().remove(target.getLocation()))
			throw new FkLightException(Messages.CMD_ERROR_NOT_LOCKED_CHEST);
		broadcast(Messages.CMD_LOCKED_CHEST_REMOVED.getMessage().replace("%name%", chest.getName()), 0, args);
		return CommandResult.SUCCESS;
	}
}