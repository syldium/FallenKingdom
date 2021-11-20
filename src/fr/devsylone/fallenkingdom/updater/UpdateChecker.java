package fr.devsylone.fallenkingdom.updater;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateChecker extends BukkitRunnable {

    private static final String LATEST_RELEASE_ENDPOINT = "https://api.github.com/repos/Etrenak/FallenKingdom/releases/latest";
    private static final String USER_AGENT = "FallenKingdom/Updater";

    private final Fk plugin;
    private final Logger logger;
    private final PluginVersion currentVersion;
    private @Nullable GitHubReleaseInfo latestRelease;

    public UpdateChecker(@NotNull Fk plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.currentVersion = new PluginVersion(plugin.getDescription().getVersion());
    }

    @Override
    public void run() {
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(LATEST_RELEASE_ENDPOINT).openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8))) {
                this.latestRelease = new Gson().fromJson(reader, GitHubReleaseInfo.class);
                this.latestRelease.pluginVersion = new PluginVersion(this.latestRelease.tagName);

                for (GitHubAssetInfo assetInfo : this.latestRelease.assets) {
                    if (isVersionRelevant(assetInfo)) {
                        this.latestRelease.platformAsset = assetInfo;
                    }
                }

                if (this.currentVersion.compareTo(this.latestRelease.pluginVersion) >= 0) {
                    return;
                }

                this.logger.info(Messages.CONSOLE_NEW_VERSION_AVAILABLE_1.getMessage() + " " + this.latestRelease.tagName + Messages.CONSOLE_NEW_VERSION_AVAILABLE_2.getMessage() + " (" + Messages.CONSOLE_NEW_VERSION_AVAILABLE_3.getMessage() + " " + this.plugin.getDescription().getVersion() + ").");
                final GitHubAssetInfo asset = this.latestRelease.platformAsset;
                boolean done = false;
                if (asset != null) {
                    this.logger.info(Messages.CONSOLE_DOWNLOADING_NEW_VERSION.getMessage());
                    done = this.plugin.updatePlugin(asset);
                }
                if (done) {
                    this.logger.info(Messages.CONSOLE_VERSION_DOWNLOADED.getMessage());
                    this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
                } else {
                    this.logger.warning(Messages.CONSOLE_UPDATE_ERROR.getMessage());
                    this.plugin.addOnConnectWarning(Messages.CONSOLE_NEW_VERSION_AVAILABLE.getMessage());
                }
            } catch (JsonSyntaxException | NumberFormatException ex) {
                this.logger.log(Level.WARNING, "Failed to parse the latest version info.", ex);
            }
        } catch (IOException ex) {
            this.logger.log(Level.WARNING, "Failed to get release info from api.github.com.", ex);
        }
    }

    public @NotNull PluginVersion getCurrentVersion() {
        return this.currentVersion;
    }

    public @Nullable GitHubReleaseInfo getLatestRelease() {
        return this.latestRelease;
    }

    private boolean isVersionRelevant(@NotNull GitHubAssetInfo assetInfo) {
        return true;
    }
}
