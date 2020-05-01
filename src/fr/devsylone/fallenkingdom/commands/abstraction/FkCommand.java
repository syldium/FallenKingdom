package fr.devsylone.fallenkingdom.commands.abstraction;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class FkCommand extends AbstractCommand
{
    protected final String usage;
    protected final int minArgumentCount;
    protected final List<Argument<?>> arguments;

    public FkCommand(String name, String usage, Messages description, CommandPermission permission) {
        super(name, description, permission);
        this.arguments = Argument.create(usage);
        this.usage = arguments.stream().map(Argument::getForUsage).collect(Collectors.joining(" "));
        this.minArgumentCount = (int) arguments.stream().filter(Argument::isRequired).count();
    }

    public FkCommand(String name, List<Argument<?>> arguments, Messages description, CommandPermission permission) {
        this(name, arguments.stream().map(Argument::getForUsage).collect(Collectors.joining(" ")), (int) arguments.stream().filter(Argument::isRequired).count(), arguments, description, permission);
    }

    public FkCommand(String name, Messages description, CommandPermission permission) {
        this(name, "", 0, Collections.emptyList(), description, permission);
    }

    public FkCommand(String name, String usage, int minArgumentCount, List<Argument<?>> arguments, Messages description, CommandPermission permission) {
        super(name, description, permission);
        this.usage = usage;
        this.minArgumentCount = minArgumentCount;
        this.arguments = arguments;
    }

    @Override
    public String getUsage() {
        return getName() + " " + usage;
    }

    @Override
    public int getMinArgumentCount() {
        return minArgumentCount;
    }

    public List<Argument<?>> getArguments() {
        return arguments;
    }

    @Override
    public FkCommand get(List<String> args) {
        return this;
    }

    @Override
    public AbstractCommand get(Class<? extends AbstractCommand> cmd) {
        if (this.getClass().equals(cmd)) {
            return this;
        }
        return null;
    }

    @Override
    public List<String> tabComplete(Fk plugin, CommandSender sender, List<java.lang.String> args) {
        if (args.size() < 1 || !hasPermission(sender)) {
            return Collections.emptyList();
        }
        if (args.size() <= arguments.size()) {
            return arguments.get(args.size() - 1).provideTabComplete(plugin, args.get(args.size() - 1));
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(permission.get());
    }
}
