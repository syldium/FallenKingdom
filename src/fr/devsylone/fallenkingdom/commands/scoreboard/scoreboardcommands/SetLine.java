package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.scoreboard.FkScoreboardCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.players.FkPlayer.PlayerState;

public class SetLine extends FkScoreboardCommand
{
	public SetLine()
	{
		super("setLine", "<number> <text>", 2, Messages.CMD_MAP_SCOREBOARD_SET_LINE);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args) throws Exception
	{
		if(!args[0].matches("\\d+") || Integer.parseInt(args[0]) > 15 || Integer.parseInt(args[0]) < 1)
			throw new FkLightException(Messages.CMD_ERROR_SCOREBOARD_INVALID_LINE);

		if(fkp.getState() != PlayerState.EDITING_SCOREBOARD)
		{
			Fk.getInstance().getCommandManager().executeCommand(new String[] {"scoreboard", "edit"}, sender);
			if(!fkp.hasAlreadyLearntHowToEditTheBeautifulScoreboard())
				return;
		}

		String line = String.join(" ", args);
		line = line.substring(args[0].length() + 1);
		if(line.length() >= 45)
			throw new FkLightException("La ligne ne peut pas faire plus de 44 caractères");

		if(line.length() >= 32 && !Fk.getInstance().isNewVersion())
			fkp.sendMessage("§c[Attention] Votre ligne fait plus de 32 caractères, le plugin peut dysfonctionner. Il est possible que le scoreboard bug à un moment.\nS'il y a un bug, changez la ligne et redémarrez votre serveur.");

		Fk.getInstance().getScoreboardManager().setLine(Integer.parseInt(args[0]), line.replace("&", "§"));
	}
}
