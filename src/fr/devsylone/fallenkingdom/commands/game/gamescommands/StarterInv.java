package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StarterInv extends FkPlayerCommand
{
	private static final String SET_PERMISSION = "fallenkingdom.commands.game.starterInv.set";

	public StarterInv()
	{
		super("starterInv", "<save|undo|show>", Messages.CMD_MAP_GAME_STARTER_INV, CommandRole.PLAYER);
	}

	@Override
	public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label)
	{
		if(!args.get(0).equalsIgnoreCase("show") && !plugin.getCommandManager().hasPermission(sender, SET_PERMISSION))
			return CommandResult.NO_PERMISSION;

		if(args.get(0).equalsIgnoreCase("undo"))
		{
			if(plugin.getStarterInventoryManager().undo())
				sender.sendMessage(Messages.CMD_GAME_STARTER_INV_UNDO.getMessage());
			else
				throw new FkLightException(Messages.CMD_ERROR_STARTER_INV_CANNOT_UNDO);
		}
		else if(args.get(0).equalsIgnoreCase("save"))
		{
			plugin.getStarterInventoryManager().setStarterInv(sender.getInventory());

			TextComponent message = new TextComponent(Messages.CMD_GAME_STARTER_INV_CANCEL.getMessage());
			message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fk game starterInv undo"));
			sender.spigot().sendMessage(message);
		}
		else if(args.get(0).equalsIgnoreCase("show"))
		{
			plugin.getStarterInventoryManager().show(sender);
		}
		return CommandResult.SUCCESS;
	}

	@Override
	public @NotNull Map<String, CommandRole> getPermissions() {
		Map<String, CommandRole> permissions = new HashMap<>();
		permissions.put(this.permission, this.role);
		permissions.put(SET_PERMISSION, CommandRole.ADMIN);
		return permissions;
	}
}
