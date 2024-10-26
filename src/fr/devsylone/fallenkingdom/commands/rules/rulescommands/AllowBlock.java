package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import com.cryptomorin.xseries.XMaterial;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.ArgumentParser.MaterialWithData;
import fr.devsylone.fallenkingdom.commands.abstraction.Argument;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fkpi.rules.AllowedBlocks;

import java.util.List;

public class AllowBlock extends FkCommand
{
	public AllowBlock()
	{
		super("allowBlock", Argument.list(Argument.create("block", false, "sinon prendra le bloc tenu en main")), Messages.CMD_MAP_RULES_ALLOW_BLOCK, CommandRole.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
		final MaterialWithData material;
		if (!(sender instanceof Player)) {
			if (args.size() <= 0) {
				return CommandResult.NOT_VALID_EXECUTOR;
			}
			material = ArgumentParser.parseBlock(args.get(0), (input) -> enderEyeSuggestion(sender, input));
		} else {
			material = ArgumentParser.parseBlock(0, args, (Player) sender, true, AllowBlock::enderEyeSuggestion);
		}

		enderEyeSuggestion(sender, material.getMaterial().name());

		AllowedBlocks rule = FkPI.getInstance().getRulesManager().getRule(Rule.ALLOWED_BLOCKS);

		if(rule.isAllowed(material.getMaterial(), material.getData()))
			throw new FkLightException(Messages.CMD_RULES_ERROR_ALREADY_ALLOWED);

		rule.add(material.getMaterial(), material.getData());
		broadcast(Messages.CMD_RULES_ALLOW_BLOCK.getMessage().replace("%block%", material.toString()));

		return CommandResult.SUCCESS;
	}

	private static void enderEyeSuggestion(CommandSender sender, String input) {
		makeSuggestionIf(input, "ender", Messages.CMD_RULES_ENDER_EYE_MSG + " " + XMaterial.END_PORTAL_FRAME.parseMaterial().name() + "&a.", sender);
	}

	private static void makeSuggestionIf(String haystack, String needle, String message, CommandSender sender)
	{
		if (haystack.toLowerCase().contains(needle.toLowerCase()))
			ChatUtils.sendMessage(sender, message);
	}
}
