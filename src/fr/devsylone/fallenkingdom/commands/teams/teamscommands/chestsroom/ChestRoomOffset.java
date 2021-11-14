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

public class ChestRoomOffset extends FkCommand {
    public ChestRoomOffset() {
        super("offset", "<i:value>", Messages.CMD_MAP_CHEST_ROOM_OFFSET, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
    {
        if(plugin.getGame().hasStarted())
            throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_STARTED);

        int offset = ArgumentParser.parseOffset(args.get(0), Messages.CMD_ERROR_CHEST_ROOM_INVALID_OFFSET);

        Fk.getInstance().getFkPI().getChestsRoomsManager().setOffset(offset);
        broadcast(Messages.CMD_TEAM_CHEST_ROOM_OFFSET.getMessage()
                .replace("%offset%", args.get(0))
                .replace("%unit%", Messages.Unit.BLOCKS.tl(offset))
        );
        return CommandResult.SUCCESS;
    }
}
