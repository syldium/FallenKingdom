package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fkpi.rules.AllowedBlocks;

public class AllowBlock extends FkRuleCommand
{
	public AllowBlock()
	{
		super("allowBlock", "[block] OU /fk rules allowBlock (prendra l'item dans votre main)", 0, "Permet de pouvoir poser un bloc en dehors de sa base.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Player p = org.bukkit.Bukkit.getPlayer(sender.getName());



		// Si ENDER dans le nom, même si c'est un mauvais material, prevenir que les yeux c'est une state du ENDER_PORTAL_FRAME
		if(args[0].contains("ender"))
			p.sendMessage(ChatUtils.PREFIX + "§a[Note] Si vous souhaitez autoriser les joueurs à compléter les portails de l'end avec des yeux, utilisez §e/fk rules allowblock ENDER_PORTAL_FRAME");
		
		Material m = null;
		try
		{
			String block = args[0];
			m = Material.matchMaterial(block);
			if(m == null)
				throw new FkLightException(block + " n'est pas un bloc ! ");
		}catch(ArrayIndexOutOfBoundsException e)
		{
			if(p == null || p.getItemInHand().getType().equals(Material.AIR))
				throw new FkLightException(usage);

			m = p.getItemInHand().getType();
		}
		AllowedBlocks rule = (AllowedBlocks) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("AllowedBlocks");

		List<String> list = rule.getValue();
		if(list.contains(m.name()))
			throw new FkLightException("Il est déjà autorisé de poser ce block ! ");
		
		list.add(m.name());
		broadcast("Le bloc", m.toString(), "peut maintenant être posé en dehors de sa base ! ");
	}
}
