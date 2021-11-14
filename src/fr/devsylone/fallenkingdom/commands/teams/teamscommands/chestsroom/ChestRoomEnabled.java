package fr.devsylone.fallenkingdom.commands.teams.teamscommands.chestsroom;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ChestRoomEnabled extends FkCommand {
    public ChestRoomEnabled() {
        super("enabled", "<b:true|false>", Messages.CMD_MAP_CHEST_ROOM_ENABLED, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
    {
        if(plugin.getGame().hasStarted())
            throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_STARTED);

        boolean value = ArgumentParser.parseBoolean(args.get(0), Messages.CMD_ERROR_BOOL_FORMAT);
        plugin.getFkPI().getChestsRoomsManager().setEnabled(value);
        if(value)
            broadcast(Messages.CMD_TEAM_CHEST_ROOM_ENABLED.getMessage());
        else
            broadcast(Messages.CMD_TEAM_CHEST_ROOM_DISABLED.getMessage());
        return CommandResult.SUCCESS;
    }
}
