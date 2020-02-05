package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import java.util.List;

import fr.devsylone.fkpi.util.BlockDescription;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.rules.AllowedBlocks;

public class DenyBlock extends FkRuleCommand
{
	public DenyBlock()
	{
		super("denyBlock", "[block] OU /fk rules denyBlock (prendra l'item dans votre main)", 0,
				"Annule l'effet du allowBlock sur le bloc choisi.");
	}

	@SuppressWarnings("deprecation")
	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Player p = org.bukkit.Bukkit.getPlayer(sender.getName());

		final BlockDescription blockDescription;
		if(args.length > 0) {
			String block = args[0];
			blockDescription = new BlockDescription(block);
			if(Material.matchMaterial(blockDescription.getBlockName()) == null)
				throw new FkLightException(block + " n'est pas un bloc ! ");
		} else {
			if(p == null || p.getItemInHand().getType().equals(Material.AIR))
				throw new FkLightException(usage);
			blockDescription = new BlockDescription(p.getItemInHand());
		}
		AllowedBlocks rule = (AllowedBlocks) Fk.getInstance().getFkPI().getRulesManager()
				.getRuleByName("AllowedBlocks");

		if (!rule.isAllowed(blockDescription))
			throw new FkLightException("Il est déjà interdit de poser ce block ! ");
		
		List<BlockDescription> list = rule.getValue();
		// Si le nom de bloc donné est moins précis que ceux enregistrés (ex : WOOL avec WOOL:2, WOOL:3)
		// on supprime toutes les occurences de WOOL (donc des fois plusieurs)
		list.removeIf(b -> b.equals(blockDescription));
		Fk.getInstance().getFkPI().getRulesManager().getRuleByName("AllowedBlocks").setValue(list);

		broadcast("Le bloc ", blockDescription.toString(), "ne peut plus être posé en dehors de sa base !");
	}
}
