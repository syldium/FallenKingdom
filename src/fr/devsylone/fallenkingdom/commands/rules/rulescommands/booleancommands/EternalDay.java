package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import org.bukkit.Bukkit;
import org.bukkit.World;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;

import static fr.devsylone.fallenkingdom.version.Environment.setAdvanceTime;

public class EternalDay extends FkBooleanRuleCommand
{
	public EternalDay()
	{
		super("eternalDay", Messages.CMD_MAP_RULES_ETERNAL_DAY, Rule.ETERNAL_DAY);
	}

	@Override
	protected void sendMessage(boolean newValue) {
		for(World w : Bukkit.getWorlds())
		{
			if(Fk.getInstance().getWorldManager().isAffected(w))
			{
				setAdvanceTime(w, !newValue);
				w.setTime(Fk.getInstance().getGame().getExceptedWorldTime());
			}
		}
		broadcastOnOff(!newValue, Messages.CMD_RULES_ETERNAL_DAY);
	}
}
