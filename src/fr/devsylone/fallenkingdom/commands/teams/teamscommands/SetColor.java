package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fkpi.util.Color;
import org.bukkit.entity.Player;

import java.util.List;

public class SetColor extends FkCommand
{
	public SetColor()
	{
		super("setColor", "<team> <color>", Messages.CMD_MAP_TEAM_SET_COLOR, CommandRole.ADMIN);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		Team team;
		if((team = plugin.getFkPI().getTeamManager().getTeam(args.get(0))) == null)
			throw new FkLightException(Messages.CMD_ERROR_UNKNOWN_TEAM.getMessage().replace("%team%", args.get(0)));

		try {
			team.setColor(Color.of(args.get(1)));
		} catch (NumberFormatException ex) {
			// Seulement lors des lectures des couleurs hex
			invalidColor(sender, args.get(1), ex);
			return CommandResult.INVALID_ARGS;
		}

		broadcast(Messages.CMD_TEAM_SET_COLOR.getMessage()
				.replace("%team%", team.toString())
				.replace("%color%", team.getChatColor() + team.getColor().getGenredName(Color.GENRE_F))
		);
		plugin.getScoreboardManager().recreateAllScoreboards();
		return CommandResult.SUCCESS;
	}

	public static BaseComponent invalidColor(String value, NumberFormatException ex) {
		TranslatableComponent component = new TranslatableComponent("argument.color.invalid");
		component.addWith(value);
		component.setColor(ChatColor.RED);

		component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ex.getMessage())}));
		return component;
	}

	public static void invalidColor(CommandSender sender, String value, NumberFormatException ex) {
		if (sender instanceof Player) {
			((Player) sender).spigot().sendMessage(invalidColor(value, ex));
		} else {
			sender.sendMessage(invalidColor(value, ex).toLegacyText());
		}
	}
}
