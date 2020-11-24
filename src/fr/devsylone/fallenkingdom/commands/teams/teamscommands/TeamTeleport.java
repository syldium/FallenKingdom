package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.SafeLocationSearcher;
import fr.devsylone.fkpi.teams.Base;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.List;

public class TeamTeleport extends FkPlayerCommand {

    public TeamTeleport() {
        super("tp", "[team]", Messages.CMD_MAP_TEAM_LIST, CommandPermission.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label) {
        Team team = args.size() < 1 ? plugin.getFkPI().getTeamManager().getPlayerTeam(sender) : plugin.getFkPI().getTeamManager().getTeam(args.get(0));
        if (team == null || team.getBase() == null)
            throw new FkLightException(Messages.CMD_ERROR_NO_TEAM);
        teleport(team.getBase(), sender, TeleportCause.COMMAND);
        return CommandResult.SUCCESS;
    }

    public static void teleport(Base base, Entity entity, TeleportCause cause) {
        new SafeLocationSearcher(base.getCenter())
                .find(Math.min(base.getRadius(), 8))
                .thenApply(loc -> entity.teleport(loc, cause))
                .exceptionally(throwable -> {
                    ChatUtils.sendMessage(entity, Messages.PLAYER_BASE_OBSTRUCTED);
                    if (!(throwable.getCause() instanceof SafeLocationSearcher.LocationNotFound)) {
                        throwable.getCause().printStackTrace();
                    }
                    return true;
                });
    }

    public static void teleport(Base base, Entity entity) {
        teleport(base, entity, TeleportCause.PLUGIN);
    }
}
