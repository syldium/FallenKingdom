package fr.devsylone.fallenkingdom.updater;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class GitHubAssetInfo {

    String url;

    int id;

    String name;

    @SerializedName("browser_download_url")
    String browserDownloadUrl;

    public @NotNull String name() {
        return this.name;
    }

    public @NotNull String browserDownloadUrl() {
        return this.browserDownloadUrl;
    }
}
