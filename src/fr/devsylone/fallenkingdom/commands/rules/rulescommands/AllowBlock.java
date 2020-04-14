package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import com.cryptomorin.xseries.XMaterial;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fkpi.rules.AllowedBlocks;
import fr.devsylone.fkpi.util.BlockDescription;

public class AllowBlock extends FkRuleCommand
{
	// Si ENDER dans le nom, même si c'est un mauvais material, prévenir que les yeux c'est une state du ENDER_PORTAL_FRAME
	private static final String ENDER_EYE_MSG = "§a[Note] Si vous souhaitez autoriser les joueurs à compléter les portails de l'end avec des yeux, utilisez §e/fk rules allowblock " + XMaterial.END_PORTAL_FRAME.parseMaterial().name();

	public AllowBlock()
	{
		super("allowBlock", "[block] OU /fk rules allowBlock (prendra l'item dans votre main)", 0, Messages.CMD_MAP_RULES_ALLOW_BLOCK);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Player p = org.bukkit.Bukkit.getPlayer(sender.getName());
		
		final BlockDescription blockDescription;
		if(args.length > 0) {
			String block = args[0];
			makeSuggestionIf(block, "ender", ENDER_EYE_MSG, p);
			blockDescription = new BlockDescription(block);
			if(Material.matchMaterial(blockDescription.getBlockName()) == null)
				throw new FkLightException(Messages.CMD_ERROR_UNKNOWN_BLOCK.getMessage().replace("%block%", block));
		} else {
			if(p == null || p.getItemInHand().getType().equals(Material.AIR))
				throw new FkLightException(usage);
			blockDescription = new BlockDescription(p.getItemInHand());
			makeSuggestionIf(p.getItemInHand().getType().name(), "ender", ENDER_EYE_MSG, p);
		}
		AllowedBlocks rule = FkPI.getInstance().getRulesManager().getRule(Rule.ALLOWED_BLOCKS);

		if(rule.isAllowed(blockDescription))
			throw new FkLightException(Messages.CMD_RULES_ERROR_ALREADY_ALLOWED);

		rule.getValue().add(blockDescription);
		broadcast(Messages.CMD_RULES_ALLOW_BLOCK.getMessage().replace("%block%", blockDescription.toString()));
	}

	public void makeSuggestionIf(String haystack, String needle, String message, Player player)
	{
		if (haystack.toLowerCase().contains(needle.toLowerCase()))
			player.sendMessage(ChatUtils.PREFIX + message);
	}
}
