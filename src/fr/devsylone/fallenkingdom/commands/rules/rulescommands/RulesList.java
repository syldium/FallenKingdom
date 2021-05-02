package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.utils.RulesFormatter;

public class RulesList extends FkCommand
{
	public RulesList()
	{
		super("list", Messages.CMD_MAP_RULES_LIST, CommandRole.PLAYER);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, java.util.List<String> args, String label) {
		sender.sendMessage(ChatUtils.PREFIX + "§7§m--------§b " + Messages.CMD_RULES_LIST + " §7§m--------\n");
		for(String s : RulesFormatter.formatRules())
			sender.sendMessage(ChatUtils.PREFIX + s);
		sender.sendMessage(ChatUtils.PREFIX + "§7§m------------------------------\n");
		return CommandResult.SUCCESS;
	}
}
