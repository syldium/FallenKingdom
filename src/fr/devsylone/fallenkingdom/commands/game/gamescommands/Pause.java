package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.utils.FkSound;
import fr.devsylone.fallenkingdom.utils.Messages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Pause extends FkCommand
{
	public Pause()
	{
		super("pause", Messages.CMD_MAP_GAME_PAUSE, CommandPermission.ADMIN);
	}

	@Override
	@SuppressWarnings("deprecation")
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		if(plugin.getGame().getState().equals(Game.GameState.BEFORE_STARTING))
			throw new FkLightException(Messages.CMD_ERROR_GAME_NOT_STARTED);
		if(plugin.getGame().getState().equals(Game.GameState.PAUSE))
			throw new FkLightException(Messages.CMD_ERROR_ALREADY_IN_PAUSE);
		plugin.getGame().setState(Game.GameState.PAUSE);

		for(World w : Bukkit.getWorlds())
			if (plugin.getWorldManager().isAffected(w))
				w.setGameRuleValue("doDaylightCycle", "false");

		plugin.getDeepPauseManager().removeAIs();
		plugin.getDeepPauseManager().protectDespawnItems();

		int id = plugin.getPauseRestorer().registerAll();
		sender.sendMessage("§b§m-----------");
		sender.sendMessage(Messages.CMD_GAME_PAUSE_SAVE_INFO.getMessage());

		TextComponent message = new TextComponent(Messages.CMD_GAME_PAUSE_RESTORE_INVITE.getMessage());
		message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fk game restore " + id));
		if(sender instanceof Player)
			((Player) sender).spigot().sendMessage(message);

		sender.sendMessage("§b§m-----------");
		broadcast(Messages.CMD_GAME_PAUSE.getMessage());
		broadcast(Messages.CMD_GAME_PAUSE_ID.getMessage().replace("%id%", String.valueOf(id)), FkSound.NOTE_BASS_GUITAR);
		return CommandResult.SUCCESS;
	}
}
