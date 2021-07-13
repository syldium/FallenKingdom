package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.managers.TeamManager;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TeamRename extends FkCommand {

    public TeamRename() {
        super("rename", "<team> <name>", Messages.CMD_MAP_TEAM_RENAME, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
        final TeamManager manager = plugin.getFkPI().getTeamManager();

        if (manager.getTeam(args.get(1)) != null)
            throw new FkLightException(Messages.CMD_ERROR_TEAM_ALREADY_EXIST);

        final Team team = manager.getTeamOrThrow(args.get(0));
        final String initialName = team.toString();
        team.setName(args.get(1));
        broadcast(
                Messages.CMD_TEAM_RENAME.getMessage()
                        .replace("%team%", initialName)
                        .replace("%name%", team.toString()),
                3, args);
        plugin.getScoreboardManager().recreateAllScoreboards();
        return CommandResult.SUCCESS;
    }
}
