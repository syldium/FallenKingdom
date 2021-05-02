package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.players.FkPlayer;

import java.util.List;

public class DeathLimit extends FkCommand
{
	public DeathLimit()
	{
		super("deathLimit", "<i:limit>", Messages.CMD_MAP_RULES_DEATH_LIMIT, CommandRole.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
		int limit = ArgumentParser.parsePositiveInt(args.get(0), true, Messages.CMD_ERROR_NAN);

		FkPI.getInstance().getRulesManager().setRule(Rule.DEATH_LIMIT, limit);

		if (limit == 0)
		{
			plugin.getPlayerManager().getConnectedPlayers().forEach(FkPlayer::clearDeaths);
			broadcast(Messages.CMD_RULES_DEATH_LIMIT_RESET.getMessage());
			broadcast(Messages.CMD_RULES_DEATH_LIMIT_REMOVED.getMessage());
		}
		else
			broadcast(Messages.CMD_RULES_DEATH_LIMIT_FIXED.getMessage()
					.replace("%limit%", args.get(0))
					.replace("%unit%", Messages.Unit.DEATHS.tl(limit))
			);
		return CommandResult.SUCCESS;
	}
}
