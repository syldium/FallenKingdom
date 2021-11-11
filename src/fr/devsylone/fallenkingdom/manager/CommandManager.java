package fr.devsylone.fallenkingdom.manager;

import com.google.common.collect.ImmutableList;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.AbstractCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.chests.FkChestsCommand;
import fr.devsylone.fallenkingdom.commands.debug.Bug;
import fr.devsylone.fallenkingdom.commands.debug.Debug;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.commands.lang.FkLangCommand;
import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.commands.rules.rulescommands.RulesList;
import fr.devsylone.fallenkingdom.commands.scoreboard.FkScoreboardCommand;
import fr.devsylone.fallenkingdom.commands.teams.FkTeamCommand;
import fr.devsylone.fallenkingdom.exception.ArgumentParseException;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandManager
{
	private final List<? extends AbstractCommand> mainCommands;
	private final boolean permissions;

	public CommandManager(boolean permissions) {
		this.mainCommands = ImmutableList.<AbstractCommand>builder()
				.add(new Bug())
				.add(new FkChestsCommand())
				.add(new Debug())
				.add(new FkGameCommand())
				.add(new FkLangCommand())
				.add(new FkRuleCommand())
				.add(new FkScoreboardCommand())
				.add(new FkTeamCommand())
				.build();
		this.permissions = permissions;
	}

	public CommandResult executeCommand(Fk plugin, CommandSender sender, String label, List<String> arguments) {
		// Si aide principale
		if (!arguments.isEmpty() && arguments.get(0).equalsIgnoreCase("help")) {
			sendMainHelp(sender, label);
			return CommandResult.SUCCESS;
		}

		// Recherche de la commande principale utilisée
		String token = !arguments.isEmpty() ? arguments.get(0) : "";
		AbstractCommand main = mainCommands.stream()
				.filter(cmd -> cmd.getName().equals(token))
				.findFirst()
				.orElse(null);

		// Commande principale non trouvée
		if (main == null) {
			sendCommandUsage(sender, label);
			return CommandResult.INVALID_ARGS;
		}

		arguments.remove(0);
		main = main.get(arguments);

		// Vérifie que le joueur a les permissions suffisantes
		if (permissions && !main.hasPermission(sender))
			return CommandResult.NO_PERMISSION;

		// Vérifie que l'exécuteur est le bon
		if (!main.isValidExecutor(sender))
			return CommandResult.NOT_VALID_EXECUTOR;

		// Vérifie qu'il y a le bon nombre d'arguments
		if (main.getMinArgumentCount() > arguments.size()) {
			sender.sendMessage(ChatColor.RED.toString() + Messages.CMD_ERROR_UNKNOWN_ARG + label + " " + main.getFullUsage());
			return CommandResult.INVALID_ARGS;
		}

		// Essaye d'exécuter la commande
		CommandResult result;
		try {
			result = main.execute(plugin, sender, arguments, label);
		} catch (ArgumentParseException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			result = CommandResult.INVALID_ARGS;
		} catch (FkLightException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			if (sender instanceof Player)
				plugin.getLogger().info(Messages.CONSOLE_LIGHT_ERROR.getMessage() + " " + e.getMessage());
			result = CommandResult.STATE_ERROR;
		} catch (Throwable e) {
			e.printStackTrace();
			sender.sendMessage("§c" + Messages.CMD_ERROR);
			result = CommandResult.FAILURE;
		}

		// TODO En créer une interface ?
		if (main.getClass().getName().contains("rulescommands") && !(main instanceof RulesList) && plugin.getGame().getState().equals(Game.GameState.BEFORE_STARTING))
			plugin.getDisplayService().updateAll();
		plugin.getTipsManager().addUsed(main);

		return result;
	}

	public CommandResult executeCommand(Fk plugin, CommandSender sender, String literal) {
		return executeCommand(plugin, sender, "fk", new ArrayList<>(Arrays.asList(literal.split(" "))));
	}

	public List<String> tabCompleteCommand(Fk plugin, CommandSender sender, List<String> arguments) {
		if (arguments.size() == 1) {
			return mainCommands.stream()
					.filter(AbstractCommand::shouldDisplay)
					.filter(c -> c.getName().startsWith(arguments.get(0)))
					.filter(c -> !permissions || c.hasPermission(sender))
					.map(AbstractCommand::getName)
					.collect(Collectors.toList());
		}

		Optional<? extends AbstractCommand> main = mainCommands.stream()
				.filter(AbstractCommand::shouldDisplay)
				.filter(cmd -> cmd.getName().equals(arguments.get(0)))
				.findFirst();

		if (main.isPresent()) {
			arguments.remove(0);
			return main.get().get(arguments).tabComplete(plugin, sender, arguments);
		}

		return Collections.emptyList();
	}

	public Optional<? extends AbstractCommand> search(Class<? extends AbstractCommand> command) {
		for (AbstractCommand cmd : mainCommands) {
			AbstractCommand c = cmd.get(command);
			if (c != null) {
				return Optional.of(c);
			}
		}
		return Optional.empty();
	}

	private void sendCommandUsage(CommandSender sender, String label) {
		sender.sendMessage(ChatColor.RED + Messages.CMD_ERROR_UNKNOWN.getMessage().replace("%help%", "/" + label + " help"));
	}

	private void sendMainHelp(CommandSender sender, String label) {
		mainCommands.stream()
				.filter(c -> !permissions || c.hasPermission(sender))
				.filter(AbstractCommand::shouldDisplay)
				.forEach(c -> sender.sendMessage(ChatColor.GREEN + "/" + label + " " + c.getFullUsage() + " " + ChatColor.GRAY + c.getDescription()));
	}

	public List<? extends AbstractCommand> getMainCommands() {
		return mainCommands;
	}

	public boolean hasPermission(CommandSender sender, String permission) {
		return !permissions || sender.hasPermission(permission);
	}

	public boolean withPermissions() {
		return permissions;
	}
}
