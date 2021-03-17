package fr.devsylone.fallenkingdom.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class LanguageManager
{
    private String langPrefix;

    private final Properties defaultLocale = new Properties();
    private final Properties locale = new Properties();

    private int taskId = -1;

    public void init(Fk plugin) {
        if (taskId > 0) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }

        langPrefix = plugin.getConfig().getString("lang", "unknown");

        // Copie des fichiers de langue par défaut pour permettre d'éditer
    String[] locales = new String[] {"fr", "en"/*, "de"*/};
        for (String locale : locales) {
            String path = "locales" + File.separator + locale + ".properties";
            if (!new File(plugin.getDataFolder(), File.separator + path).exists())
                plugin.saveResource(path, false);
        }

        // Demande de la langue
        boolean skipCustomLangLoad = false;
        if (langPrefix == null || langPrefix.equalsIgnoreCase("unknown")) {
            File[] files = Objects.requireNonNull(new File(plugin.getDataFolder(), File.separator + "locales").listFiles(), Messages.CONSOLE_LANG_COULD_NOT_LIST_FILES.getMessage());
            locales = Arrays.stream(files)
                .filter(File::isFile)
                .map(file -> file.getName().substring(0, file.getName().lastIndexOf('.')))
                .toArray(String[]::new);
            String message = ChatColor.RED + "Veuillez sélectionner votre langue en cliquant dessus.\n\u21AA Please select your language by clicking on it.\n\u21AA Bitte wählen Sie Ihre Sprache aus, indem Sie darauf klicken.";
            BaseComponent[] localeComponents = new BaseComponent[] {};
            for (String locale : locales) {
                BaseComponent[] localeComponent = TextComponent.fromLegacyText(ChatColor.GRAY + "[" + ChatColor.UNDERLINE + ChatColor.DARK_AQUA + locale + ChatColor.GRAY + "] ");
                for (BaseComponent component : localeComponent) {
                        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bUtiliser ce §erépertoire\n§bUse this §elocale\n§bVerwendung Się dièses §eGebietsschema").create()));
                        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fk lang set " + locale));
                }
                localeComponent[1].setItalic(true);
                localeComponent[2].setItalic(false);
                localeComponents = (BaseComponent[]) ArrayUtils.addAll(localeComponents, localeComponent);
            }
            TextComponent finalMessage = new TextComponent((BaseComponent[]) ArrayUtils.addAll(TextComponent.fromLegacyText(ChatUtils.PREFIX), localeComponents));
            Bukkit.getConsoleSender().sendMessage(message);
            taskId = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ChatUtils.sendMessage(player, message);
                        player.spigot().sendMessage(finalMessage);
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 5 * 20, 15 * 20).getTaskId();
            skipCustomLangLoad = true;
        }

        // Chargement de la langue par défaut
        InputStream stream = Objects.requireNonNull(plugin.getResource("locales/fr.properties"), Messages.CONSOLE_CANNOT_LOAD_DEFAULT_LANGUAGE.getMessage());
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            defaultLocale.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (skipCustomLangLoad) {
            return;
        }

        // Chargement de la langue de l'utilisateur
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(plugin.getDataFolder() + "/locales/" + langPrefix + ".properties"), StandardCharsets.UTF_8)) {
            locale.load(reader);
        } catch (IOException e) {
            plugin.getLogger().warning(Messages.CONSOLE_UNABLE_TO_LOAD_LANGUAGE_FILE_1.getMessage() + " " + langPrefix + ". " + Messages.CONSOLE_UNABLE_TO_LOAD_LANGUAGE_FILE_2.getMessage());
            plugin.getLogger().warning(Messages.CONSOLE_CAUSE.getMessage() + " " + e.getMessage());
        }
    }

    public String getLanguageMessage(String path)
    {
        return getLanguageMessage(path, false);
    }

    public String getLanguageMessage(String path, boolean strict)
    {
        String prop = locale.getProperty(path);
        if (prop == null && !strict) {
            Fk.getInstance().getLogger().warning("Key " + path + " not translated in your language; using of default value"); // À ne pas traduire !
            return defaultLocale.getProperty(path);
        }
        return prop;
    }

    public String getLocalePrefix() {
        return langPrefix;
    }
}
