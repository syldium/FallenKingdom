package fr.devsylone.fallenkingdom.commands.brigadier;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.devsylone.fallenkingdom.commands.abstraction.AbstractCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.FkParentCommand;
import fr.devsylone.fallenkingdom.manager.CommandManager;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

/**
 * Gère la liaison entre les commandes Bukkit et Brigadier.
 *
 * @see BrigadierSpigotManager Implémentation sous Spigot
 * @see fr.devsylone.fallenkingdom.commands.FkAsyncRegisteredCommandExecutor Implémentation sous Paper
 * @param <S>
 */
public class BrigadierManager<S>
{
    private static final Method GET_BUKKIT_SENDER_METHOD;

    static {
        try {
            Class<?> commandListenerWrapper = NMSUtils.nmsClass("commands", "CommandListenerWrapper");
            GET_BUKKIT_SENDER_METHOD = commandListenerWrapper.getDeclaredMethod("getBukkitSender");
            GET_BUKKIT_SENDER_METHOD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
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
        return register(commandManager, root.getLiteral(), suggestionProvider);
    }

    /**
     * Créé l'équivalent Brigadier du gestionnaire de commande donné
     *
     * @param commandManager Instance du gestionnaire de commandes du plugin
     * @param literal Nom de base de la commande
     * @param suggestionProvider Prestataire de suggestions
     * @return Nouveau nœud de commande
     */
    public LiteralCommandNode<S> register(CommandManager commandManager, String literal, SuggestionProvider<S> suggestionProvider) {
        LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder.literal(literal);
        builder.then(LiteralArgumentBuilder.literal("help"));

        for (AbstractCommand command : commandManager.getMainCommands()) {
            if (command.shouldDisplay()) {
                builder.then(buildCommandNode(command, suggestionProvider, commandManager.withPermissions()));
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
    CommandNode<S> buildCommandNode(AbstractCommand command, SuggestionProvider<S> suggestionProvider, boolean withPermissions) {
        LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder.<S>literal(command.getName())
                .requires(sender -> !withPermissions || command.hasPermission(getBukkitSender(sender)));
        if (command instanceof FkParentCommand) {
            builder.then(LiteralArgumentBuilder.literal("help"));
            for (AbstractCommand subCommand : ((FkParentCommand) command).getChildren()) {
                if (subCommand.shouldDisplay()) {
                    builder.then(buildCommandNode(subCommand, suggestionProvider, withPermissions));
                }
            }
            return builder.build();
        }

        FkCommand cmd = (FkCommand) command;
        CommandNode<S> node = builder.build();
        CommandNode<S> prevNode = node;
        for (int i = 0; i < cmd.getArguments().size(); i ++) {
            RequiredArgumentBuilder<S, ?> arg = ArgumentTypeBuilder.getFromArg(cmd.getArguments().get(i));
            if (cmd.getArguments().get(i).shouldBrigadierAskServer()) {
                arg.suggests(suggestionProvider);
            }
            if (i < cmd.getArguments().size()-1 && !cmd.getArguments().get(i+1).isRequired()) {
                arg.executes(s -> 0);
            }
            prevNode.addChild(prevNode = arg.build());
        }
        return node;
    }

    public static CommandSender getBukkitSender(Object commandWrapperListener) {
        try {
            return (CommandSender) GET_BUKKIT_SENDER_METHOD.invoke(commandWrapperListener);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
