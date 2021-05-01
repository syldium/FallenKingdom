package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.exception.ArgumentParseException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.teams.Base;

import java.util.List;

public class SetBase extends FkPlayerCommand
{
	public SetBase()
	{
		super("setBase", "<team> <i4:radius> [block]", Messages.CMD_MAP_TEAM_SET_BASE, CommandPermission.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label) {
		int radius = ArgumentParser.parseInt(args.get(1), Messages.CMD_ERROR_RADIUS_FORMAT);
		ArgumentParser.MaterialWithData block = ArgumentParser.parseBlock(2, args, sender, false, true);

		Team team = plugin.getFkPI().getTeamManager().getTeam(args.get(0));
		if(team == null)
			throw new FkLightException(Messages.CMD_ERROR_UNKNOWN_TEAM.getMessage().replace("%team%", args.get(0)));

		if(!Fk.getInstance().getWorldManager().isAffected(sender.getWorld()))
			throw new FkLightException(Messages.CMD_ERROR_NOT_AFFECTED_WORLD.getMessage());

		if(radius < 4)
			throw new ArgumentParseException(Messages.CMD_ERROR_RADIUS_FORMAT.getMessage());

		Base base = new Base(team, sender.getLocation(), radius, block.getMaterial(), block.getData());
		plugin.getFkPI().getTeamManager().getTeam(args.get(0)).setBase(base);

		if (block.getMaterial() != Material.AIR) {
			if (base.isLoaded()) {
				base.construct();
			} else {
				fkp.sendMessage(Messages.CMD_BASE_UNLOADED);
			}
		}

		broadcast(Messages.CMD_TEAM_SET_BASE.getMessage()
				.replace("%team%", team.toString())
				.replace("%x%", String.valueOf(base.getCenter().getBlockX()))
				.replace("%z%", String.valueOf(base.getCenter().getBlockZ())),
		4, args);
		plugin.getScoreboardManager().refreshAllScoreboards();
		plugin.getWorldManager().invalidateBaseWorldsCache(plugin.getFkPI().getTeamManager());
		return CommandResult.SUCCESS;
	}
}
