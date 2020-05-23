package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.FkSound;
import fr.devsylone.fallenkingdom.utils.Messages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class Pause extends FkPlayerCommand // La commande pourrait être exécutée par la console, mais CommandSender#spigot() n'est pas disponible en 1.8
{
	public Pause()
	{
		super("pause", Messages.CMD_MAP_GAME_PAUSE, CommandPermission.ADMIN);
	}

	@Override
	@SuppressWarnings("deprecation")
	public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label)
	{
		if(Fk.getInstance().getGame().getState().equals(Game.GameState.BEFORE_STARTING))
			throw new FkLightException(Messages.CMD_ERROR_GAME_NOT_STARTED);
		if(Fk.getInstance().getGame().getState().equals(Game.GameState.PAUSE))
			throw new FkLightException(Messages.CMD_ERROR_ALREADY_IN_PAUSE);
		Fk.getInstance().getGame().setState(Game.GameState.PAUSE);

		for(World w : Bukkit.getWorlds())
			w.setGameRuleValue("doDaylightCycle", "false");

		Fk.getInstance().getDeepPauseManager().removeAIs();
		Fk.getInstance().getDeepPauseManager().protectDespawnItems();

		int id = Fk.getInstance().getPauseRestorer().registerAll();
		sender.sendMessage("§b§m-----------");
		sender.sendMessage(Messages.CMD_GAME_PAUSE_SAVE_INFO.getMessage());

		TextComponent message = new TextComponent(Messages.CMD_GAME_PAUSE_RESTORE_INVITE.getMessage());
		message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fk game restore " + id));
		sender.spigot().sendMessage(message);

		sender.sendMessage("§b§m-----------");
		broadcast(Messages.CMD_GAME_PAUSE.getMessage());
		broadcast(Messages.CMD_GAME_PAUSE_ID.getMessage().replace("%id%", String.valueOf(id)), FkSound.NOTE_BASS_GUITAR);
		return CommandResult.SUCCESS;
	}
}
