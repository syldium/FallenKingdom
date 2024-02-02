package fr.devsylone.fallenkingdom.commands.chests.chestscommands;

import java.util.List;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
import fr.devsylone.fkpi.lockedchests.LockedChestLoadout;

public class LoadLoadout extends FkPlayerCommand {
    private static String USAGE = "<i:day>";

    public LoadLoadout() {
        super("loadLoadout", USAGE, Messages.CMD_MAP_CHEST_LOAD_LOADOUT, CommandRole.ADMIN);
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
        Integer day =
                ArgumentParser.parsePositiveInt(args.get(0), false, Messages.CMD_ERROR_DAY_FORMAT);
        LockedChestLoadout loadout = chest.getLoadout(day);
        if (loadout == null) {
            throw new FkLightException(Messages.CMD_ERROR_NOT_LOCKED_CHEST_LOADOUT);
        }
        target.getInventory().clear();
        target.getInventory()
                .setContents(loadout.getInventory(target.getLocation()).toArray(new ItemStack[0]));

        broadcast(Messages.CMD_LOCKED_CHEST_LOADOUT_LOADED.getMessage()
            .replace("%day%", day.toString()));
        return CommandResult.SUCCESS;
    }
}
