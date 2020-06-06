package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.*;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.util.BlockDescription;
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
		BlockDescription block = ArgumentParser.parseBlock(2, args, sender, false);

		if(!plugin.getFkPI().getTeamManager().getTeamNames().contains(args.get(0)))
			throw new FkLightException(Messages.CMD_ERROR_UNKNOWN_TEAM.getMessage().replace("%team%", args.get(0)));

		if(!Fk.getInstance().getWorldManager().isAffected(sender.getWorld()))
			throw new FkLightException(Messages.CMD_ERROR_NOT_AFFECTED_WORLD.getMessage());

		Base base = new Base(plugin.getFkPI().getTeamManager().getTeam(args.get(0)), sender.getLocation(), radius, Material.getMaterial(block.getBlockName()), block.getData());
		plugin.getFkPI().getTeamManager().getTeam(args.get(0)).setBase(base);
		base.construct();
		broadcast(Messages.CMD_TEAM_SET_BASE.getMessage()
				.replace("%team%", args.get(0))
				.replace("%x%", String.valueOf(base.getCenter().getBlockX()))
				.replace("%z%", String.valueOf(base.getCenter().getBlockZ())),
		4, args);
		plugin.getScoreboardManager().refreshAllScoreboards();
		return CommandResult.SUCCESS;
	}
}
