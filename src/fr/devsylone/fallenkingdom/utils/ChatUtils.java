package fr.devsylone.fallenkingdom.utils;

import fr.devsylone.fallenkingdom.Fk;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ChatUtils
{
	public static final String DEVSYLONE = "§1§odevsylone";

	private ChatUtils() throws IllegalAccessException {
		throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
	}

	public static String colorMessage(Messages message)
	{
		String msg = Fk.getInstance().getLanguageManager().get(message);
		return msg == null ? null : ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static void sendMessage(CommandSender sender, String message) {
		if (message == null || message.isEmpty()) return;

		if (Fk.PAPI_ENABLED) {
			if (sender instanceof Player) {
				message = PlaceholderAPI.setPlaceholders((Player) sender, message);
			} else {
				UUID dummyUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
				OfflinePlayer dummyPlayer = Bukkit.getOfflinePlayer(dummyUUID);
				message = PlaceholderAPI.setPlaceholders(dummyPlayer, message);
			}
		}

		sender.sendMessage(Messages.PREFIX_FK.getMessage() + message);
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
