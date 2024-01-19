package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.FkConfig;
import fr.devsylone.fallenkingdom.utils.Messages;

public class Save extends FkCommand {
    private final static String NAME = "save";
    private final static String USAGE = "<save>";


    public Save() {
        super(NAME, USAGE, Messages.CMD_MAP_GAME_SAVE, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
        List<String> saveFiles = new ArrayList<>();
        if (args.isEmpty()) {
            saveFiles.add("save.yml");
        }
        for (String arg : args) {
            saveFiles.add(arg + ".yml");
        }
        for (String fp : saveFiles) {
            if (fp.equals("config.yml")) {
                throw new FkLightException(
                        Messages.CMD_ERROR_CONFIG_NAME.getMessage().replaceAll("%save%", fp));
            }
            FkConfig save = new FkConfig(new File(plugin.getDataFolder(), fp));
            save.set("last_version", plugin.getPreviousVersion());
            plugin.getSaveableManager().delayedSaveAll(save);
            broadcast(Messages.CMD_GAME_SAVE.getMessage().replaceAll("%save%", fp));
        }

        return CommandResult.SUCCESS;
    }
}
