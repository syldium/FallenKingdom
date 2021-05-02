package fr.devsylone.fallenkingdom.commands.rules;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class FkBooleanRuleCommand extends FkCommand
{
	private final Rule<Boolean> booleanRule;

	public FkBooleanRuleCommand(String path, Messages description, Rule<Boolean> booleanRule)
	{
		super(path, "<b:true|false>", description, CommandRole.ADMIN);
		this.booleanRule = booleanRule;
	}

	@Override
	public final CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
		boolean value = ArgumentParser.parseBoolean(args.get(0), Messages.CMD_ERROR_BOOL_FORMAT);
		if(value == FkPI.getInstance().getRulesManager().getRule(booleanRule))
			throw new FkLightException(Messages.CMD_ERROR_RULES_ALREADY_DEFINED);

		FkPI.getInstance().getRulesManager().setRule(booleanRule, value);
		sendMessage(value);
		return CommandResult.SUCCESS;
	}

	protected abstract void sendMessage(boolean newValue);

	protected void broadcastOnOff(boolean state, Messages msg)
	{
		broadcastState(state, msg, Messages.CMD_RULES_ACTIVATED, Messages.CMD_RULES_DEACTIVATED);
	}

	protected void broadcastPossibleImpossible(boolean state, Messages msg)
	{
		broadcastState(state, msg, Messages.CMD_RULES_POSSIBLE, Messages.CMD_RULES_IMPOSSIBLE);
	}

	protected void broadcastState(boolean state, Messages base, Messages trueMsg, Messages falseMsg)
	{
		Messages value = state ? trueMsg : falseMsg;
		broadcast(base.getMessage().replace("%state%", value.getMessage()));
	}
}
