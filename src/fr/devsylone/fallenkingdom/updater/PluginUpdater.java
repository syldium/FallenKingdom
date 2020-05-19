package fr.devsylone.fallenkingdom.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.Instant;
import java.util.Arrays;

import javax.naming.ServiceUnavailableException;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Streams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import fr.devsylone.fallenkingdom.Fk;
import lombok.Getter;
import lombok.Setter;

public class PluginUpdater extends BukkitRunnable
{
    private final Plugin plugin;
    private boolean enabled = true;
    private static final Gson GSON;
    private static final String URL_RELEASE_LIST = "https://servermods.forgesvc.net/servermods/files?projectIds=276763";
    static
    {
        GSON = new GsonBuilder().registerTypeAdapter(Instant.class, new JsonDeserializer<Instant>()
        {
            @Override
            public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
            {
                return json == null || !json.getAsString().matches("\\/Date\\(\\d+\\)\\/") ? null : Instant.ofEpochMilli(Long.parseLong(json.getAsString().replaceAll("\\D", "")));
            }
        }).registerTypeAdapter(ReleaseType.class, new JsonDeserializer<ReleaseType>()
        {
            @Override
            public ReleaseType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
            {
                return ReleaseType.valueOf(json.getAsString().toUpperCase());
            }
        }).create();
    }

    public PluginUpdater(Plugin plugin)
    {
        Validate.notNull(plugin, "Plugin cannot be null");
        this.plugin = plugin;
    }

    enum ReleaseType
    {
        RELEASE,
        BETA,
        ALPHA;

        private boolean isRelease()
        {
            return this == RELEASE;
        }
    }

    @Getter
    @Setter
    class ReleaseInfo
    {
        private Instant dateReleased;
        private URL downloadUrl;
        private String fileName;
        private URL fileUrl;
        private String gameVersion;
        private String md5;
        private String name;
        private int projectId;
        private ReleaseType releaseType;

        public String getPluginVersion()
        {
            return fileName.replaceAll("FallenKingdom-?", "");
        }
    }

    public void run()
    {
        if(!plugin.isEnabled() || !enabled)
            return;
        try
        {
            ReleaseInfo[] releases = GSON.fromJson(new InputStreamReader(new URL(URL_RELEASE_LIST).openConnection().getInputStream()), ReleaseInfo[].class);
            ReleaseInfo latestRelease = Streams.findLast(Arrays.stream(releases).filter(info -> info.getReleaseType().isRelease() && info.getName().startsWith("FallenKingdom"))).orElse(null);

            if(latestRelease == null)
                throw new ServiceUnavailableException();
            
            if(FilesUpdater.isGrowing(plugin.getDescription().getVersion(), latestRelease.getPluginVersion()))
            {
                this.plugin.getLogger().info("[Updater] Une nouvelle version est disponible : " + latestRelease.getPluginVersion() + " ! (Version actuelle : " + this.plugin.getDescription().getVersion() + ")");
                this.plugin.getLogger().info("[Updater] Téléchargement de la nouvelle version...");
                try
                {
                    ReadableByteChannel rbc = Channels.newChannel(latestRelease.getDownloadUrl().openStream());
                    final File latestJar = new File(Fk.getInstance().getDataFolder().getParent(), latestRelease.getFileName());
                    FileOutputStream fos = new FileOutputStream(latestJar);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.close();

                    this.plugin.getLogger().info("[Updater] Version téléchargée");
                    this.plugin.getLogger().info("[Updater] Téléchargement de l'updater...");

                    ReleaseInfo latestUpdaterRelease = Streams.findLast(Arrays.stream(releases).filter(info -> info.getReleaseType().isRelease() && info.getName().startsWith("FkUpdater"))).orElse(null);
                    if(latestUpdaterRelease == null)
                    {
                        this.plugin.getLogger().warning("[Updater] Une erreur est survenue : Impossible de trouver le plugin de mise à jour");
                        return;
                    }
                    rbc = Channels.newChannel(latestUpdaterRelease.getDownloadUrl().openStream());
                    final File updater = new File(Fk.getInstance().getDataFolder().getParent() + "/FkUpdater.jar");
                    fos = new FileOutputStream(updater);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.close();
                    this.plugin.getLogger().info("[Updater] Updater téléchargé");
                    this.plugin.getLogger().info("[Updater] Mise à jour...");

                    Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> {
                        try
                        {
                            Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().loadPlugin(updater));
                        }catch(UnknownDependencyException | InvalidPluginException | InvalidDescriptionException e)
                        {
                            e.printStackTrace();
                        }
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fkupdate " + Fk.class.getProtectionDomain().getCodeSource().getLocation().getPath() + " " + latestJar.getAbsolutePath());
                    });

                }catch(Exception e)
                {
                    this.plugin.getLogger().info("[Updater] Echec, veuilez la télécharger manuellement ici : http://www.spigotmc.org/resources/38878");
                    Fk.getInstance().addOnConnectWarning("Une nouvelle version est diponible : http://www.spigotmc.org/resources/38878");
                }
            }
        }catch(ServiceUnavailableException ex)
        {
            //forge down
        }catch(IOException ex)
        {
            ex.printStackTrace();
            this.plugin.getLogger().warning("[Updater] Une erreur est survenue");
        }
    }
}
