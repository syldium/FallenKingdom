package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;

public class DeepPause extends FkBooleanRuleCommand
{
	public DeepPause()
	{
		super("deepPause", Messages.CMD_MAP_RULES_DEEP_PAUSE, Rule.DO_PAUSE_AFTER_DAY);
	}

	@Override
	protected void sendMessage(boolean newValue) {
		Messages value = newValue ? Messages.CMD_RULES_DEEP_PAUSE_IN_DEPTH : Messages.CMD_RULES_DEEP_PAUSE_LIGHT;
		broadcast(Messages.CMD_RULES_DEEP_PAUSE.getMessage().replace("%state%", value.getMessage()));

		if (newValue)
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
