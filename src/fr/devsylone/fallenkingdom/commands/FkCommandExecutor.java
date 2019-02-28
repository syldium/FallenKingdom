package fr.devsylone.fallenkingdom.commands;

import java.util.LinkedHashMap;

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
	public static LinkedHashMap<String, Boolean> logs;
	private int i = 0;

	public FkCommandExecutor()
	{
		logs = new LinkedHashMap<String, Boolean>();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player))
		{
			if(args.length > 0 && args[0].equals("updated"))
				UpdateUtils.deleteUpdater(args[1]);
			else
				sender.sendMessage(ChatColor.DARK_RED + "Only player can use this command !");
			return true;
		}

		FkPlayer fkp = Fk.getInstance().getPlayerManager().getPlayer(sender.getName());
		try
		{
			Fk.getInstance().getCommandManager().executeCommand(args, (Player) sender);
			logs.put(++i + ". " + sender.getName() + " ->" + "/fk " + String.join(" ", args), Boolean.valueOf(true));

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
			logs.put(++i + ". " + sender.getName() + " ->" + "/fk " + String.join(" ", args), Boolean.valueOf(false));
			fkp.sendMessage(ChatColor.RED + "Une erreur inconnue est survenue, merci de la signaler");
			e.printStackTrace();
		}
		return true;
	}

}
