package fr.devsylone.fallenkingdom.commands.brigadier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.devsylone.fallenkingdom.commands.abstraction.AbstractCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.Argument;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.FkParentCommand;
import fr.devsylone.fallenkingdom.manager.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.Function;

/**
 * Gère la liaison entre les commandes Bukkit et Brigadier.
 *
 * @see BrigadierSpigotManager Implémentation sous Spigot
 * @see fr.devsylone.fallenkingdom.commands.FkAsyncRegisteredCommandExecutor Implémentation sous Paper
 * @param <S>
 */
public class BrigadierManager<S>
{
    private final Function<S, CommandSender> bukkitSender;

    public BrigadierManager(Function<S, CommandSender> bukkitSender) {
        this.bukkitSender = bukkitSender;
    }

    /**
     * Créé à partir d'une commande existante son équivalent à appliquer pour Brigadier.
     *
     * @param commandManager Instance du gestionnaire de commandes du plugin
     * @param root Nœud de commande à mettre à jour
     * @param suggestionProvider Prestataire de suggestions
     * @return Nouveau nœud de commande
     */
    public LiteralCommandNode<S> register(CommandManager commandManager, LiteralCommandNode<S> root, SuggestionProvider<S> suggestionProvider) {
        return register(commandManager, root.getLiteral(), root.getCommand(), suggestionProvider);
    }

    /**
     * Créé l'équivalent Brigadier du gestionnaire de commande donné
     *
     * @param commandManager Instance du gestionnaire de commandes du plugin
     * @param literal Nom de base de la commande
     * @param suggestionProvider Prestataire de suggestions
     * @return Nouveau nœud de commande
     */
    public LiteralCommandNode<S> register(CommandManager commandManager, String literal, Command<S> executor, SuggestionProvider<S> suggestionProvider) {
        LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder.<S>literal(literal).executes(executor);
        builder.then(LiteralArgumentBuilder.<S>literal("help").executes(executor));

        for (AbstractCommand command : commandManager.getMainCommands()) {
            if (command.shouldDisplay()) {
                builder.then(buildCommandNode(command, executor, suggestionProvider, commandManager.withPermissions()));
            }
        }
        return builder.build();
    }

    /**
     * Construit une sous commande du plugin (peut être appelé récursivement)
     * @param command Commande du Fk
     * @param suggestionProvider Prestataire de suggestions
     * @return Commande Brigadier
     */
    CommandNode<S> buildCommandNode(AbstractCommand command, Command<S> executor, SuggestionProvider<S> suggestionProvider, boolean withPermissions) {
        LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder.<S>literal(command.getName())
                .requires(sender -> !withPermissions || command.hasPermission(this.bukkitSender.apply(sender)));
        if (command instanceof FkParentCommand) {
            builder.executes(executor);
            builder.then(LiteralArgumentBuilder.<S>literal("help").executes(executor));
            for (AbstractCommand subCommand : ((FkParentCommand) command).getChildren()) {
                if (subCommand.shouldDisplay()) {
                    builder.then(buildCommandNode(subCommand, executor, suggestionProvider, withPermissions));
                }
            }
            return builder.build();
        }

        FkCommand cmd = (FkCommand) command;
        List<Argument<?>> arguments = cmd.getArguments();
        if (arguments.isEmpty() || !arguments.get(0).isRequired()) {
            builder.executes(executor);
        }

        CommandNode<S> node = builder.build();
        CommandNode<S> prevNode = node;
        for (int i = 0; i < arguments.size(); i ++) {
            RequiredArgumentBuilder<S, ?> arg = ArgumentTypeBuilder.getFromArg(arguments.get(i));
            if (arguments.get(i).shouldBrigadierAskServer()) {
                arg.suggests(suggestionProvider);
            }
            if (i == arguments.size() - 1 || !arguments.get(i + 1).isRequired()) {
                arg.executes(executor);
            }
            prevNode.addChild(prevNode = arg.build());
        }
        return node;
    }
}
