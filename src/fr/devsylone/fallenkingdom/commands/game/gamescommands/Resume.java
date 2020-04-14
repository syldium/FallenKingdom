package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.World;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.FkSound;

public class Resume extends FkGameCommand
{
	public Resume()
	{
		super("resume", Messages.CMD_MAP_GAME_RESUME.getMessage());
		permission = ADMIN_PERMISSION;
	}

	@SuppressWarnings("deprecated")
	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if(Fk.getInstance().getGame().getState().equals(Game.GameState.BEFORE_STARTING))
			throw new FkLightException(Messages.CMD_ERROR_GAME_NOT_STARTED);
		if(Fk.getInstance().getGame().getState().equals(Game.GameState.STARTED))
			throw new FkLightException(Messages.CMD_ERROR_NOT_IN_PAUSE);

		Fk.getInstance().getGame().setState(Game.GameState.STARTED);

		if(!FkPI.getInstance().getRulesManager().getRule(Rule.ETERNAL_DAY))
		{
			for(World w : org.bukkit.Bukkit.getWorlds())
				w.setGameRuleValue("doDaylightCycle", "true");
		}
		Fk.getInstance().getDeepPauseManager().unfreezePlayers();
		Fk.getInstance().getDeepPauseManager().resetAIs();
		Fk.getInstance().getDeepPauseManager().unprotectItems();

		if(sender != null)
			super.broadcast(Messages.CMD_GAME_RESUME.getMessage(), FkSound.NOTE_PIANO);

	}
}
