package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.util.BlockDescription;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.rules.AllowedBlocks;

public class DenyBlock extends FkRuleCommand
{
	public DenyBlock()
	{
		super("denyBlock", "[block] OU /fk rules denyBlock (prendra l'item dans votre main)", 0,
				Messages.CMD_MAP_RULES_DENY_BLOCK);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Player p = org.bukkit.Bukkit.getPlayer(sender.getName());

		final BlockDescription blockDescription;
		if(args.length > 0) {
			String block = args[0];
			blockDescription = new BlockDescription(block);
			if(Material.matchMaterial(blockDescription.getBlockName()) == null)
				throw new FkLightException(Messages.CMD_ERROR_UNKNOWN_BLOCK.getMessage().replace("%block%", block));
		} else {
			if(p == null || p.getItemInHand().getType().equals(Material.AIR))
				throw new FkLightException(usage);
			blockDescription = new BlockDescription(p.getItemInHand());
		}
		AllowedBlocks rule = FkPI.getInstance().getRulesManager()
				.getRule(Rule.ALLOWED_BLOCKS);

		if (!rule.isAllowed(blockDescription))
			throw new FkLightException(Messages.CMD_RULES_ERROR_ALREADY_DENIED);

		// Si le nom de bloc donné est moins précis que ceux enregistrés (ex : WOOL avec WOOL:2, WOOL:3)
		// on supprime toutes les occurrences de WOOL (donc des fois plusieurs)
		rule.getValue().removeIf(b -> b.equals(blockDescription));

		broadcast(Messages.CMD_RULES_DENY_BLOCK.getMessage().replace("%block%", blockDescription.toString()));
	}
}
