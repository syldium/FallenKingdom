package fr.devsylone.fallenkingdom.commands.chests.chestscommands;

import java.util.Set;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.chests.FkChestsCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.lockedchests.LockedChest;

public class Add extends FkChestsCommand
{
	public Add()
	{
		super("add", "<day> <time> (en " + Messages.UNIT_SECONDS + ") [name] ", 2, Messages.CMD_MAP_CHEST_ADD.getMessage());
		permission = ADMIN_PERMISSION;
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		int day = assertPositiveNumber(args[0], false, Messages.CMD_ERROR_DAY_FORMAT);
		int time = assertPositiveNumber(args[0], false, Messages.CMD_ERROR_TIME_FORMAT);
		Block target = sender.getTargetBlock((Set<Material>) null, 10);
		
		if(!target.getType().equals(Material.CHEST))
			throw new FkLightException(Messages.CMD_ERROR_NOT_CHEST);

		String name = args.length >= 3 ? args[2] : String.valueOf(Fk.getInstance().getFkPI().getLockedChestsManager().getChestList().size());

		Fk.getInstance().getFkPI().getLockedChestsManager().addOrEdit(new LockedChest(target.getLocation(), time, day, name));

		broadcast(Messages.CMD_LOCKED_CHEST_CREATED.getMessage()
				.replace("%name%", name)
				.replace("%x%", String.valueOf(target.getLocation().getBlockX()))
				.replace("%y%", String.valueOf(target.getLocation().getBlockY()))
				.replace("%z%", String.valueOf(target.getLocation().getBlockZ()))
				.replace("%time%", String.valueOf(time))
				.replace("%unit%", Messages.Unit.SECONDS.tl(time))
		);

	}
}