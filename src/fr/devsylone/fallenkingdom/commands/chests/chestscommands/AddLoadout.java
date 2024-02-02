package fr.devsylone.fallenkingdom.commands.chests.chestscommands;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

public class AddLoadout extends FkPlayerCommand {
    private static String USAGE = "<i:day> <i:time> [i:expiry] [advancement]";

    public AddLoadout() {
        super("addLoadout", USAGE, Messages.CMD_MAP_CHEST_ADD_LOADOUT, CommandRole.ADMIN);

    }

    @Override
    public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args,
            String label) {
        Chest target = FkChestsCommand.getCommandChest(sender);
        LockedChest chest = Fk.getInstance().getFkPI().getLockedChestsManager().getChestAt(target.getLocation());
        if (chest == null) {
            throw new FkLightException(Messages.CMD_ERROR_NOT_LOCKED_CHEST);
        }

        // Parse all arguments
        int day =
                ArgumentParser.parsePositiveInt(args.get(0), false, Messages.CMD_ERROR_DAY_FORMAT);
        int time =
                ArgumentParser.parsePositiveInt(args.get(1), false, Messages.CMD_ERROR_TIME_FORMAT);
        int expiry = -1;
        if (args.size() >= 3) {
            expiry = ArgumentParser.parsePositiveInt(args.get(2), false,
                    Messages.CMD_ERROR_TIME_FORMAT);
        }
        String advancement = "";
        if (args.size() == 4) {
            advancement = args.get(3);
        }
        chest.addChestLoadout(day, time, expiry, advancement, target.getSnapshotInventory().getContents());

        broadcast(Messages.CMD_LOCKED_CHEST_LOADOUT_CREATED.getMessage()
                .replace("%name%", name)
                .replace("%day%", String.valueOf(day))
                .replace("%time%", String.valueOf(time))
                .replace("%unit%", Messages.Unit.SECONDS.tl(time)), 3, args);
        return CommandResult.SUCCESS;
    }
}
