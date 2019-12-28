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
	// Si ENDER dans le nom, même si c'est un mauvais material, prevenir que les yeux c'est une state du ENDER_PORTAL_FRAME
	private static final String ENDER_EYE_MSG =  "§a[Note] Si vous souhaitez autoriser les joueurs à compléter les portails de l'end avec des yeux, utilisez §e/fk rules allowblock ENDER_PORTAL_FRAME";

	public AllowBlock()
	{
		super("allowBlock", "[block] OU /fk rules allowBlock (prendra l'item dans votre main)", 0, "Permet de pouvoir poser un bloc en dehors de sa base.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Player p = org.bukkit.Bukkit.getPlayer(sender.getName());
		
		Material m = null;
		if(args.length > 0) {
			String block = args[0];
			m = Material.matchMaterial(block);
			makeSuggestionIf(block, "ender", ENDER_EYE_MSG, p);
			if(m == null)
				throw new FkLightException(block + " n'est pas un bloc ! ");
		} else {
			if(p == null || p.getItemInHand().getType().equals(Material.AIR))
				throw new FkLightException(usage);
			m = p.getItemInHand().getType();
			makeSuggestionIf(m.name(), "ender", ENDER_EYE_MSG, p);
		}
		AllowedBlocks rule = (AllowedBlocks) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("AllowedBlocks");

		List<String> list = rule.getValue();
		if(list.contains(m.name()))
			throw new FkLightException("Il est déjà autorisé de poser ce block ! ");
		
		list.add(m.name());
		broadcast("Le bloc", m.toString(), "peut maintenant être posé en dehors de sa base ! ");
	}

	public void makeSuggestionIf(String haystack, String needle, String message, Player player)
	{
		if (haystack.toLowerCase().contains(needle.toLowerCase()))
			player.sendMessage(ChatUtils.PREFIX + message);
	}
}
