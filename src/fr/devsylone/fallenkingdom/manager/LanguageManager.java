package fr.devsylone.fallenkingdom.manager;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class LanguageManager {

    public static final String MESSAGES_BUNDLE = "messages";
    public static final String LOCALES = "locales";
    private static final String[] BUNDLED_LOCALES = new String[]{"fr", "en"};

    private static final ResourceBundle NULL_BUNDLE = new ResourceBundle() {
        public @NotNull Enumeration<String> getKeys() {
            return Collections.emptyEnumeration();
        }

        protected Object handleGetObject(@NotNull String key) {
            return null;
        }
    };

    private final ResourceBundle defaultBundle;
    private final Set<String> untranslatedKeys = new HashSet<>(0);
    private final Fk plugin;
    private ResourceBundle localeBundle;
    private ResourceBundle customBundle;
    private Locale locale;

    public LanguageManager(@NotNull Fk plugin) {
        this.defaultBundle = ResourceBundle.getBundle(MESSAGES_BUNDLE, Locale.FRENCH, new UTF8PropertiesControl());
        this.plugin = plugin;
        this.copyDefaultTranslations();
        this.updateLocale(parseLocale(plugin.getConfig().getString("lang", "unknown")));
        if (!this.isLocaleSet()) {
            plugin.addOnConnectWarning(this.createInvite());
        }
    }

    public void updateLocale(@NotNull Locale userLocale) {
        ResourceBundle.clearCache();

        try {
            this.localeBundle = ResourceBundle.getBundle(MESSAGES_BUNDLE, userLocale, new UTF8PropertiesControl());
        } catch (MissingResourceException ex) {
            this.localeBundle = NULL_BUNDLE;
        }

        try {
            this.customBundle = ResourceBundle.getBundle(MESSAGES_BUNDLE, userLocale, new FileResClassLoader(this.plugin.getClass().getClassLoader(), this.plugin), new UTF8PropertiesControl());
        } catch (final MissingResourceException ex) {
            this.customBundle = NULL_BUNDLE;
        }
        this.locale = userLocale;
    }

    public @NotNull String get(@NotNull Messages key) {
        return this.translate(key.getAccessor());
    }

    private @NotNull String translate(@PropertyKey(resourceBundle = MESSAGES_BUNDLE) @NotNull String string) {
        try {
            try {
                return this.customBundle.getString(string);
            } catch (MissingResourceException ex) {
                return this.localeBundle.getString(string);
            }
        } catch (MissingResourceException ex) {
            if (this.untranslatedKeys.add(string) && this.isLocaleSet()) {
                this.plugin.getLogger().log(Level.WARNING, String.format("Missing translation key \"%s\" in translation file \"%s\"", ex.getKey(), this.locale.toString()));
            }
            return this.defaultBundle.getString(string);
        }
    }

    private void copyDefaultTranslations() {
        Path localeDir = this.plugin.getDataFolder().toPath().resolve(LOCALES);
        try {
            Files.createDirectories(localeDir);
        } catch (IOException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to create locales folder", ex);
            return;
        }

        for (String locale : BUNDLED_LOCALES) {
            String fileName = String.format("%s_%s.properties", MESSAGES_BUNDLE, locale);
            try (InputStream stream = Fk.class.getResourceAsStream("/" + fileName)) {
                requireNonNull(stream, "failed to read bundled translation file");
                Files.copy(stream, localeDir.resolve(fileName));
            } catch (FileAlreadyExistsException ignored) {
            } catch (IOException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to copy " + locale + " translation file to plugin folder", ex);
            }
        }
    }

    public @NotNull Locale getLocale() {
        return this.locale;
    }

    public boolean isLocaleSet() {
        return this.localeBundle.getLocale() != null;
    }

    public @NotNull BaseComponent[] createInvite() {
        BaseComponent builder = new TextComponent("Veuillez sélectionner votre langue en cliquant dessus.\n\u21AA Please select your language by clicking on it.\n\u21AA Bitte wählen Sie Ihre Sprache aus, indem Sie darauf klicken.\n");
        builder.setColor(ChatColor.RED);

        TextComponent leftBracket = new TextComponent("[");
        leftBracket.setColor(ChatColor.GRAY);
        TextComponent rightBracket = new TextComponent("]");
        rightBracket.setColor(ChatColor.GRAY);
        TextComponent space = null;

        for (String locale : this.listLocales()) {
            TextComponent component = new TextComponent(locale);
            component.setColor(ChatColor.DARK_AQUA);
            component.setUnderlined(true);
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.BLUE + "Use this locale").create()));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fk lang set " + locale));
            builder.addExtra(leftBracket);
            builder.addExtra(component);
            builder.addExtra(rightBracket);
            if (space != null) {
                builder.addExtra(space);
            } else {
                space = new TextComponent(" ");
            }
        }
        BaseComponent[] prefix = TextComponent.fromLegacyText("§7[§5Fk§7] ");
        BaseComponent[] result = new BaseComponent[prefix.length + 1];
        System.arraycopy(prefix, 0, result, 0, prefix.length);
        result[prefix.length] = builder;
        return result;
    }

    public @NotNull @Unmodifiable List<String> listLocales() {
        try (Stream<Path> files = Files.list(this.plugin.getDataFolder().toPath().resolve(LOCALES))) {
            return files.filter(path -> {
                        if (!Files.isRegularFile(path)) {
                            return false;
                        }
                        String name = path.getFileName().toString();
                        return name.startsWith(LanguageManager.MESSAGES_BUNDLE + '_') && name.endsWith(".properties");
                    })
                    .map(path -> {
                        String name = path.getFileName().toString();
                        return name.substring(LanguageManager.MESSAGES_BUNDLE.length() + 1, name.lastIndexOf('.'));
                    })
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            return Arrays.asList(BUNDLED_LOCALES);
        }
    }

    public boolean isLocaleAvailable(@NotNull String locale) {
        return Files.exists(this.plugin.getDataFolder().toPath().resolve(LOCALES).resolve(String.format("%s_%s.properties", MESSAGES_BUNDLE, locale)))
                || Arrays.asList(BUNDLED_LOCALES).contains(locale);
    }

    public static @NotNull Locale parseLocale(@Nullable String languageTag) {
        String[] parts = languageTag == null ? new String[0] : languageTag.split("[_\\.]");
        if (parts.length == 2) {
            return new Locale(parts[0], parts[1]);
        } else if (parts.length == 3) {
            return new Locale(parts[0], parts[1], parts[2]);
        } else {
            return parts.length > 0 ? new Locale(parts[0]) : Locale.getDefault();
        }
    }

    /**
     * Attempts to load properties files from the plugin directory before falling back to the jar.
     */
    private static class FileResClassLoader extends ClassLoader {

        private final File dataFolder;

        FileResClassLoader(@NotNull ClassLoader classLoader, @NotNull Fk plugin) {
            super(classLoader);
            this.dataFolder = new File(plugin.getDataFolder(), LOCALES);
        }

        @Override
        public URL getResource(final String string) {
            File file = new File(this.dataFolder, string);
            if (file.exists()) {
                try {
                    return file.toURI().toURL();
                } catch (final MalformedURLException ignored) { }
            }
            return null;
        }

        @Override
        public InputStream getResourceAsStream(final String string) {
            final File file = new File(this.dataFolder, string);
            if (file.exists()) {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException ignored) { }
            }
            return null;
        }
    }

    /**
     * Reads .properties files as UTF-8 instead of ISO-8859-1, which is the default on Java 8/below.
     * Java 9 fixes this by defaulting to UTF-8 for .properties files.
     */
    private static class UTF8PropertiesControl extends ResourceBundle.Control {

        @Override
        public ResourceBundle newBundle(final String baseName, final Locale locale, final String format, final ClassLoader loader, final boolean reload) throws IOException {
            String resourceName = this.toResourceName(this.toBundleName(baseName, locale), "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) { // UTF-8
                    bundle = new PropertyResourceBundle(reader);
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }

        @Override // ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT)
        public @Nullable Locale getFallbackLocale(final String baseName, final Locale locale) {
            return null;
        }
    }
}
