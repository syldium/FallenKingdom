package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.scoreboard.FkScoreboardCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

public class RemoveLine extends FkScoreboardCommand
{
    public RemoveLine()
    {
        super("removeLine", "<number>", 1, Messages.CMD_MAP_SCOREBOARD_REMOVE_LINE);
    }

    public void execute(Player sender, FkPlayer fkp, String[] args)
    {
        if(!Fk.getInstance().getScoreboardManager().removeLine(Integer.parseInt(args[0])))
            throw new FkLightException(Messages.CMD_ERROR_SCOREBOARD_INVALID_LINE);
    }
}
