package fr.devsylone.fallenkingdom.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.FkCommand;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class CommandManager
{
	private List<FkCommand> commandList;
	private HashMap<String, Boolean> confirms;

	public final static String NO_PERMISSION_MSG =  "Vous n'avez pas la permission d'exécuter cette commande.";
	private final boolean permissions = Fk.getInstance().getConfig().getBoolean("enable-permissions", false);

	public CommandManager()
	{
		commandList = new ArrayList<>();
		confirms = new HashMap<>();
		confirms.put("stop", false);
		confirms.put("reset", false);
		confirms.put("sbreset", false);
	}

	public boolean isConfirmed(String key)
	{
		return confirms.get(key);
	}

	public boolean setConfirmed(String key, boolean value)
	{
		return confirms.put(key, value);
	}

	public void registerCommands()
	{
		try
		{
			final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));

			if(jarFile.isFile())
			{
				JarFile jar;

				jar = new JarFile(jarFile);

				final Enumeration<JarEntry> entries = jar.entries();
				int blb = 0;
				while(entries.hasMoreElements())
				{
					final String name = entries.nextElement().getName();
					if(name.startsWith("fr/devsylone/fallenkingdom/commands/") && !name.contains("Fk") && !name.contains("$")  && name.endsWith(".class")) //On ne veut que les class (pas les pkg qui génèrent un CNFE)
						try
						{
							blb++;
							registerNewCommand((FkCommand) Class.forName(name.replaceAll("/", ".").replaceAll(".class", "")).newInstance());
						}catch(InstantiationException | IllegalAccessException | ClassNotFoundException e)
						{
							e.printStackTrace();
						}
				}
				if(blb < 10)
				{
					Fk.getInstance().getLogger().severe("Erreur ! Veuillez contacter le support");
					Fk.getInstance().addError("Erreur au chargement des commandes, merci de contacter le support via https://discord.gg/SmAAFxh ou bien devsylone@gmail.com");
				}

				jar.close();
			}

			else
			{
				Fk.getInstance().getLogger().severe("Merci de renommer le plugin en FallenKingdom.jar");
				Fk.getInstance().addError("Erreur au chargement des commandes, merci de contacter le support via https://discord.gg/SmAAFxh ou bien devsylone@gmail.com");
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private void registerNewCommand(FkCommand command)
	{
		commandList.add(command);
	}

	public void sendHelp(Player p)
	{
		List<FkCommand> list = new ArrayList<>();
		list.add(new FkCommand("team", "", 0, "Gestion des équipes")
		{
			@Override
			public void execute(Player sender, FkPlayer fkp, String[] args)
			{}
		});

		list.add(new FkCommand("rules", "", 0, "Gestion des règles")
		{
			@Override
			public void execute(Player sender, FkPlayer fkp, String[] args)
			{}
		});

		list.add(new FkCommand("game", "", 0, "Gestion de la partie")
		{
			@Override
			public void execute(Player sender, FkPlayer fkp, String[] args)
			{}
		});

		list.add(new FkCommand("scoreboard", "", 0, "Gestion du scoreboard")
		{
			@Override
			public void execute(Player sender, FkPlayer fkp, String[] args)
			{}
		});

		list.add(new FkCommand("chests", "", 0, "Gestion des coffres à crocheter")
		{
			@Override
			public void execute(Player sender, FkPlayer fkp, String[] args)
			{}
		});
		sendHelp(list, p);
	}

	public void sendHelp(String name, Player p)
	{
		List<FkCommand> list = new ArrayList<>();
		for(FkCommand command : commandList)
			if(command.getUsage().contains(name))
				list.add(command);

		sendHelp(list, p);
	}

	private void sendHelp(List<FkCommand> commands, Player p)
	{
		boolean alternate = false;

		int sepSize = 39;

		if(commands.get(0).getClass().getSuperclass().equals(FkGameCommand.class))
			sepSize = 21;

		if(commands.get(0).getClass().getSuperclass().equals(FkCommand.class))
			sepSize = 9;

		p.sendMessage(ChatColor.BLUE + "§m-------------------" + ChatColor.DARK_BLUE + " fk help " + ChatColor.BLUE + "§m-------------------");

		for(FkCommand cmd : commands)
		{
			String separation = "§7§m";
			for(int i = cmd.getUsage().length(); i < sepSize; i++)
				separation += "-";

			separation += "-§r";
			String currentColor = (alternate = !alternate) ? "§a" : "§2";
			p.sendMessage((cmd.equals(commands.get(0)) ? "" : "\n") + currentColor + cmd.getUsage() + "   " + separation + "  " + currentColor + cmd.getDescription().replaceAll("&r", currentColor));
		}
		p.sendMessage(ChatColor.BLUE + "§m-----------------------------------------------");
	}

	public void executeCommand(String[] args, Player sender) throws Exception
	{
		FkCommand command = null;
		String argsString = "";

		/*
		 * Converti le tableau en String.
		 */

		for(String s : args)
			argsString += s + " ";

		if(argsString.length() >= 1) // SI juste /fk
			argsString = argsString.substring(0, argsString.length() - 1);

		/*
		 * Check si /Fk help ou /Fk bug
		 * SINON
		 * Trouve la commande. Si le format {fk [SECTION] [COMMAND]} est respecte
		 */
		if(argsString.startsWith("help"))
			command = getCommand("help");

		else if(argsString.startsWith("bug"))
			command = getCommand("bug");

		else if(args.length > 1)
			for(FkCommand c : commandList)
			{
				if((args[0] + " " + args[1]).toLowerCase().equals(c.getPath().toLowerCase()))
					command = c;
			}
		/*
		 * Si on n'a pas trouvé une exception est levée. OU /fk debug
		 */
		if(command == null)
		{
			if(argsString.toLowerCase().startsWith("debug"))
				throw new FkLightException("debug_fake_error");

			if(argsString.toLowerCase().startsWith("team"))
				throw new FkLightException("Commande inconnue. §e/fk team help §cpour la liste des commandes relatives aux équipes.");

			else if(argsString.toLowerCase().startsWith("rules"))
				throw new FkLightException("Commande inconnue. §e/fk rules help §cpour la liste des commandes relatives aux règles.");

			else if(argsString.toLowerCase().startsWith("game"))
				throw new FkLightException("Commande inconnue. §e/fk game help §cpour la liste des commandes relatives au jeu.");

			else if(argsString.toLowerCase().startsWith("scoreboard"))
				throw new FkLightException("Commande inconnue. §e/fk scoreboard help §cpour la liste des commandes relatives au scoreboard.");

			else if(argsString.toLowerCase().startsWith("chests"))
				throw new FkLightException("Commande inconnue. §e/fk chests help §cpour la liste des commandes relatives aux coffres à crocheter.");

			else
				throw new FkLightException("Commande inconnue. §e/fk help §cpour la liste des commandes du plugin.");
		}

		if(!hasPermission(sender, command.getPermission()))
			throw new FkLightException(NO_PERMISSION_MSG);

		/*
		 * Trouve les arguments de la commande à effectuer.
		 */

		int commandDepth = command.getPath().split(" ").length;
		String sortedArgs = "";
		for(int i = commandDepth; i < args.length; i++)
			sortedArgs += args[i] + " ";

		if(!sortedArgs.isEmpty())
			sortedArgs = sortedArgs.substring(0, sortedArgs.length() - 1);

		/*
		 * Si la commande necessite un args
		 * ET
		 * Si il n'y a pas d'args
		 * ou
		 * Si le nombre d'argument est inférieur au nombre d'arguments qu'il faut.
		 */

		if(command.getNbrArgs() > 0 && (sortedArgs.isEmpty() || sortedArgs.split(" ").length < command.getNbrArgs()))
			throw new FkLightException(command.getUsage());

		/*
		 * Si la commande n'est pas reset/stop, cancel les confirmations.
		 */
		if(!command.getPath().equalsIgnoreCase("game reset"))
			setConfirmed("reset", false);

		if(!command.getPath().equalsIgnoreCase("game stop"))
			setConfirmed("stop", false);
		
		if(!command.getPath().equalsIgnoreCase("scoreboard reset"))
			setConfirmed("sbreset", false);

		try
		{
			command.execute(sender, Fk.getInstance().getPlayerManager().getPlayer(sender), sortedArgs.isEmpty() ? new String[0] : sortedArgs.split(" "));
		}catch(FkLightException ex)
		{
			throw ex;
		}

		Fk.getInstance().getTipsManager().addUsed(command);
		/*
		 * Refresh tous les sb au cas ou
		 */

		Fk.getInstance().getScoreboardManager().refreshAllScoreboards();
	}

	public FkCommand getCommand(String path)
	{
		for(FkCommand cmd : commandList)
			if(cmd.getPath().equalsIgnoreCase(path))
				return cmd;

		return null;
	}

	public List<FkCommand> getCommandList()
	{
		return (List<FkCommand>) ImmutableList.copyOf(commandList);
	}

	public boolean hasPermission(Player sender, String permission)
	{
		return !permissions || (permissions && sender.hasPermission(permission));
	}
}
