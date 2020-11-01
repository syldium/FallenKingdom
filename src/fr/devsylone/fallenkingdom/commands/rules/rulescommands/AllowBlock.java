package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import com.cryptomorin.xseries.XMaterial;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.Argument;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
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
import fr.devsylone.fkpi.util.BlockDescription;

import java.util.List;

public class AllowBlock extends FkCommand
{
	// Si ENDER dans le nom, même si c'est un mauvais material, prévenir que les yeux c'est une state du ENDER_PORTAL_FRAME
	private static final String ENDER_EYE_MSG = "§a[Note] Si vous souhaitez autoriser les joueurs à compléter les portails de l'end avec des yeux, utilisez §e/fk rules allowblock " + XMaterial.END_PORTAL_FRAME.parseMaterial().name();

	public AllowBlock()
	{
		super("allowBlock", Argument.list(Argument.create("block", false, "sinon prendra le bloc tenu en main")), Messages.CMD_MAP_RULES_ALLOW_BLOCK, CommandPermission.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
		final BlockDescription blockDescription;
		if (!(sender instanceof Player)) {
			if (args.size() <= 0) {
				return CommandResult.NOT_VALID_EXECUTOR;
			}
			blockDescription = ArgumentParser.parseBlock(args.get(0));
		} else {
			blockDescription = ArgumentParser.parseBlock(0, args, (Player) sender, true);
		}
		makeSuggestionIf(blockDescription.getMaterial().name(), "ender", ENDER_EYE_MSG, sender);

		AllowedBlocks rule = FkPI.getInstance().getRulesManager().getRule(Rule.ALLOWED_BLOCKS);

		if(rule.isAllowed(blockDescription))
			throw new FkLightException(Messages.CMD_RULES_ERROR_ALREADY_ALLOWED);

		rule.add(blockDescription);
		broadcast(Messages.CMD_RULES_ALLOW_BLOCK.getMessage().replace("%block%", blockDescription.toString()));

		return CommandResult.SUCCESS;
	}

	public void makeSuggestionIf(String haystack, String needle, String message, CommandSender sender)
	{
		if (haystack.toLowerCase().contains(needle.toLowerCase()))
			sender.sendMessage(ChatUtils.PREFIX + message);
	}
}
