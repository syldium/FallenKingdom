package fr.devsylone.fallenkingdom.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.devsylone.fallenkingdom.Fk;

public class PluginUpdater extends BukkitRunnable
{
    private final Plugin plugin;
    private boolean enabled;

    private static final String URL_FK_LATEST_RELEASE = "https://api.github.com/repos/Etrenak/FallenKingdom/releases/latest";
    private static final String URL_UPDATER_LATEST_RELEASE = "https://api.github.com/repos/Etrenak/FkUpdater/releases/latest";
    private static final Pattern VERSION_PATTERN = Pattern.compile("^\\d+\\.\\d+(\\.\\d+)?(-BETA\\d*)?$");

    public PluginUpdater(Plugin plugin)
    {
        Validate.notNull(plugin, "Plugin cannot be null");
        this.plugin = plugin;

        boolean isRelease = VERSION_PATTERN.matcher(plugin.getDescription().getVersion().toUpperCase(Locale.ROOT)).find();
        if(!isRelease)
            plugin.getLogger().info("[Updater] " + plugin.getDescription().getVersion() + " is a development version.");
        this.enabled = isRelease;
    }

    public void run()
    {
        if(!plugin.isEnabled() || !enabled)
            return;
        try
        {
            URLConnection latestPluginReleaseConnection = new URL(URL_FK_LATEST_RELEASE).openConnection();
            latestPluginReleaseConnection.setRequestProperty("User-Agent", "FallenKingdom/" + plugin.getDescription().getVersion());
            JsonObject latestPluginReleaseInfo = new JsonParser().parse(new InputStreamReader(latestPluginReleaseConnection.getInputStream())).getAsJsonObject();
            String latestPluginVersion = latestPluginReleaseInfo.get("tag_name").getAsString().substring(1);

            if(FilesUpdater.isGrowing(plugin.getDescription().getVersion(), latestPluginVersion))
            {
                this.plugin.getLogger().info("[Updater] Une nouvelle version est disponible : " + latestPluginVersion + " ! (Version actuelle : " + this.plugin.getDescription().getVersion() + ")");
                this.plugin.getLogger().info("[Updater] Téléchargement de la nouvelle version...");
                try
                {
                    JsonObject pluginAssetInfo = latestPluginReleaseInfo.get("assets").getAsJsonArray().get(0).getAsJsonObject();
                    ReadableByteChannel rbc = Channels.newChannel(new URL(pluginAssetInfo.get("browser_download_url").getAsString()).openStream());
                    final File latestPluginJar = new File(Fk.getInstance().getDataFolder().getParent(), pluginAssetInfo.get("name").getAsString());
                    FileOutputStream fos = new FileOutputStream(latestPluginJar);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.close();

                    this.plugin.getLogger().info("[Updater] Version téléchargée");
                    this.plugin.getLogger().info("[Updater] Téléchargement de l'updater...");

                    URLConnection latestUpdaterReleaseConnection = new URL(URL_UPDATER_LATEST_RELEASE).openConnection();
                    latestUpdaterReleaseConnection.setRequestProperty("User-Agent", "FallenKingdom/" + plugin.getDescription().getVersion());
                    JsonObject latestUpdaterReleaseInfo = new JsonParser().parse(new InputStreamReader(latestUpdaterReleaseConnection.getInputStream())).getAsJsonObject();
                    JsonObject updaterAssetInfo = latestUpdaterReleaseInfo.get("assets").getAsJsonArray().get(0).getAsJsonObject();

                    rbc = Channels.newChannel(new URL(updaterAssetInfo.get("browser_download_url").getAsString()).openStream());
                    final File latestUpdaterJar = new File(Fk.getInstance().getDataFolder().getParent(), updaterAssetInfo.get("name").getAsString());
                    fos = new FileOutputStream(latestUpdaterJar);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.close();

                    this.plugin.getLogger().info("[Updater] Updater téléchargé");
                    this.plugin.getLogger().info("[Updater] Mise à jour...");

                    Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> {
                        try
                        {
                            Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().loadPlugin(latestUpdaterJar));
                        }catch(UnknownDependencyException | InvalidPluginException | InvalidDescriptionException e)
                        {
                            e.printStackTrace();
                        }
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fkupdate " + Fk.class.getProtectionDomain().getCodeSource().getLocation().getPath() + " " + latestPluginJar.getAbsolutePath());
                    });

                }catch(Exception e)
                {
                    this.plugin.getLogger().info("[Updater] Echec, veuilez la télécharger manuellement ici : http://www.spigotmc.org/resources/38878");
                    Fk.getInstance().addOnConnectWarning("Une nouvelle version est diponible : http://www.spigotmc.org/resources/38878");
                }
            }
        }catch(IOException ex)
        {
            ex.printStackTrace();
            this.plugin.getLogger().warning("[Updater] Une erreur est survenue");
        }
    }
}
