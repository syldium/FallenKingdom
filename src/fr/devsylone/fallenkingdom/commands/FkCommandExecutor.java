package fr.devsylone.fallenkingdom.commands;

import java.util.LinkedHashMap;
import java.util.Map;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.DebuggerUtils;
import fr.devsylone.fallenkingdom.utils.UpdateUtils;

public class FkCommandExecutor implements CommandExecutor
{
	public static Map<String, Boolean> logs = new LinkedHashMap<>();
	private int i = 0;

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player))
		{
			if(args.length > 0 && args[0].equals("updated"))
				UpdateUtils.deleteUpdater(args[1]);
			else
				sender.sendMessage(ChatColor.DARK_RED + Messages.CMD_ERROR_MUST_BE_PLAYER.getMessage());
			return true;
		}

		FkPlayer fkp = Fk.getInstance().getPlayerManager().getPlayer(sender.getName());
		try
		{
			Fk.getInstance().getCommandManager().executeCommand(args, (Player) sender);
			logs.put(++i + ". " + sender.getName() + " ->" + "/fk " + String.join(" ", args), Boolean.TRUE);

		}catch(FkLightException e)
		{
			if(e.getMessage() !=null && e.getMessage().contains("debug_fake_error"))
			{
				DebuggerUtils.debugGame();
				fkp.sendMessage("Done");
				return true;
			}
			fkp.sendMessage(ChatColor.RED + e.getMessage());
			Fk.getInstance().getLogger().info("Light error : " + e.getMessage());
		}catch(Exception e)
		{
			logs.put(++i + ". " + sender.getName() + " ->" + "/fk " + String.join(" ", args), Boolean.FALSE);
			fkp.sendMessage(ChatColor.RED + Messages.CMD_ERROR.getMessage());
			e.printStackTrace();
		}
		return true;
	}

}
