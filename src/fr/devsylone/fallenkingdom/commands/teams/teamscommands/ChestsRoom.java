package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.teams.FkTeamCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.manager.CommandManager;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.teams.Team;

public class ChestsRoom extends FkTeamCommand
{
	public ChestsRoom()
	{
		super("chestsRoom", "<help|enabled|captureTime|offset|show>", 0, Messages.CMD_MAP_TEAM_CHEST_ROOM);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if(args.length == 0)
			args = new String[] {"help"};

		if(!args[0].equalsIgnoreCase("show") && !Fk.getInstance().getCommandManager().hasPermission(sender, ADMIN_PERMISSION))
			throw new FkLightException(CommandManager.NO_PERMISSION_MSG);
		
		if(args[0].equalsIgnoreCase("show"))
		{
			if(!Fk.getInstance().getFkPI().getChestsRoomsManager().isEnabled())
				throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_DISABLED);

			Team team = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(sender.getName());
			if(team == null || team.getBase() == null || team.getBase().getChestsRoom() == null || !team.getBase().getChestsRoom().exists())
				throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_NONE);

			if(args.length <= 1 || !args[1].matches("\\d+") || Integer.parseInt(args[1]) < 1 || Integer.parseInt(args[1]) > 30)
				throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_INVALID_TIME);

			team.getBase().getChestsRoom().show(sender, Integer.parseInt(args[1]));
			fkp.sendMessage(Messages.CMD_TEAM_CHEST_ROOM_SHOW);
		}

		else if(args[0].equalsIgnoreCase("enabled"))
		{
			if(Fk.getInstance().getGame().getState() != GameState.BEFORE_STARTING)
				throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_STARTED);

			if(args.length <= 1 || !args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false"))
				throw new FkLightException(Messages.CMD_ERROR_BOOL_FORMAT);

			Fk.getInstance().getFkPI().getChestsRoomsManager().setEnabled(Boolean.parseBoolean(args[1]));
			if(Boolean.parseBoolean(args[1]))
				broadcast(Messages.CMD_TEAM_CHEST_ROOM_ENABLED.getMessage());
			else
				broadcast(Messages.CMD_TEAM_CHEST_ROOM_DISABLED.getMessage());
		}

		else if(args[0].equalsIgnoreCase("CaptureTime"))
		{
			if(Fk.getInstance().getGame().getState() != GameState.BEFORE_STARTING)
				throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_STARTED);

			if(args.length <= 1 || !args[1].matches("\\d+") || Integer.parseInt(args[1]) < 1)
				throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_CAPTURE_TIME_FORMAT);

			Fk.getInstance().getFkPI().getChestsRoomsManager().setCaptureTime(Integer.parseInt(args[1]));
			broadcast(Messages.CMD_TEAM_CHEST_ROOM_CAPTURE_TIME.getMessage()
					.replace("%time%", args[1])
					.replace("%unit%", Messages.Unit.SECONDS.tl(Integer.parseInt(args[1])))
			);
		}

		else if(args[0].equalsIgnoreCase("Offset"))
		{
			if(Fk.getInstance().getGame().getState() != GameState.BEFORE_STARTING)
				throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_STARTED);

			if(args.length <= 1 || !args[1].matches("\\d+") || Integer.parseInt(args[1]) < 1 || Integer.parseInt(args[1]) > 10)
				throw new FkLightException(Messages.CMD_ERROR_CHEST_ROOM_INVALID_OFFSET);

			Fk.getInstance().getFkPI().getChestsRoomsManager().setOffset(Integer.parseInt(args[1]));
			broadcast(Messages.CMD_TEAM_CHEST_ROOM_OFFSET.getMessage()
					.replace("%offset%", args[1])
					.replace("%unit%", Messages.Unit.BLOCKS.tl(Integer.parseInt(args[1])))
			);
		}

		else
		{
			Fk.getInstance().getPacketManager().openBook(sender, "{pages:[\"[\\\"\\\",{\\\"text\\\":\\\" \\\"},{\\\"text\\\":\\\"Reconnaissance auto\\\\n     de la salle des\\\\n        coffres\\\",\\\"color\\\":\\\"dark_blue\\\"},{\\\"text\\\":\\\"\\\\n\\\\n \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"[Activer]\\\",\\\"color\\\":\\\"dark_green\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/fk team ChestsRoom enabled true\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour activer\\\"}},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"[Désactiver]\\\",\\\"color\\\":\\\"dark_red\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/fk team ChestsRoom enabled false\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour désactiver\\\"}},{\\\"text\\\":\\\"\\\\n\\\\n   \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"> En savoir plus <\\\",\\\"underlined\\\":true,\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":5},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour plus d'infos\\\"}},{\\\"text\\\":\\\"   \\\",\\\"color\\\":\\\"reset\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"   \\\"},{\\\"text\\\":\\\"Temps de capture  \\\\n      de la salle\\\\n     des coffres\\\",\\\"color\\\":\\\"dark_blue\\\"},{\\\"text\\\":\\\"\\\\n\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"> 2 minutes\\\",\\\"color\\\":\\\"green\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/fk team ChestsRoom CaptureTime 120\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer le temps\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer le temps\\\"}},{\\\"text\\\":\\\"> 1 minute\\\",\\\"color\\\":\\\"green\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/fk team ChestsRoom CaptureTime 60\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer le temps\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer le temps\\\"}},{\\\"text\\\":\\\"> 50 secondes\\\",\\\"color\\\":\\\"green\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/fk team ChestsRoom CaptureTime 50\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer le temps\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer le temps\\\"}},{\\\"text\\\":\\\"> 40 secondes\\\",\\\"color\\\":\\\"green\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/fk team ChestsRoom CaptureTime 40\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer le temps\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer le temps\\\"}},{\\\"text\\\":\\\"> 30 secondes\\\",\\\"color\\\":\\\"green\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/fk team ChestsRoom CaptureTime 30\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer le temps\\\"}},{\\\"text\\\":\\\"\\\\n\\\\n/fk team ChestsRoom CaptureTime \\\",\\\"color\\\":\\\"reset\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"   \\\"},{\\\"text\\\":\\\" Marge de la salle          des coffres \\\",\\\"color\\\":\\\"dark_blue\\\"},{\\\"text\\\":\\\"    \\\\n\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"> 1 bloc\\\",\\\"color\\\":\\\"green\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/fk team ChestsRoom offset 1\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer\\\"}},{\\\"text\\\":\\\"> 2 blocs (Conseillé)\\\",\\\"color\\\":\\\"green\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/fk team ChestsRoom offset 2\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer\\\"}},{\\\"text\\\":\\\"> 3 blocs\\\",\\\"color\\\":\\\"green\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/fk team ChestsRoom offset 3\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer\\\"}},{\\\"text\\\":\\\"> 4 blocs\\\",\\\"color\\\":\\\"green\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/fk team ChestsRoom offset 4\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer\\\"}},{\\\"text\\\":\\\"> 5 blocs\\\",\\\"color\\\":\\\"green\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/fk team ChestsRoom offset 5\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour changer\\\"}},{\\\"text\\\":\\\"\\\\n\\\\n\\\\n   \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"> En savoir plus <\\\",\\\"underlined\\\":true,\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":7},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique pour plus d'infos\\\"}},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":7}}]\",\"{\\\"text\\\":\\\"      Explications\\\\n   pages suivantes \\\"}\",\"{\\\"text\\\":\\\"\\\\u21aa La reconnaissance automatique des salles des coffres permet d'automatiser l'élimination d'une équipe lorsque le plugin détecte qu'une équipe est réstée le temps choisi dans la salle des coffres.\\\\n\\\\n\\\\u21aa Le plugin calcule les dimensions de la salle --->>\\\"}\",\"[\\\"\\\",{\\\"text\\\":\\\"en fonction des coffres posés par les membres de l'équipe.\\\\n\\\\u21aa Chaque joueur peut visualiser à tout moment de la partie sa salle des coffres grâce à la commande \\\\n\\\"},{\\\"text\\\":\\\"/fk team ChestsRoom Show \\\",\\\"color\\\":\\\"dark_purple\\\"},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Vous êtes le seul à voir votre salle des coffres.\\\",\\\"color\\\":\\\"red\\\"}]\",\"{\\\"text\\\":\\\"\\\\u21aa À chaque fois qu'un joueur pose un coffre dans sa base, la salle des coffres evolue pour former une zone rectangulaire englobant tous les coffres posés (Seulement en survival). \\\\n\\\\n ---->>>> \\\"}\",\"[\\\"\\\",{\\\"text\\\":\\\"\\\\u21aa La \\\"},{\\\"text\\\":\\\"marge\\\",\\\"color\\\":\\\"aqua\\\"},{\\\"text\\\":\\\", ou \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Offset\\\",\\\"color\\\":\\\"aqua\\\"},{\\\"text\\\":\\\", est le nombre de blocs qui se trouvent entre le\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"s\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" coffre\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"s\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" le\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"s \\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\"plus proche\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"s\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" de la limite de la salle et la limite de la salle elle-même.\\\\n\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Exemple page suivante\\\",\\\"color\\\":\\\"gold\\\"},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\" -->\\\",\\\"color\\\":\\\"gold\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"Exemple\\\",\\\"bold\\\":true,\\\"underlined\\\":true},{\\\"text\\\":\\\" : Avec une marge de 2 blocs, lorsque vous allez poser un seul coffre, les dimensions de la salle seront de 5x5x5.\\\\n\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Screens\\\",\\\"bold\\\":true,\\\"underlined\\\":true},{\\\"text\\\":\\\"\\\\n\\\\n     \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"> En Vanilla <\\\",\\\"underlined\\\":true,\\\"color\\\":\\\"light_purple\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://imgur.com/kRpAMkA\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique !\\\"}},{\\\"text\\\":\\\"\\\\n\\\\n   \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"> Marge 2 blocs <\\\",\\\"underlined\\\":true,\\\"color\\\":\\\"dark_green\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://imgur.com/q1A1BfO\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique !\\\"}},{\\\"text\\\":\\\"\\\\n\\\\n   \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"> Marge 4 blocs < \\\",\\\"underlined\\\":true,\\\"color\\\":\\\"dark_green\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://imgur.com/8flOtrC\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":\\\"Clique !\\\"}}]\"],title:\"§aChestsRoom - Help\",author:\"§1Devsylone\"}");
		}
	}
}
