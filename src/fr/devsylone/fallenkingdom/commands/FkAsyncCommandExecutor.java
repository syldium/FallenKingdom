package fr.devsylone.fallenkingdom.commands;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.Messages;

import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Paper seulement.
 * Permet l'autocomplétion asynchrone des arguments.
 */
public class FkAsyncCommandExecutor extends FkCommandExecutor implements Listener {

    private final Pattern delimiter = Pattern.compile(" ");

    public FkAsyncCommandExecutor(Fk plugin, PluginCommand command) {
        super(plugin, command);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info(Messages.CONSOLE_ENABLE_ASYNCHRONOUS_TAB_COMPLETION.getMessage());
    }

    @EventHandler(ignoreCancelled = true)
    public void onAsyncTabComplete(AsyncTabCompleteEvent e) {
        if (!e.isCommand()) {
            return;
        }

        if (e.getBuffer().isEmpty()) {
            return;
        }

        String[] args = delimiter.split(e.getBuffer(), -1);
        String label = e.getBuffer().charAt(0) == '/' ? args[0].substring(1) : args[0];
        args = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[]{""};

        if (!label.equals("fk")) {
            return;
        }

        e.setCompletions(tabCompleteCommand(plugin, e.getSender(), new ArrayList<>(Arrays.asList(args))));
        e.setHandled(true);
    }
}
