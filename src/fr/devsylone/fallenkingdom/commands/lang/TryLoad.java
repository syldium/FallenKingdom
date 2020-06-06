package fr.devsylone.fallenkingdom.commands.lang;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.manager.LanguageManager;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class TryLoad extends FkCommand
{
    public TryLoad()
    {
        super("tryload", Messages.CMD_MAP_LANG_TRY_LOAD, CommandPermission.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
        plugin.getLogger().info("Start of lang report");
        // Recherche des traductions en mode strict
        long missingTranslations = Arrays.stream(Messages.values()).filter(message -> {
            String msg = LanguageManager.getLanguageMessage(message.getAccessor(), true);
            if (msg == null && message.getMessage() == null)
                Fk.getInstance().getLogger().severe("Value of " + message.getAccessor() + " not set in any file!");
            return msg == null;
        }).count();
        plugin.getLogger().info("End of lang report");
        sender.sendMessage(ChatUtils.PREFIX + Messages.CMD_LANG_TRY_LOAD);
        if (missingTranslations > 0)
            ChatUtils.sendMessage(sender, ChatColor.RED + Messages.CMD_LANG_TRY_LOAD_MISSING.getMessage().replace("%nb%", String.valueOf(missingTranslations)));
        return CommandResult.SUCCESS;
    }
}
