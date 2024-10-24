package fr.devsylone.fallenkingdom.utils;

import fr.devsylone.fallenkingdom.Fk;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class ChatUtils
{
	public static final String DEVSYLONE = "§1§odevsylone";
	public static final String PREFIX = "§8[§6FK§8] ";
	public static final String TEAM = "§8[§bTeams§8] ";
	public static final String RULES = "§8[§bRules§8] ";
	public static final String GAME = "§8[§bGame§8] ";
	public static final String CHESTS = "§8[§bChests§8] ";
	public static final String SCOREBOARD = "§8[§bScoreboard§8] ";
	public static final String ALERT = "§4§l[§c§lAlert§4§l] ";
	public static final String DEBUG = "§8[§cDebug§8] ";
	public static final String TIP = "§8[§2Tip§8] ";

	private ChatUtils() throws IllegalAccessException {
		throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
	}

	public static String colorMessage(Messages message)
	{
		String msg = Fk.getInstance().getLanguageManager().getLanguageMessage(message.getAccessor());
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

	public static String translateColorCodeToAmpersand(String text)
	{
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length - 1; i++) {
			if (chars[i] == ChatColor.COLOR_CHAR) {
				chars[i] = '&';
			}
		}
		return new String(chars);
	}

	public static String unquoteString(String str) {
		if (str.startsWith("\"") && str.endsWith("\"")) {
			return str.substring(1, str.length() - 1);
		}
		return str;
	}
}
