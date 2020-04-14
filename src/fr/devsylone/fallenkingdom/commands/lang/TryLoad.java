package fr.devsylone.fallenkingdom.commands.lang;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.FkCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.manager.LanguageManager;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TryLoad extends FkCommand
{
    public TryLoad()
    {
        super("lang tryload", "", 0, Messages.CMD_MAP_LANG_TRY_LOAD.getMessage());
    }

    @Override
    public void execute(Player sender, FkPlayer fkp, String[] args)
    {
        Fk.getInstance().getLogger().info("Start of lang report");
        // Recherche des traductions en mode strict
        long missingTranslations = Arrays.stream(Messages.values()).filter(message -> {
            String msg = LanguageManager.getLanguageMessage(message.getAccessor(), true);
            if (msg == null && message.getMessage() == null)
                Fk.getInstance().getLogger().severe("Value of " + message.getAccessor() + " not set in any file!");
            return msg == null;
        }).count();
        Fk.getInstance().getLogger().info("End of lang report");
        fkp.sendMessage(Messages.CMD_LANG_TRY_LOAD);
        if (missingTranslations > 0)
            ChatUtils.sendMessage(sender, ChatColor.RED + Messages.CMD_LANG_TRY_LOAD_MISSING.getMessage().replace("%nb%", String.valueOf(missingTranslations)));
    }
}
