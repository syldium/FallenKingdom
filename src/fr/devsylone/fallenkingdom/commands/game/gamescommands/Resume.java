package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.utils.FkSound;

import java.util.List;

public class Resume extends FkCommand
{
	public Resume()
	{
		super("resume", Messages.CMD_MAP_GAME_RESUME, CommandPermission.ADMIN);
	}

	@Override
	@SuppressWarnings("deprecation")
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		if(plugin.getGame().getState().equals(Game.GameState.BEFORE_STARTING))
			throw new FkLightException(Messages.CMD_ERROR_GAME_NOT_STARTED);
		if(plugin.getGame().getState().equals(Game.GameState.STARTED))
			throw new FkLightException(Messages.CMD_ERROR_NOT_IN_PAUSE);

		plugin.getGame().startTimer();
		plugin.getGame().setState(Game.GameState.STARTED);

		if(!plugin.getFkPI().getRulesManager().getRule(Rule.ETERNAL_DAY))
		{
			for(World w : Bukkit.getWorlds())
				if (plugin.getWorldManager().isAffected(w))
					w.setGameRuleValue("doDaylightCycle", "true");
		}
		plugin.getDeepPauseManager().unfreezePlayers();
		plugin.getDeepPauseManager().resetAIs();
		plugin.getDeepPauseManager().unprotectItems();

		broadcast(Messages.CMD_GAME_RESUME.getMessage(), FkSound.NOTE_PIANO);
		return CommandResult.SUCCESS;
	}
}
