package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import java.io.File;
import java.util.List;
import org.bukkit.command.CommandSender;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.Confirmable;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.FkConfig;
import fr.devsylone.fallenkingdom.utils.Messages;

/**
 * Load a savefile, setting up the instance.
 */
public class Load extends FkCommand implements Confirmable {
    private final static String NAME = "load";
    private final static String USAGE = "save";

    public Load() {
        super(NAME, USAGE, Messages.CMD_MAP_GAME_LOAD, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
        if (args.isEmpty() || args.size() > 1) {
            throw new FkLightException(Messages.CMD_ERROR_UNKNOWN_ARG);
        }
        // Check if config name legal
        if (args.get(0).equals("config")) {
            throw new FkLightException(Messages.CMD_ERROR_CONFIG_NAME);
        }

        // Load new save
        FkConfig save = new FkConfig(new File(plugin.getDataFolder(), args.get(0) + ".yml"));
        if (!save.fileExists()) {
            throw new FkLightException(Messages.CMD_ERROR_FILE_DOESNT_EXIST.getMessage()
                    .replaceAll("%save%", args.get(0) + ".yml"));
        }
        save.load();
        plugin.getSaveableManager().loadAll(save);
        plugin.getDisplayService().updateAll();
        broadcast(Messages.CMD_GAME_LOAD.getMessage().replaceAll("%save%", args.get(0) + ".yml"));
        return CommandResult.SUCCESS;
    }
}
