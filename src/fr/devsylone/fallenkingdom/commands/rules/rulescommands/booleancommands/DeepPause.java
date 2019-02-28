package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class DeepPause extends FkBooleanRuleCommand
{
	public DeepPause()
	{
		super("deepPause", "Permet d'activer/désativer la pause approfondie");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args) throws ReflectiveOperationException
	{
		setRuleValue(args[0]);

		broadcast("La pause est désormais", Boolean.valueOf(args[0]).booleanValue() ? "approfondie" : "légère", " ! ");

		if(Fk.getInstance().getGame().getState().equals(Game.GameState.PAUSE)){
			if (Boolean.valueOf(args[0]).booleanValue())
			{
				Fk.getInstance().getDeepPauseManager().removeAIs();
				Fk.getInstance().getDeepPauseManager().protectDespawnItems();
			}
			else
			{
				Fk.getInstance().getDeepPauseManager().resetAIs();
				Fk.getInstance().getDeepPauseManager().unprotectItems();
			}
		}
	}
}
