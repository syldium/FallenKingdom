package fr.devsylone.fallenkingdom.commands.abstraction;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class FkParentCommand extends AbstractCommand
{
	private final List<? extends AbstractCommand> children;

	public FkParentCommand(String name, List<? extends AbstractCommand> children, Messages description, CommandPermission permission) {
		super(name, description, permission);
		this.children = children;
		children.forEach(c -> c.setParent(this));
	}

	public FkParentCommand(String name, List<? extends AbstractCommand> children, Messages description) {
		this(name, children, description, CommandPermission.PLAYER);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
		// Autre commande inconnue
		if (args.size() > 0 && !args.get(0).equalsIgnoreCase("help")) {
			throw new FkLightException("Commande inconnue. " + ChatColor.YELLOW + "/" + label + " " + getFullUsage() + " help" + ChatColor.RED + " pour la liste des commandes.");
		}
		// Affiche la description
		children.stream()
				.filter(s -> s.hasPermission(sender))
				.forEach(s -> sender.sendMessage(ChatColor.GREEN + "/" + label + " " + s.getFullUsage() + " " + ChatColor.GRAY + s.getDescription()));
		return CommandResult.SUCCESS;
	}

	@Override
	public List<String> tabComplete(Fk plugin, CommandSender sender, List<String> args) {
		if (args.size() < 1) {
			return Collections.emptyList();
		}
		List<String> complete = children.stream()
				.filter(s -> s.getName().toLowerCase().startsWith(args.get(0).toLowerCase()))
				.filter(s -> s.hasPermission(sender))
				.map(AbstractCommand::getName)
				.collect(Collectors.toList());

		if ("help".startsWith(args.get(0).toLowerCase())) {
			complete.add("help");
		}
		return complete;
	}

	@Override
	public int getMinArgumentCount() {
		return 0;
	}


	@Override
	public AbstractCommand get(List<String> args) {
		if (args.size() == 0) {
			return this;
		}
		List<AbstractCommand> sub = children.stream()
				.filter(AbstractCommand::shouldDisplay)
				.filter(s -> s.getName().equalsIgnoreCase(args.get(0)))
				.collect(Collectors.toList());
		if (sub.size() == 1) {
			args.remove(0);
			return sub.get(0).get(args);
		}
		return this;
	}

	@Override
	public AbstractCommand get(Class<? extends AbstractCommand> cmd) {
		for (AbstractCommand command : children) {
			AbstractCommand c = command.get(cmd);
			if (c != null) {
				return c;
			}
		}
		return null;
	}

	public List<? extends AbstractCommand> getChildren() {
		return children;
	}
}
