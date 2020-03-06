package fr.devsylone.fallenkingdom.commands.game.gamescommands;

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
		super("resume", "Reprend la partie après une pause.");
		permission = ADMIN_PERMISSION;
	}

	@SuppressWarnings("deprecated")
	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if(Fk.getInstance().getGame().getState().equals(Game.GameState.BEFORE_STARTING))
			throw new FkLightException("La partie n'a pas encore commencé.");
		if(Fk.getInstance().getGame().getState().equals(Game.GameState.STARTED))
			throw new FkLightException("La partie n'est pas en pause.");

		Fk.getInstance().getGame().setState(Game.GameState.STARTED);

		if(!(Boolean) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("EternalDay").getValue())
		{
			for(World w : org.bukkit.Bukkit.getWorlds())
				w.setGameRuleValue("doDaylightCycle", "true");
		}
		Fk.getInstance().getDeepPauseManager().unfreezePlayers();
		Fk.getInstance().getDeepPauseManager().resetAIs();
		Fk.getInstance().getDeepPauseManager().unprotectItems();

		if(sender != null)
			super.broadcast("La partie", "reprend", ".", FkSound.NOTE_PIANO);

	}
}
