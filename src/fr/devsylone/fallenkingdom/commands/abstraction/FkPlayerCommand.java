package fr.devsylone.fallenkingdom.commands.abstraction;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class FkPlayerCommand extends FkCommand {

    public FkPlayerCommand(String name, String usage, Messages description, CommandPermission permission) {
        super(name, usage, description, permission);
    }

    public FkPlayerCommand(String name, List<Argument<?>> arguments, Messages description, CommandPermission permission) {
        super(name, arguments, description, permission);
    }

    public FkPlayerCommand(String name, Messages description, CommandPermission permission) {
        super(name, description, permission);
    }

    public FkPlayerCommand(String name, String usage, int argumentListMinSize, List<Argument<?>> arguments, Messages description, CommandPermission permission) {
        super(name, usage, argumentListMinSize, arguments, description, permission);
    }

    public abstract CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label);

    @Override
    public final CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
        return execute(plugin, (Player) sender, plugin.getPlayerManager().getPlayer(((Player) sender)),  args, label);
    }

    @Override
    public boolean isValidExecutor(CommandSender sender) {
        return sender instanceof Player;
    }
}
