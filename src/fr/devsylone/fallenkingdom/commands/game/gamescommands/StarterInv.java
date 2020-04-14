package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.manager.CommandManager;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.PacketUtils;

public class StarterInv extends FkGameCommand
{
	public StarterInv()
	{
		super("starterInv", "<save|undo|show>", 1, Messages.CMD_MAP_GAME_STARTER_INV.getMessage());
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if(!args[0].equalsIgnoreCase("show") && !Fk.getInstance().getCommandManager().hasPermission(sender, ADMIN_PERMISSION))
			throw new FkLightException(CommandManager.NO_PERMISSION_MSG);

		if(args[0].equalsIgnoreCase("undo"))
		{
			if(Fk.getInstance().getStarterInventoryManager().undo())
				fkp.sendMessage(Messages.CMD_GAME_STARTER_INV_UNDO);
			else
				throw new FkLightException(Messages.CMD_ERROR_STARTER_INV_CANNOT_UNDO);
		}
		else if(args[0].equalsIgnoreCase("save"))
		{
			Fk.getInstance().getStarterInventoryManager().setStarterInv(sender.getInventory());

			String message = "{\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/fk game starterInv undo\"},\"text\":\"" + Messages.CMD_GAME_STARTER_INV_CANCEL + "\"}";
			PacketUtils.sendJSON(sender, message);
		}
		else if(args[0].equalsIgnoreCase("show"))
		{
			Fk.getInstance().getStarterInventoryManager().show(sender);
		}
	}
}
