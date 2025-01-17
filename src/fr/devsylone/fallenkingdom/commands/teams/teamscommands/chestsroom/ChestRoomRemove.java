package fr.devsylone.fallenkingdom.commands.teams.teamscommands.chestsroom;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.teams.CrystalCore;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ChestRoomRemove extends FkCommand {

    public ChestRoomRemove() {
        super("remove", "<team>", Messages.CMD_MAP_CHEST_ROOM_REMOVE, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
        final Team team = plugin.getFkPI().getTeamManager().getTeam(args.get(0));
        if (team == null || team.getBase() == null) {
            throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_NO_BASE);
        }
        if (!(team.getBase().getNexus() instanceof CrystalCore)) {
            throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_NO_CRYSTAL);
        }

        team.getBase().resetChestRoom();
        ChatUtils.sendMessage(sender, Messages.CMD_TEAM_CHEST_ROOM_REMOVED.getMessage().replace("%team%", team.toString()));
        return CommandResult.SUCCESS;
    }
}
