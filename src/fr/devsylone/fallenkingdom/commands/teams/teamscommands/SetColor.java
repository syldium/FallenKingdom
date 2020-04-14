package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.teams.FkTeamCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fkpi.util.Color;

public class SetColor extends FkTeamCommand
{
	public SetColor()
	{
		super("setColor", "<team> <color>", 2, Messages.CMD_MAP_TEAM_SET_COLOR);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Team team;
		if((team = Fk.getInstance().getFkPI().getTeamManager().getTeam(args[0])) == null)
			throw new FkLightException("Cette équipe n'existe pas");
		team.setColor(Color.forName(args[1]));
		broadcast("L'équipe " + team.toString() + " §6est maintenant de couleur " + team.getColor().getChatColor() + team.getColor().getGenredName(Color.GENRE_F));
		Fk.getInstance().getScoreboardManager().refreshNicks();
	}
}
