package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.entity.Player;

public class DayDuration extends FkRuleCommand
{
    public DayDuration()
    {
        super("dayDuration", "<" + Messages.Unit.MINUTES.tl(20) + ">", 1, Messages.CMD_MAP_RULES_DAY_DURATION);
    }

    @Override
    public void execute(Player sender, FkPlayer fkp, String[] args)
    {
        int mins = assertPositiveNumber(args[0], false, Messages.CMD_ERROR_POSITIVE_INT);
        if(mins >= 1200)
            mins /= 1200;

        FkPI.getInstance().getRulesManager().setRule(Rule.DAY_DURATION, mins*1200);
        broadcast(Messages.CMD_RULES_DAY_DURATION.getMessage()
                .replace("%duration%", String.valueOf(mins))
                .replace("%unit%", Messages.Unit.MINUTES.tl(mins))
        );
        Fk.getInstance().getGame().updateDayDuration();
    }
}
