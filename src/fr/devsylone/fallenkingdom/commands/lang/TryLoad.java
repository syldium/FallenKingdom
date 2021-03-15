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
        plugin.getLogger().info(Messages.CONSOLE_START_OF_LANG_REPORT.getMessage());
        // Recherche des traductions en mode strict
        long missingTranslations = Arrays.stream(Messages.values()).filter(message -> {
            String msg = plugin.getLanguageManager().getLanguageMessage(message.getAccessor(), true);
            if (msg == null && message.getMessage() == null)
                Fk.getInstance().getLogger().severe(Messages.CONSOLE_VALUE_NOT_SET_IN_ANY_FILE_1.getMessage() + " " + message.getAccessor() + " " + Messages.CONSOLE_VALUE_NOT_SET_IN_ANY_FILE_2.getMessage());
            return msg == null;
        }).count();
        plugin.getLogger().info(Messages.CONSOLE_END_OF_LANG_REPORT.getMessage());
        sender.sendMessage(ChatUtils.PREFIX + Messages.CMD_LANG_TRY_LOAD);
        if (missingTranslations > 0)
            ChatUtils.sendMessage(sender, ChatColor.RED + Messages.CMD_LANG_TRY_LOAD_MISSING.getMessage().replace("%nb%", String.valueOf(missingTranslations)));
        return CommandResult.SUCCESS;
    }
}
