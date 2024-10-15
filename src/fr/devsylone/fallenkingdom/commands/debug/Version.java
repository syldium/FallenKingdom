package fr.devsylone.fallenkingdom.commands.debug;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Version extends FkCommand {

    public Version() {
        super("version", "update", null, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
        boolean update = !args.isEmpty() && args.get(0).equals("update");
        if (!update) {
            sender.sendMessage("§6FallenKingdom §7v" + plugin.getDescription().getVersion());
        }
        if (update && plugin.getUpdater().isUpToDate()) {
            throw new FkLightException(Messages.CMD_VERSION_UP_TO_DATE);
        }

        plugin.getUpdater().getReleaseInfo().thenAccept(releaseInfo -> {
            if (releaseInfo == null) {
                ChatUtils.sendMessage(sender,Messages.CMD_VERSION_UNKNOWN_LATEST);
                return;
            }
            if (plugin.getUpdater().isUpToDate() || releaseInfo.platformAsset == null) {
                ChatUtils.sendMessage(sender, Messages.CMD_VERSION_UP_TO_DATE);
                return;
            }
            if (update) {
                ChatUtils.sendMessage(sender, Messages.CONSOLE_DOWNLOADING_NEW_VERSION);
                if (plugin.updatePlugin(releaseInfo.platformAsset)) {
                    ChatUtils.sendMessage(sender, Messages.CONSOLE_VERSION_DOWNLOADED);
                } else {
                    ChatUtils.sendMessage(sender, Messages.CONSOLE_UPDATE_ERROR);
                }
            }
        });
        return CommandResult.SUCCESS;
    }
}
