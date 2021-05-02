package fr.devsylone.fallenkingdom.commands.chests.chestscommands;

import com.google.common.collect.Sets;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class ChestUnlock extends FkPlayerCommand {

    public ChestUnlock() {
        super("unlock", "[chest]", Messages.CMD_MAP_CHEST_UNLOCK, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label) {
        LockedChest chest;
        if (args.size() < 1) {
            Block target = sender.getTargetBlock(Sets.newHashSet(Material.AIR), 10);
            chest = Fk.getInstance().getFkPI().getLockedChestsManager().getChestAt(target.getLocation());
        } else {
            chest = ArgumentParser.getLockedChest(args.get(0));
        }

        if (chest == null) {
            throw new FkLightException(Messages.CMD_ERROR_NOT_LOCKED_CHEST);
        }
        chest.setRequiredAdvancement(null);
        ChatUtils.sendMessage(sender, Messages.CMD_LOCKED_CHEST_UNLOCKED.getMessage().replace("%name%", chest.getName()));
        return CommandResult.SUCCESS;
    }
}
