package fr.devsylone.fallenkingdom.commands.teams.teamscommands.chestsroom;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ChestRoomRegen extends FkCommand {

    public ChestRoomRegen() {
        super("regen", "<i:health> <i:perAlly>", Messages.CMD_MAP_CHEST_ROOM_REGEN, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) throws FkLightException, IllegalArgumentException {
        final int health = ArgumentParser.parsePositiveInt(args.get(0), false, Messages.CMD_ERROR_CHEST_ROOM_INVALID_HEALTH);
        final int perAlly = ArgumentParser.parsePositiveInt(args.get(1), true, Messages.CMD_ERROR_CHEST_ROOM_INVALID_HEALTH);
        plugin.getFkPI().getChestsRoomsManager().setRegenerationPerMinute(health);
        plugin.getFkPI().getChestsRoomsManager().setRegenerationPerMinutePerAlly(perAlly);
        broadcast(Messages.CMD_TEAM_CHEST_ROOM_REGEN.getMessage().replace("%health%", args.get(0)).replace("%allyBonus%", args.get(1)));
        return CommandResult.SUCCESS;
    }
}
