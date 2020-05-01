package fr.devsylone.fallenkingdom.commands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.manager.CommandManager;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FkCommandExecutor extends CommandManager implements CommandExecutor, TabExecutor
{
	protected final Fk plugin;
	protected final PluginCommand pluginCommand;

	public FkCommandExecutor(Fk plugin, PluginCommand command) {
		this.plugin = plugin;
		this.pluginCommand = command;
		command.setExecutor(this);
		command.setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		List<String> arguments = new ArrayList<>(Arrays.asList(args)); // Copie de la liste pour pouvoir modifier sa taille
		CommandResult result = executeCommand(plugin, sender, label, arguments);
		if (result.equals(CommandResult.NO_PERMISSION))
			ChatUtils.sendMessage(sender, ChatColor.RED + Messages.CMD_ERROR_NO_PERMISSION.getMessage());
		else if (result.equals(CommandResult.NOT_VALID_EXECUTOR))
			ChatUtils.sendMessage(sender, ChatColor.RED + Messages.CMD_ERROR_MUST_BE_PLAYER.getMessage());
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return tabCompleteCommand(plugin, sender, new ArrayList<>(Arrays.asList(args)));
	}
}
