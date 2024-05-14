package fr.devsylone.fallenkingdom.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.brigadier.BrigadierManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("UnstableApiUsage")
public class FkLifecycleRegisteredExecutor extends FkAsyncCommandExecutor {

    private final BrigadierManager<CommandSourceStack> builder = new BrigadierManager<>(CommandSourceStack::getSender);
    private SuggestionProvider<CommandSourceStack> suggestionProvider;

    public FkLifecycleRegisteredExecutor(Fk plugin, PluginCommand command) {
        super(plugin, command);
        final LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            CommandNode<CommandSourceStack> old = commands.getDispatcher().getRoot().getChild(command.getName());
            if (old != null) {
                // Se déplace sur un nœud peut être non littéral pour pouvoir récupérer un SuggestionsProvider.
                old = old.getChildren().iterator().next();
            }
            if (old instanceof ArgumentCommandNode) {
                // Récupère le BukkitBrigSuggestionProvider construit pour la commande brute par Paper.
                // S'il s'agit de la deuxième fois que la commande est enregistrée (pour un rechargement des datapacks par exemple),
                // le provider n'existe plus sur "args" vu que le plugin a déjà reconstruit la commande. À la place, le
                // provider précédent est réutilisé.
                this.suggestionProvider = ((ArgumentCommandNode<CommandSourceStack, String>) old).getCustomSuggestions();
            }
            final LiteralCommandNode<CommandSourceStack> literal = builder.register(this, command.getName(), new RawExecutor(command), this.suggestionProvider);
            commands.register(literal, command.getAliases());
        });
    }

    private static class RawExecutor implements Command<CommandSourceStack> {

        private final org.bukkit.command.Command inner;

        private RawExecutor(org.bukkit.command.Command inner) {
            this.inner = inner;
        }

        @Override
        public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            final CommandSender sender = context.getSource().getSender();
            final String content = context.getRange().get(context.getInput());
            final String[] args = org.apache.commons.lang3.StringUtils.split(content, ' ');
            return this.inner.execute(sender, this.inner.getName(), args) ? 1 : 0;
        }
    }
}
