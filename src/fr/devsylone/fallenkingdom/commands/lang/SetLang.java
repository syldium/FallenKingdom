package fr.devsylone.fallenkingdom.commands.lang;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.display.ScoreboardDisplayService;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.manager.LanguageManager;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetLang extends FkCommand
{
    public SetLang() {
        super("set", "<lang>", Messages.CMD_MAP_LANG_SET, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
        boolean isDefaultScoreboard = plugin.getDisplayService().scoreboard().isDefaultSidebar();
        String lang = args.get(0);
        if (!plugin.getLanguageManager().isLocaleAvailable(lang)) {
            throw new FkLightException(Messages.CMD_ERROR_LANG_SET_LANG_LANG_NOT_FOUND + " " + lang + ".");
        }

        plugin.getConfig().set("lang", lang);
        Path path = new File(plugin.getDataFolder(), "config.yml").toPath();
        try (Stream<String> lines = Files.lines(path)) {
            List<String> replaced = lines
                    .map(line -> line.replaceAll("^lang:(.+)$", "lang: \"" + lang + "\""))
                    .collect(Collectors.toList());
            Files.write(path, replaced);
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.getLanguageManager().updateLocale(LanguageManager.parseLocale(lang));

        if (isDefaultScoreboard) {
            plugin.getDisplayService().setScoreboard(ScoreboardDisplayService.createDefault());
            plugin.getDisplayService().updateAll();
        }
        ChatUtils.sendMessage(sender, Messages.CMD_LANG_SET);
        return CommandResult.SUCCESS;
    }
}
