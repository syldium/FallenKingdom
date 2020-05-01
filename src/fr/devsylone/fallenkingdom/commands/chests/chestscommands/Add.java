package fr.devsylone.fallenkingdom.commands.chests.chestscommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
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

public class Add extends FkPlayerCommand
{
	public Add()
	{
		super("add", "<i1:day> <i:time> [name]", Messages.CMD_MAP_CHEST_ADD, CommandPermission.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label)
	{
		int day = ArgumentParser.parsePositiveInt(args.get(0), false, Messages.CMD_ERROR_DAY_FORMAT);
		int time = ArgumentParser.parsePositiveInt(args.get(1), false, Messages.CMD_ERROR_TIME_FORMAT);

		Block target = sender.getTargetBlock(null, 10);

		if(!target.getType().equals(Material.CHEST))
			throw new FkLightException(Messages.CMD_ERROR_NOT_CHEST);

		if(!Fk.getInstance().getWorldManager().isAffected(sender.getWorld()))
			throw new FkLightException(Messages.CMD_ERROR_NOT_AFFECTED_WORLD.getMessage());

		String name = args.size() >= 3 ? args.get(2) : "" + Fk.getInstance().getFkPI().getLockedChestsManager().getChestList().size();

		Fk.getInstance().getFkPI().getLockedChestsManager().addOrEdit(new LockedChest(target.getLocation(), time, day, name));

		broadcast(Messages.CMD_LOCKED_CHEST_CREATED.getMessage()
				.replace("%name%", name)
				.replace("%x%", String.valueOf(target.getLocation().getBlockX()))
				.replace("%y%", String.valueOf(target.getLocation().getBlockY()))
				.replace("%z%", String.valueOf(target.getLocation().getBlockZ()))
				.replace("%time%", String.valueOf(time))
				.replace("%unit%", Messages.Unit.SECONDS.tl(time)),
		3, args);
		return CommandResult.SUCCESS;
	}
}