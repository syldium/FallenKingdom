package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ChargedCreepers extends FkCommand
{
	public ChargedCreepers()
	{
		super("chargedCreepers", "<i0;100:taux de spawn> <i0;100:chance de drop> <i0;100:nombre de tnts>",
				Messages.CMD_MAP_RULES_CHARGED_CREEPERS, CommandPermission.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
		fr.devsylone.fkpi.rules.ChargedCreepers rule = FkPI.getInstance().getRulesManager().getRule(Rule.CHARGED_CREEPERS);
		int spawn = ArgumentParser.parsePercentage(args.get(0), Messages.CMD_ERROR_PERCENTAGE_FORMAT);
		int drop = ArgumentParser.parsePercentage(args.get(1), Messages.CMD_ERROR_PERCENTAGE_FORMAT);
		int tntAmount = ArgumentParser.parsePositiveInt(args.get(2), true, Messages.CMD_ERROR_POSITIVE_INT);
		rule.setValue(spawn, drop, tntAmount);

		broadcast(Messages.CMD_RULES_CHARGED_CREEPERS_SPAWN_RATE.getMessage().replace("%spawn%", args.get(0)));
		broadcast(Messages.CMD_RULES_CHARGED_CREEPERS_DROP_RATE.getMessage().replace("%rate%", args.get(1)).replace("%amount%", args.get(2)));
		return CommandResult.SUCCESS;
	}
}
