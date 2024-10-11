package fr.devsylone.fallenkingdom.commands.rules;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.command.CommandSender;

import java.util.List;

public class FkCapCommand extends FkCommand
{
	protected final Rule<Integer> cap;
	protected final Messages successMsg;

	public FkCapCommand(String path, Messages description, Rule<Integer> cap, Messages successMsg)
	{
		super(path, "<i1:day>", description, CommandRole.ADMIN);
		this.cap = cap;
		this.successMsg = successMsg;
	}

	@Override
	public final CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
		int day = ArgumentParser.parsePositiveInt(args.get(0), false, Messages.CMD_ERROR_DAY_FORMAT);

		int currentCap = plugin.getFkPI().getRulesManager().getRule(cap);
		if (currentCap == day) {
			throw new FkLightException(Messages.CMD_ERROR_DAY_ALREADY.getMessage()
					.replace("%day%", String.valueOf(day))
			);
		}

		if (currentCap <= plugin.getGame().getDay()) {
			ChatUtils.sendMessage(sender, Messages.CMD_RULES_CAP_PASSED.getMessage()
					.replace("%day%", String.valueOf(currentCap))
			);
		} else if (day < plugin.getGame().getDay()) {
			ChatUtils.sendMessage(sender, Messages.CMD_RULES_CAP_PASSED_DAY.getMessage()
					.replace("%day%", String.valueOf(day))
			);
		}
		FkPI.getInstance().getRulesManager().setRule(cap, day);
		broadcast(Messages.CMD_RULES_CAP.getMessage()
				.replace("%first%", successMsg.getMessage())
				.replace("%from%", (day == 1 ? Messages.CMD_RULES_CAP_FROM_DAY_1 : Messages.CMD_RULES_CAP_FROM_DAY).getMessage())
				.replace("%day%", String.valueOf(day))
		);
		return CommandResult.SUCCESS;
	}
}
