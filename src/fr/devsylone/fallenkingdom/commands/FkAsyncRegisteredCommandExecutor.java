package fr.devsylone.fallenkingdom.commands;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.brigadier.BrigadierManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;

/**
 * Paper (avec les tout derniers patchs) seulement.
 * Remplace le nœud créé par Bukkit.
 */
public class FkAsyncRegisteredCommandExecutor extends FkAsyncCommandExecutor {

    private final BrigadierManager<BukkitBrigadierCommandSource> builder = new BrigadierManager<>();

    public FkAsyncRegisteredCommandExecutor(Fk plugin, PluginCommand command) {
        super(plugin, command);
    }

    @EventHandler
    public void onCommandRegister(CommandRegisteredEvent<BukkitBrigadierCommandSource> event) {
        if (event.getCommandLabel().equals(pluginCommand.getLabel())) {
            event.setLiteral(builder.register(this, event.getLiteral(), event.getBrigadierCommand()));
        }
    }
}
