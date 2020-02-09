package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import java.util.List;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fkpi.rules.AllowedBlocks;
import fr.devsylone.fkpi.util.BlockDescription;

public class AllowBlock extends FkRuleCommand
{
	// Si ENDER dans le nom, même si c'est un mauvais material, prevenir que les yeux c'est une state du ENDER_PORTAL_FRAME
	private static final String ENDER_EYE_MSG = "§a[Note] Si vous souhaitez autoriser les joueurs à compléter les portails de l'end avec des yeux, utilisez §e/fk rules allowblock " + XMaterial.END_PORTAL_FRAME.parseMaterial().name();

	public AllowBlock()
	{
		super("allowBlock", "[block] OU /fk rules allowBlock (prendra l'item dans votre main)", 0, "Permet de pouvoir poser un bloc en dehors de sa base.");
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
				throw new FkLightException(block + " n'est pas un bloc ! ");
		} else {
			if(p == null || p.getItemInHand().getType().equals(Material.AIR))
				throw new FkLightException(usage);
			blockDescription = new BlockDescription(p.getItemInHand());
			makeSuggestionIf(p.getItemInHand().getType().name(), "ender", ENDER_EYE_MSG, p);
		}
		AllowedBlocks rule = (AllowedBlocks) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("AllowedBlocks");

		if(rule.isAllowed(blockDescription))
			throw new FkLightException("Il est déjà autorisé de poser ce block ! ");

		List<BlockDescription> list = rule.getValue();
		list.add(blockDescription);
		broadcast("Le bloc", blockDescription.toString(), "peut maintenant être posé en dehors de sa base ! ");
	}

	public void makeSuggestionIf(String haystack, String needle, String message, Player player)
	{
		if (haystack.toLowerCase().contains(needle.toLowerCase()))
			player.sendMessage(ChatUtils.PREFIX + message);
	}
}
