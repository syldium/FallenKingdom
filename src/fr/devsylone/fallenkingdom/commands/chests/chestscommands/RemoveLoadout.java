package fr.devsylone.fallenkingdom.commands.chests.chestscommands;


import java.util.List;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.commands.chests.FkChestsCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.lockedchests.LockedChest;

public class RemoveLoadout extends FkPlayerCommand {
    private static String USAGE = "<i:day>";

    public RemoveLoadout() {
        super("removeLoadout", USAGE, Messages.CMD_MAP_CHEST_REMOVE_LOADOUT, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args,
            String label) {
        Chest target = FkChestsCommand.getCommandChest(sender);
        LockedChest chest =
                plugin.getFkPI().getLockedChestsManager().getChestAt(target.getLocation());
        if (chest == null) {
            throw new FkLightException(Messages.CMD_ERROR_NOT_LOCKED_CHEST);
        }
        boolean success = chest.removeLoadout(
                ArgumentParser.parsePositiveInt(args.get(0), false, Messages.CMD_ERROR_DAY_FORMAT));

        if (!success) {
            throw new FkLightException(Messages.CMD_ERROR_NOT_LOCKED_CHEST_LOADOUT);
        }
        broadcast(Messages.CMD_LOCKED_CHEST_LOADOUT_REMOVED.getMessage()
            .replace("%day%", args.get(0))
            .replace("%name%", chest.getName()));

        return CommandResult.SUCCESS;
    }
}
