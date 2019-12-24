package fr.devsylone.fallenkingdom.commands.chests.chestscommands;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.chests.FkChestsCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.lockedchests.LockedChest;

import java.util.Set;

public class Add extends FkChestsCommand
{
	public Add()
	{
		super("add", "<day> <time> (en secondes) [name] ", 2, "Le coffre devant vous deviendra un coffre à crocheter durant le temps défini, crochetable à partir du jour défini, avec un nom ou pas");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if(!args[0].matches("\\d+") || Integer.parseInt(args[0]) < 1)
			throw new FkLightException("Le jour doit être un nombre entier supérieur à 1");

		if(!args[1].matches("\\d+") || Integer.parseInt(args[1]) < 0)
			throw new FkLightException("Le temps en seconde doit être un nombre entier supérieur à 0");

		Block target = sender.getTargetBlock((Set<Material>)null, 10);
		
		if(!target.getType().equals(Material.CHEST))
			throw new FkLightException("Vous devez regarder (pointer) vers un coffre pour le transformer en coffre à crocheter");

		String name = args.length >= 3 ? args[2] : "" + Fk.getInstance().getFkPI().getLockedChestsManager().getChestList().size();

		Fk.getInstance().getFkPI().getLockedChestsManager().addOrEdit(new LockedChest(target.getLocation(), Integer.parseInt(args[1]), Integer.parseInt(args[0]), name));

		broadcast("§2Un nouveau coffre à crocheter a été créé : §5" + name + " §2à la position x:" + target.getLocation().getBlockX() + " y:" + target.getLocation().getBlockY() + " z:" + target.getLocation().getBlockZ() + " \n§aIl sera crochetable à partir du jour " + args[0] + ". La durée de crochetage est de " + args[1] + " seconde(s)");

	}
}