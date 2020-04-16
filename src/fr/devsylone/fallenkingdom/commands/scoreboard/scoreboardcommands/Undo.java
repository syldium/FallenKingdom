package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.scoreboard.FkScoreboardCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

public class Undo extends FkScoreboardCommand
{
    public Undo()
    {
        super("undo", "", 0, Messages.CMD_MAP_SCOREBOARD_UNDO);
    }

    public void execute(Player sender, FkPlayer fkp, String[] args)
    {
        if (!Fk.getInstance().getScoreboardManager().undo())
            throw new FkLightException(Messages.CMD_ERROR_SCOREBOARD_CANNOT_UNDO);
    }
}
