package fr.devsylone.fallenkingdom.commands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.manager.CommandManager;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.UpdateUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class FkCommandExecutor extends CommandManager implements TabExecutor
{
    protected final Fk plugin;
    protected final PluginCommand pluginCommand;

    public static LinkedHashMap<String, Boolean> logs = new LinkedHashMap<>();
    private int i = 0;

    public FkCommandExecutor(Fk plugin, PluginCommand command) {
        super(plugin.getConfig().getBoolean("enable-permissions", false));
        this.plugin = plugin;
        this.pluginCommand = command;
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof ConsoleCommandSender && args.length > 0 && args[0].equals("updated"))
        {
            UpdateUtils.deleteUpdater(args[1]);
            return true;
        }

        List<String> arguments = new ArrayList<>(Arrays.asList(args)); // Copie de la liste pour pouvoir modifier sa taille
        CommandResult result = executeCommand(plugin, sender, label, arguments);
        if (result.equals(CommandResult.NO_PERMISSION))
            ChatUtils.sendMessage(sender, ChatColor.RED + Messages.CMD_ERROR_NO_PERMISSION.getMessage());
        else if (result.equals(CommandResult.NOT_VALID_EXECUTOR))
            ChatUtils.sendMessage(sender, ChatColor.RED + Messages.CMD_ERROR_MUST_BE_PLAYER.getMessage());
        logs.put(++i + ". " + sender.getName() + " ->" + "/fk " + String.join(" ", args), !result.equals(CommandResult.FAILURE));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return tabCompleteCommand(plugin, sender, new ArrayList<>(Arrays.asList(args)));
    }
}
