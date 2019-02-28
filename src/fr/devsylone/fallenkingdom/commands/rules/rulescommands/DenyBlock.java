package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import java.util.List;

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

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Player p = org.bukkit.Bukkit.getPlayer(sender.getName());
		Material m;
		
		try
		{
			String block = args[0];
			m = Material.matchMaterial(block);

			if (m == null)
				throw new FkLightException(block + " n'est pas un bloc !");

		}
		catch (ArrayIndexOutOfBoundsException e)
		{

			if ((p == null) || (p.getItemInHand().getType() == Material.AIR))
				throw new FkLightException(usage);
			
			m = p.getItemInHand().getType();
		}
		AllowedBlocks rule = (AllowedBlocks) Fk.getInstance().getFkPI().getRulesManager()
				.getRuleByName("AllowedBlocks");

		if (!rule.getValue().contains(m.toString()))
			throw new FkLightException("Il est déjà interdit de poser ce block ! ");
		
		List<String> list = rule.getValue();
		list.remove(m.name());
		Fk.getInstance().getFkPI().getRulesManager().getRuleByName("AllowedBlocks").setValue(list);

		broadcast("Le bloc ", m.toString(), "ne peut plus être posé en dehors de sa base !");
	}
}
