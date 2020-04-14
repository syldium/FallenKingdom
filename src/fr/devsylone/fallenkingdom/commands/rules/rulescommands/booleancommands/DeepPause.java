package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class DeepPause extends FkBooleanRuleCommand
{
	public DeepPause()
	{
		super("deepPause", Messages.CMD_MAP_RULES_DEEP_PAUSE);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		setRuleValue(args[0]);

		Messages value = Boolean.parseBoolean(args[0]) ? Messages.CMD_RULES_DEEP_PAUSE_IN_DEPTH : Messages.CMD_RULES_DEEP_PAUSE_LIGHT;
		broadcast(Messages.CMD_RULES_DEEP_PAUSE.getMessage().replace("%state%", value.getMessage()));

		if(Fk.getInstance().getGame().getState().equals(Game.GameState.PAUSE)){
			if (Boolean.parseBoolean(args[0]))
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
