package fr.devsylone.fallenkingdom.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.FkCommandExecutor;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import fr.devsylone.fkpi.rules.Rule;
import org.jetbrains.annotations.NotNull;

import static fr.devsylone.fallenkingdom.version.Version.classExists;

public final class DebuggerUtils
{
    private static final String USER_AGENT = "FallenKingdom/Debug";

    private DebuggerUtils() {}

    public static boolean debugGame(boolean send, String username) {
        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.yml");
        final PluginLog log = new PluginLog();
        log.addLine("--------------------------------------");
        log.addLine("OS : " + System.getProperty("os.name"));
        log.addLine("Java version : " + System.getProperty("java.version"));
        log.addLine(getServerSoftwareName() + " version : " + Bukkit.getBukkitVersion() + " | " + Bukkit.getVersion());
        log.addLine("Plugin version : v" + Fk.getInstance().getDescription().getVersion());
        log.addLine("---- Commands since reload ----");
        for (Map.Entry<String, Boolean> cmd : FkCommandExecutor.logs.entrySet()) {
            log.addLine("  > " + cmd.getKey() + (cmd.getValue() ? "" : "  [Error occurred]"));
        }
        log.addLine("---- Rules ----");
        for (Map.Entry<Rule<?>, Object> rule : Fk.getInstance().getFkPI().getRulesManager().getRulesList().entrySet()) {
            log.addLine("  > " + rule.getKey().getName() + " : " + rule.getValue());
        }
        log.addLine("---- Game ----");
        log.addLine("  > State: " + Fk.getInstance().getGame().getState());
        log.addLine("  > Day: " + Fk.getInstance().getGame().getDay());
        log.addLine("  > Time: " + Fk.getInstance().getGame().getTime());
        log.addLine("  > Nether: " + Fk.getInstance().getGame().isNetherEnabled());
        log.addLine("  > PvP: " + Fk.getInstance().getGame().isPvpEnabled());
        log.addLine("  > End: " + Fk.getInstance().getGame().isEndEnabled());
        log.addLine("  > Assaults: " + Fk.getInstance().getGame().isAssaultsEnabled());
        log.addLine("---- Teams ----");
        for (Team team : FkPI.getInstance().getTeamManager().getTeams()) {
            Location loc = team.getBase() == null ? null : team.getBase().getCenter();
            log.addLine("  > " + team.getName() + ": " + (loc == null ? '/' : "(" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ") - " + team.getBase().getRadius()) + " - " + team.getPlayers());
        }
        log.addLine("---- Chests ---");
        for (LockedChest chest : Fk.getInstance().getFkPI().getLockedChestsManager().getChests()) {
            log.addLine("  > " + chest.toString());
        }
        log.addLine("---- Plugins ---");
        log.addLine("  > " + Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Plugin::getName).collect(Collectors.joining(", ")));

        Fk.getInstance().getLogger().info(log.getContent());
        if (!send) {
            return true;
        }

        boolean complete = false;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Fk.getInstance().getPluginFolder())) {
            for (Path path : stream) {
                if (!Files.isDirectory(path) && matcher.matches(path)) {
                    log.addFileContent(path);
                }
            }
            final Path logs = Fk.getInstance().getRunDir().resolve("logs");
            final Path latest = logs.resolve("latest.log");
            if (Files.exists(latest)) {
                log.addFileContent(latest);
            }
            complete = true;
        } catch (IOException ex) {
            Fk.getInstance().getLogger().log(Level.SEVERE, "An error occurred while reading the plugin folder", ex);
        }

        if (complete) {
            try {
                complete = upload(log, username);
            } catch (IOException | URISyntaxException ex) {
                Fk.getInstance().getLogger().log(Level.SEVERE, "Unable to upload the log", ex);
                complete = false;
            }
        }

        return complete;
    }

    private static boolean upload(@NotNull PluginLog log, String username) throws IOException, URISyntaxException {
        final String boundary = Long.toHexString(System.currentTimeMillis());
        final URL url = new URI("https://fklogs.etrenak.ovh/new").toURL();
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String content = "--" + boundary + "\n" +
                    "Content-Disposition: form-data; name=\"username\"\n" +
                    "Content-Type: text/plain; charset=UTF-8\n" +
                    "\n" +
                    username + "\n" +
                    "--" + boundary + "\n" +
                    "Content-Disposition: form-data; name=\"file\"; filename=\"log.txt\"\n" +
                    "Content-Type: text/plain; charset=UTF-8\n" +
                    "\n" +
                    log.getContent() + "\n" +
                    "--" + boundary + "--";
            byte[] out = content.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            connection.setFixedLengthStreamingMode(length);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("User-Agent", USER_AGENT + '/' + Fk.getInstance().getDescription().getVersion());
            connection.connect();
            try (OutputStream os = connection.getOutputStream()) {
                os.write(out);
            }

            return connection.getResponseCode() == 200 || connection.getResponseCode() == 201;
        } finally {
            connection.disconnect();
        }
    }

    private static @NotNull String getRawServerSoftwareName() {
        if (classExists("io.papermc.paper.threadedregions.scheduler.AsyncScheduler")) {
            return "Folia";
        } else if (classExists("com.destroystokyo.paper.PaperConfig") || classExists("io.papermc.paper.configuration.Configuration")) {
            return "Paper";
        } else if (classExists("org.spigotmc.SpigotConfig")) {
            return "Spigot";
        } else {
            return "CraftBukkit";
        }
    }

    private static @NotNull String getServerSoftwareName() {
        final String deducedName = getRawServerSoftwareName();
        if (Bukkit.getVersion().contains(deducedName)) {
            return deducedName;
        } else {
            return deducedName + " fork";
        }
    }
}
