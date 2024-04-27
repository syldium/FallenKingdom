package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

	private static final char GLOBAL_CHAT_ONLY = ' ';

	@EventHandler
	public void event(AsyncPlayerChatEvent event) {
		if (!Fk.getInstance().getWorldManager().isAffected(event.getPlayer().getWorld())) {
			return;
		}

		String message = event.getMessage();
		if (message.isEmpty()) {
			return;
		}

		Messages channel = Messages.CHAT_GLOBAL;
		Team playerTeam = FkPI.getInstance().getTeamManager().getPlayerTeam(event.getPlayer());
		char globalChat = FkPI.getInstance().getRulesManager().getRule(Rule.GLOBAL_CHAT_PREFIX);
		if (playerTeam != null && globalChat != GLOBAL_CHAT_ONLY) {
			if (message.charAt(0) == globalChat) {
				message = message.substring(1);
			} else {
				channel = Messages.CHAT_TEAM;
			}
		}

		event.setMessage(message);
		String format = channel.getMessage() + "%s" + ChatColor.WHITE + " : %s";
		event.setFormat(format);

		if (channel == Messages.CHAT_TEAM) {
			event.getRecipients().clear();
			for (String playerName : playerTeam.getPlayers()) {
				Player player = Bukkit.getPlayer(playerName);
				if (player != null) {
					event.getRecipients().add(player);
				}
			}
		}
	}
}
