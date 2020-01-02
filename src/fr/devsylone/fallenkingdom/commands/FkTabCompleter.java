package fr.devsylone.fallenkingdom.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import fr.devsylone.fkpi.util.Color;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.teams.Team;

public class FkTabCompleter implements TabCompleter
{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String aliases, String[] args)
	{
		ArrayList<String> list = new ArrayList<String>();
		String fullArgs = new String();
		for(String s : args)
			fullArgs += " " + s;

		fullArgs = fullArgs.substring(1);//Enleve le TOUT PREMIER ESPACE après le /fk

		for(FkCommand c : Fk.getInstance().getCommandManager().getCommandList())
		{
			for(String s : add(fullArgs, c))
				list.add(s);
		}

		List<String> deduped = new ArrayList<String>(new HashSet<String>(list));
		return deduped;
	}

	private ArrayList<String> add(String args, FkCommand command)
	{
		ArrayList<String> returned = new ArrayList<String>();

		String baseCommand = command.getPath();

		if(startsWith(args, baseCommand + " "))
		{//Il manque peut-etre des parametres à la commande ! et le mec a ajouté un espace derrière
			String[] parameters = new String[StringUtils.countMatches(command.getUsage(), "<")];//Les paramètres QU'IL FAUT renseigner
			String[] optionalParameters = new String[StringUtils.countMatches(command.getUsage(), "[")];
			String[] tableauChevron = command.getUsage().split(">");
			String[] tableauCrochet = command.getUsage().split("]");

			for(int i = 0; i < parameters.length; i++)
				parameters[i] = tableauChevron[i].split("<")[1];
			for(int i = 0; i < optionalParameters.length; i++)
				optionalParameters[i] = tableauCrochet[i].split("\\[")[1];

			if(parameters.length == 0 && optionalParameters.length == 0)//il n'y a aucun paramètre à renseigner.
				return returned;

			String parametersPart = args.substring(baseCommand.length() + 1); //Enlever "/fk rules FriendlyFire " = "tr" || "" || "blablabla"...  

			if(parametersPart.isEmpty() && parameters.length > 0)
			{//Il n'y a aucun argument de donné pour l'instant
				for(String s : getListOfPossibleValueForArg(parameters[0], ""))//On regarde toutes les valeurs possibles pour le premier paramètre
					returned.add(s);

				return returned;
			}

			String[] parametersEntered;

			String[] theSplit = parametersPart.split(" ");
			if(parametersPart.endsWith(" "))
			{
				parametersEntered = new String[theSplit.length + 1];
				for(int i = 0; i < theSplit.length; i++)
					parametersEntered[i] = theSplit[i];
				parametersEntered[parametersEntered.length - 1] = "";//il me faut ABSOLUMENT un "" pour le dernier.
			}
			else
				parametersEntered = theSplit;

			String map;
			if(parametersEntered.length > parameters.length+optionalParameters.length)
				return returned;
			else if(parametersEntered.length > parameters.length)
				map = optionalParameters[parametersEntered.length - parameters.length - 1];
			else
				map = parameters[parametersEntered.length - 1];

			String lastParameter = parametersEntered[parametersEntered.length - 1];//"/fk team AddPlayer fabulacraft rou" -> "rou" <- else || "AddPlayer fabulacraft " -> "" <- if

			for(String s : getListOfPossibleValueForArg(map, lastParameter))
				returned.add(s);

		}
		else if(startsWith(baseCommand, args) && !baseCommand.equalsIgnoreCase(args))
		{//La commande n'est pas fini ex : "/fk team AddPl"
			String[] arrayArg = baseCommand.split(" ");//Les args du path QU'IL FAUT
			String[] arrayArgsEntered = args.split(" ");//Les args du path qui ont ETE RENTRES
			if(args.endsWith(" "))
				returned.add(arrayArg[arrayArgsEntered.length]);
			else
				returned.add(arrayArg[arrayArgsEntered.length - 1]);
		}

		return returned;
	}

	private ArrayList<String> getListOfPossibleValueForArg(String arg, String startOfArg)
	{
		ArrayList<String> possibleValues = new ArrayList<>();

		if(arg.equalsIgnoreCase("team"))
		{
			//			possibleValues.add("");
			for(Team t : Fk.getInstance().getFkPI().getTeamManager().getTeams())
				if(startsWith(t.getName(), startOfArg))
					possibleValues.add(/*t.getChatColor()+*/t.getName()/*+"§r"*/);

		}
		else if(arg.equalsIgnoreCase("player"))
		{
			for(Player p : Fk.getInstance().getServer().getOnlinePlayers())
				if(startsWith(p.getName(), startOfArg))
					possibleValues.add(p.getName());

		}
		else if(arg.equalsIgnoreCase("block"))
		{
			for(Material m : Material.values())
				if(m.isBlock() && m.name().startsWith(startOfArg.toUpperCase()))
					possibleValues.add(m.name().toLowerCase());
		}
		else if(arg.equalsIgnoreCase("color"))
		{
			for(Color c : Color.values())
				if(startsWith(c.getGenredName(1), startOfArg))
					possibleValues.add(c.getGenredName(1));
		}
		else if(arg.contains("|"))
		{
			for(String s : arg.split("\\|"))
				if(startsWith(s, startOfArg))
					possibleValues.add(s);
		}
		else if(arg.equalsIgnoreCase("limit") || arg.equalsIgnoreCase("name") || arg.equalsIgnoreCase("day") || arg.equalsIgnoreCase("radius"))
		{

		}

		return possibleValues;

	}

	private boolean startsWith(String word, String prefix)
	{
		return word.toLowerCase().startsWith(prefix.toLowerCase());
	}
}
