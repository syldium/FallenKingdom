package fr.devsylone.fallenkingdom.commands.teams.teamscommands.chestsroom;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.teams.CrystalCore;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

import static fr.devsylone.fkpi.teams.Base.adjustLoc;

public class ChestRoomSpawn extends FkPlayerCommand {

    public ChestRoomSpawn() {
        super("spawn", "<team>", Messages.CMD_MAP_CHEST_ROOM_SPAWN, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label) {
        if (!Version.VersionType.V1_9_V1_12.isHigherOrEqual()) {
            throw new FkLightException(Messages.CMD_ERROR_VERSION_TOO_OLD.getMessage().replace("%version%", Version.VersionType.V1_9_V1_12.toString()));
        }
        if (!plugin.getFkPI().getChestsRoomsManager().isEnabled()) {
            throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_DISABLED);
        }

        final Team team = plugin.getFkPI().getTeamManager().getTeam(args.get(0));
        if (team == null || team.getBase() == null) {
            throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_NO_BASE);
        }

        final Location spawnLocation = adjustLoc(sender.getLocation()).add(0, .5, 0);
        final Entity entity = sender.getWorld().spawn(spawnLocation, EnderCrystal.class);
        team.getBase().setChestsRoom(new CrystalCore(team.getBase(), entity));
        return CommandResult.SUCCESS;
    }
}
