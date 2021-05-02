package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.*;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ChestLimit extends FkCommand
{
	public ChestLimit()
	{
		super("chestLimit", Argument.list(new IntegerArgument("limit", true, "mettre a 0 pour ne pas en avoir",0)), Messages.CMD_MAP_RULES_CHEST_LIMIT, CommandRole.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
		int limit = ArgumentParser.parsePositiveInt(args.get(0), true, Messages.CMD_ERROR_NAN);
		if(limit < 0)
			limit = 0;
		FkPI.getInstance().getRulesManager().setRule(Rule.CHEST_LIMIT, limit);

		if(limit == 0)
			broadcast(Messages.CMD_RULES_CHEST_LIMIT_REMOVED.getMessage());
		else
			broadcast(Messages.CMD_RULES_CHEST_LIMIT_FIXED.getMessage().replace("%limit%", args.get(0)));
		return CommandResult.SUCCESS;
	}
}
