package fr.devsylone.fallenkingdom.commands.chests.chestscommands;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.chests.FkChestsCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.lockedchests.LockedChest;

public class Remove extends FkChestsCommand
{
	public Remove()
	{
		super("remove", "Le coffre devant vous redeviendra normal");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Block target = sender.getTargetBlock(Sets.newHashSet(Material.AIR), 10);
		LockedChest chest = Fk.getInstance().getFkPI().getLockedChestsManager().getChestAt(target.getLocation());
		
		if(!Fk.getInstance().getFkPI().getLockedChestsManager().remove(target.getLocation()))
			throw new FkLightException("Vous devez regarder un coffre à crocheter pour le remettre normal");
		broadcast("§cLe coffre à crocheter §5" + chest.getName() + "§c a été supprimé");

	}
}