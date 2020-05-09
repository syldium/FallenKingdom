package fr.devsylone.fallenkingdom.manager;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

public class LanguageManager
{
    private static String langPrefix;

    private static final Properties defaultLocale = new Properties();
    private static final Properties locale = new Properties();

    private static int taskId = -1;

    private LanguageManager()
    {
    }

    public static void init(Fk plugin)
    {
        if (taskId > 0) {
            Bukkit.getScheduler().cancelTask(taskId);
        }

        langPrefix = plugin.getConfig().getString("lang", "unknown");

        // Copie des fichiers de langue par défaut pour permettre d'éditer
        String[] locales = new String[] {"fr"};
        for (String locale : locales) {
            String path = "locales" + File.separator + locale + ".properties";
            if (!new File(plugin.getDataFolder(), File.separator + path).exists())
                plugin.saveResource(path, false);
        }

        // Demande de la langue
        boolean skipCustomLangLoad = false;
        if (langPrefix == null || langPrefix.equalsIgnoreCase("unknown")) {
            File[] files = Objects.requireNonNull(new File(plugin.getDataFolder(), File.separator + "locales").listFiles(), "Unable to list files in the locales/ directory.");
            locales = Arrays.stream(files)
                .filter(File::isFile)
                .map(file -> file.getName().substring(0, file.getName().lastIndexOf('.')))
                .toArray(String[]::new);
            String message = ChatColor.RED + "Veuillez sélectionner votre langue / Please select your language";
            ComponentBuilder localeComponentBuilder = new ComponentBuilder(ChatUtils.PREFIX);
            for (String locale : locales) {
                BaseComponent[] localeComponents = TextComponent.fromLegacyText(ChatColor.GRAY + "[" + ChatColor.UNDERLINE + ChatColor.DARK_AQUA + locale + ChatColor.GRAY + "] ");
                for (BaseComponent component : localeComponents) {
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.BLUE + "Use this locale").create()));
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fk lang set " + locale));
                }
                localeComponents[1].setItalic(true);
                localeComponents[2].setItalic(false);
                localeComponentBuilder.append(localeComponents);
            }
            Bukkit.getConsoleSender().sendMessage(message);
            taskId = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ChatUtils.sendMessage(player, message);
                        player.spigot().sendMessage(localeComponentBuilder.create());
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 5 * 20, 15 * 20).getTaskId();
            skipCustomLangLoad = true;
        }

        // Chargement de la langue par défaut
        InputStream stream = Objects.requireNonNull(plugin.getResource("locales/fr.properties"), "Cannot load default language file in resources");
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
            plugin.getLogger().warning("Unable to load language file " + langPrefix + ". Using default one (french)!");
            plugin.getLogger().warning("Cause: " + e.getMessage());
        }
    }

    public static String getLanguageMessage(String path)
    {
        return getLanguageMessage(path, false);
    }

    public static String getLanguageMessage(String path, boolean strict)
    {
        String prop = locale.getProperty(path);
        if (prop == null && !strict) {
            Fk.getInstance().getLogger().warning("Key " + path + " not translated in your language; using of default value");
            return defaultLocale.getProperty(path);
        }
        return prop;
    }

    public static String getLocalePrefix() {
        return langPrefix;
    }
}