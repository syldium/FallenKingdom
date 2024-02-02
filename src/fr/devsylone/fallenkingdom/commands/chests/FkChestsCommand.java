package fr.devsylone.fallenkingdom.commands.chests;

import com.google.common.collect.ImmutableList;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.FkParentCommand;
import fr.devsylone.fallenkingdom.commands.chests.chestscommands.Add;
import fr.devsylone.fallenkingdom.commands.chests.chestscommands.AddLoadout;
import fr.devsylone.fallenkingdom.commands.chests.chestscommands.ChestLock;
import fr.devsylone.fallenkingdom.commands.chests.chestscommands.ChestUnlock;
import fr.devsylone.fallenkingdom.commands.chests.chestscommands.ChestsList;
import fr.devsylone.fallenkingdom.commands.chests.chestscommands.Remove;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

public class FkChestsCommand extends FkParentCommand
{
	public FkChestsCommand()
	{
		super("chests", ImmutableList.<FkCommand>builder()
				.add(new Add())
                .add(new AddLoadout())
				.add(new ChestsList())
				.add(new ChestLock())
				.add(new ChestUnlock())
				.add(new Remove())
				.build()
		, Messages.CMD_MAP_CHEST);
	}

    public static Chest getCommandChest(Player sender) throws FkLightException {
        Block target = sender.getTargetBlock(null, 10);
        if (!target.getType().equals(Material.CHEST)) {
            throw new FkLightException(Messages.CMD_ERROR_NOT_CHEST);
        }
        if (!Fk.getInstance().getWorldManager().isAffected(sender.getWorld())) {
            throw new FkLightException(Messages.CMD_ERROR_NOT_AFFECTED_WORLD.getMessage());
        }

        return ((Chest) target.getState());
    }

	@Override
	protected void broadcast(String message)
	{
		Fk.broadcast(ChatColor.GOLD + message, ChatUtils.CHESTS);
	}
}
