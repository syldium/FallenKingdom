package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.manager.CommandManager;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.PacketUtils;

public class StarterInv extends FkGameCommand
{
	public StarterInv()
	{
		super("StarterInv", "<save|undo|show>", 1, "Gèrer l'inventaire que les joueurs ont au départ");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if(!args[0].equalsIgnoreCase("show") && !Fk.getInstance().getCommandManager().hasPermission(sender, ADMIN_PERMISSION))
			throw new FkLightException(CommandManager.NO_PERMISSION_MSG);

		if(args[0].equalsIgnoreCase("undo"))
		{
			if(Fk.getInstance().getStarterInventoryManager().undo())
				fkp.sendMessage("§aDernière sauvegarde de l'inventaire annulée");
			else
				fkp.sendMessage("§cImpossible de revenir en arrière !");
		}
		else if(args[0].equalsIgnoreCase("save"))
		{
			Fk.getInstance().getStarterInventoryManager().setStarterInv(sender.getInventory());

			String message = "{\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/fk game starterInv undo\"},\"text\":\"§l§c[✘ ANNULER ✘]\"}";
			PacketUtils.sendJSON(sender, message);
		}
		else if(args[0].equalsIgnoreCase("show"))
		{
			Fk.getInstance().getStarterInventoryManager().show(sender);
		}
	}
}
