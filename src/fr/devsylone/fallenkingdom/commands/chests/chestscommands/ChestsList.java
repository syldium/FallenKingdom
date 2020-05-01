package fr.devsylone.fallenkingdom.commands.chests.chestscommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ChestsList extends FkCommand
{
    public ChestsList()
    {
        super("list", Messages.CMD_MAP_CHEST_LIST, CommandPermission.PLAYER);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
    {
        if(Fk.getInstance().getFkPI().getLockedChestsManager().getChestList().size() == 0)
            throw new FkLightException(Messages.CMD_ERROR_EMPTY_CHESTS_LIST);
        StringBuilder builder = new StringBuilder(ChatColor.DARK_GREEN + "§m-----------------" + ChatColor.BLUE + " Liste " + ChatColor.DARK_GREEN + "§m-----------------" + System.lineSeparator());
        for(LockedChest chest : Fk.getInstance().getFkPI().getLockedChestsManager().getChestList())
        {
            builder.append(Messages.CMD_LOCKED_CHEST_LIST_INFO.getMessage()
                    .replace("%name%", chest.getName())
                    .replace("%day%", String.valueOf(chest.getUnlockDay()))
                    .replace("%time%", String.valueOf(chest.getUnlockingTime()))
            );
            builder.append(Messages.CMD_LIST_POSITION.getMessage()
                    .replace("%x%", String.valueOf(chest.getLocation().getBlockX()))
                    .replace("%y%", String.valueOf(chest.getLocation().getBlockY()))
                    .replace("%z%", String.valueOf(chest.getLocation().getBlockZ()))
            );
            builder.append(ChatColor.DARK_GREEN + "§m----------------------------------------").append(System.lineSeparator());
        }
        sender.sendMessage(builder.toString());
        return CommandResult.SUCCESS;
    }

    @Override
    public java.util.List<String> tabComplete(Fk plugin, CommandSender sender, java.util.List<String> args) {
        return null;
    }
}