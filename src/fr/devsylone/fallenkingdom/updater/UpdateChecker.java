package fr.devsylone.fallenkingdom.updater;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateChecker implements Runnable {

    private static final long CACHE_TIME = 30 * 60 * 1000;
    private static final String LATEST_RELEASE_ENDPOINT = "https://api.github.com/repos/syldium/FallenKingdom/releases/latest";
    private static final String USER_AGENT = "FallenKingdom/Updater";

    private final Logger logger;
    private final PluginVersion currentVersion;
    private @Nullable GitHubReleaseInfo latestRelease;

    private long lastCheck = 0;
    private boolean checkingVersion = false;
    private boolean hasVersion = false;
    private boolean upToDate = false;
    private final ReentrantLock versionLock = new ReentrantLock();

    public UpdateChecker(@NotNull PluginVersion currentVersion, @NotNull Logger logger) {
        this.logger = logger;
        this.currentVersion = currentVersion;
    }

    @Override
    public void run() {
        boolean isOwner = this.versionLock.tryLock();
        this.checkingVersion = true;
        Reader reader = null;
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URI(LATEST_RELEASE_ENDPOINT).toURL().openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT + '/' + this.currentVersion);
            connection.connect();

            reader = new InputStreamReader(connection.getInputStream());
            JsonObject apiResponse = new JsonParser().parse(reader).getAsJsonObject();
            this.latestRelease = new Gson().fromJson(apiResponse, GitHubReleaseInfo.class);

            for (GitHubAssetInfo assetInfo : this.latestRelease.assets) {
                if (!isVersionRelevant(assetInfo)) {
                    continue;
                }
                this.latestRelease.platformAsset = assetInfo;

                if (this.currentVersion.compareTo(new PluginVersion(this.latestRelease.tagName)) < 0) {
                    this.logger.info(Messages.CONSOLE_NEW_VERSION_AVAILABLE_1.getMessage() + " " + this.latestRelease.tagName + Messages.CONSOLE_NEW_VERSION_AVAILABLE_2.getMessage() + " (" + Messages.CONSOLE_NEW_VERSION_AVAILABLE_3.getMessage() + " " + this.currentVersion + ").");
                } else {
                    this.upToDate = true;
                }
            }
        } catch (FileNotFoundException ex) {
            this.logger.log(Level.WARNING, "404 error: " + ex.getMessage());
        } catch (Exception ex) {
            this.logger.log(Level.WARNING, "Failed to get release info from api.github.com.", ex);
        } finally {
            this.checkingVersion = false;
            this.hasVersion = true;
            this.lastCheck = System.currentTimeMillis();
            if (isOwner) {
                this.versionLock.unlock();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    this.logger.log(Level.WARNING, "Unable to read the list of updates.", ex);
                }
            }
        }
    }

    public @NotNull CompletableFuture<@Nullable GitHubReleaseInfo> getReleaseInfo() {
        return CompletableFuture.supplyAsync(this::fetchReleaseInfo);
    }

    private @Nullable GitHubReleaseInfo fetchReleaseInfo() {
        if (this.hasVersion) {
            if (System.currentTimeMillis() - this.lastCheck > CACHE_TIME) {
                this.hasVersion = false;
            } else {
                return this.latestRelease;
            }
        }

        this.versionLock.lock();
        try {
            if (!this.checkingVersion) {
                this.run();
            }
        } finally {
            this.versionLock.unlock();
        }
        return this.latestRelease;
    }

    public @NotNull PluginVersion getCurrentVersion() {
        return this.currentVersion;
    }

    public boolean isUpToDate() {
        return this.upToDate;
    }

    private boolean isVersionRelevant(@NotNull GitHubAssetInfo assetInfo) {
        return true;
    }
}
