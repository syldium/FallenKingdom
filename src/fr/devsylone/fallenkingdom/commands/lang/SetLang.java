package fr.devsylone.fallenkingdom.commands.lang;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.manager.LanguageManager;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetLang extends FkCommand
{
    public SetLang() {
        super("set", "<lang>", Messages.CMD_MAP_LANG_SET, CommandPermission.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
        File[] files = Objects.requireNonNull(new File(plugin.getDataFolder(), File.separator + "locales").listFiles(), "Could not list files in locales/ directory.");

        String lang = Arrays.stream(files)
                .filter(File::isFile)
                .map(file -> file.getName().substring(0, file.getName().lastIndexOf('.')))
                .filter(locale -> locale.equals(args.get(0)))
                .findFirst()
                .orElseThrow(() -> new FkLightException("Unable to find language file for " + args.get(0) + "."));

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
        plugin.getLanguageManager().init(plugin);
        ChatUtils.sendMessage(sender, Messages.CMD_LANG_SET);
        return CommandResult.SUCCESS;
    }
}
