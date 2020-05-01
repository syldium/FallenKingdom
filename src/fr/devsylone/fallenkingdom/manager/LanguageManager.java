package fr.devsylone.fallenkingdom.manager;

import fr.devsylone.fallenkingdom.Fk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public class LanguageManager
{
    private static String langPrefix;

    private static final Properties defaultLocale = new Properties();
    private static final Properties locale = new Properties();

    private LanguageManager()
    {
    }

    public static void init(Fk plugin)
    {
        langPrefix = plugin.getConfig().getString("lang", "fr");

        // Copie des fichiers de langue par défaut pour permettre d'éditer
        String[] locales = new String[] {"fr"};
        for (String locale : locales) {
            String path = "locales/" + locale + ".properties";
            if (!new File(plugin.getDataFolder(), '/' + path).exists())
                plugin.saveResource(path, false);
        }

        // Chargement de la langue par défaut
        InputStream stream = Objects.requireNonNull(plugin.getResource("locales/fr.properties"), "Cannot load default language file in resources");
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            defaultLocale.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
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
