package fr.devsylone.fallenkingdom.commands.rules;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import org.bukkit.entity.Player;

public class DayDuration extends FkRuleCommand
{
    public DayDuration()
    {
        super("dayDuration", "<mins>", 1, "Modifie la durée en minutes d'un jour.");
    }

    @Override
    public void execute(Player sender, FkPlayer fkp, String[] args)
    {
        if(args.length == 0)
            throw new FkLightException(usage);

        try
        {
            Integer.parseInt(args[0]);
        }
        catch(NumberFormatException e)
        {
            throw new FkLightException(args[0] + " n'est pas un nombre valide ! ");
        }
        int mins = Integer.parseInt(args[0]);

        if(mins <= 0)
        {
            throw new FkLightException("La durée d'un jour doit être strictement positive.");
        }

        Fk.getInstance().getFkPI().getRulesManager().getRuleByName("DayDuration").setValue(mins*1200);
        broadcast("Un jour dure maintenant", args[0], "minute" + (mins > 1 ? "s" : "") + ".");
        Fk.getInstance().getGame().updateDayDuration();
    }
}
