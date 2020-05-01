package fr.devsylone.fallenkingdom.utils;

import fr.devsylone.fallenkingdom.manager.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtils
{
	public static final String DEVSYLONE = "§1§odevsylone";
	public static final String PREFIX = "§7[§5Fk§7] ";
	public static final String TEAM = "§7[§bTeams§7] ";
	public static final String RULES = "§7[§bRules§7] ";
	public static final String GAME = "§7[§bGame§7] ";
	public static final String CHESTS = "§7[§bChests§7] ";
	public static final String SCOREBOARD = "§7[§bScoreboard§7] ";
	public static final String ALERT = "§4§l[§c§lAlert§4§l] ";
	public static final String DEBUG = "§7[§cDebug§7] ";
	public static final String TIP = "§r[§2Tip§r] ";

	public static String colorMessage(Messages message)
	{
		String msg = LanguageManager.getLanguageMessage(message.getAccessor());
		return msg == null ? null : ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static void sendMessage(CommandSender sender, String message)
	{
		if (message == null || message.isEmpty()) return;
		sender.sendMessage(PREFIX + message);
	}

	public static void sendMessage(CommandSender sender, Messages message)
	{
		sendMessage(sender, colorMessage(message));
	}
}
