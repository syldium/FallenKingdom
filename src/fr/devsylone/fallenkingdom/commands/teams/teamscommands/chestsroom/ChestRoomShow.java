package fr.devsylone.fallenkingdom.commands.teams.teamscommands.chestsroom;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.entity.Player;

import java.util.List;

public class ChestRoomShow extends FkPlayerCommand {
    public ChestRoomShow() {
        super("show", "<i:sec>", Messages.CMD_MAP_CHEST_ROOM_SHOW, CommandPermission.PLAYER);
    }

    @Override
    public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label)
    {
        if(!plugin.getFkPI().getChestsRoomsManager().isEnabled())
            throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_DISABLED);

        Team team = plugin.getFkPI().getTeamManager().getPlayerTeam(sender.getName());
        if(team == null || team.getBase() == null || team.getBase().getChestsRoom() == null || !team.getBase().getChestsRoom().exists())
            throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_NONE);

        team.getBase().getChestsRoom().show(sender, ArgumentParser.parseViewTime(args.get(0), Messages.CMD_ERROR_CHEST_ROOM_INVALID_TIME));
        return CommandResult.SUCCESS;
    }
}
