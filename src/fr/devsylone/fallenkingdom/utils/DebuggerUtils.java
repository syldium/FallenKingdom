package fr.devsylone.fallenkingdom.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

public class DebuggerUtils
{
    private static PrintWriter writer;

    public static String getLastLineStaskTrace(Thread t)
    {
        for(StackTraceElement element : t.getStackTrace())
            if(element.getClassName() != DebuggerUtils.class.getName() && !element.getClassName().contains("Thread"))
            {
                return element.toString();
            }
        return "";
    }

    public static String getStackTrace(Thread t)
    {
        return getStackTrace(t.getStackTrace());
    }

    public static String getStackTrace(Throwable throwable)
    {
        return getStackTrace(throwable.getStackTrace());
    }

    public static String getStackTrace(StackTraceElement[] elements)
    {
        String totalStackTrace = "";

        for(StackTraceElement element : elements)
        {
            if(element.getClassName() == DebuggerUtils.class.getName() || element.getClassName().contains("Thread"))
                totalStackTrace = "Current trace : \n";

            else if(!element.getClassName().contains("devsylone"))
            {
                totalStackTrace += "And more...";
                break;
            }

            else
                totalStackTrace += " |- " + element.toString() + "\n";
        }
        return totalStackTrace;
    }

    public static void printCurrentStackTrace()
    {
        Fk.debug(getStackTrace(Thread.currentThread()));
    }

    public static String getServerFolderName()
    {
        String path = Fk.getInstance().getDataFolder().getAbsolutePath();
        String serverName;
        try
        {
            String[] folders = path.split(File.separator);
            serverName = folders[folders.length - 3];
        }catch(Exception e)
        {
            serverName = "serverErr";
        }
        return serverName;
    }

    public static void log(String msg)
    {
        System.out.println(msg);
        if(writer != null)
            writer.println(msg);
    }

    public static boolean debugGame(boolean send, String username)
    {
        HttpURLConnection newLogConnection = null;
        String boundary = Long.toHexString(System.currentTimeMillis());
        if(send)
        {
            try
            {
                newLogConnection = (HttpURLConnection) new URL("https://fklogs.etrenak.ovh/new").openConnection();
                newLogConnection.setDoOutput(true);
                newLogConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                writer = new PrintWriter(newLogConnection.getOutputStream());

            }catch(IOException e)
            {
                e.printStackTrace();
                return false;
            }

            writer.println("--" + boundary);
            writer.println("Content-Disposition: form-data; name=\"username\"");
            writer.println("Content-Type: text/plain; charset=UTF-8");
            writer.println();
            writer.println(username);

            writer.println("--" + boundary);
            writer.println("Content-Disposition: form-data; name=\"file\"; filename=\"whatever\"");
            writer.println("Content-Type: text/plain; charset=UTF-8");
            writer.println();
        }
        log("--------------------------------------");
        log("OS : " + System.getProperty("os.name"));
        log("Java version : " + System.getProperty("java.version"));
        if(Bukkit.getVersion().contains("Spigot"))
            log("Spigot version : " + Bukkit.getBukkitVersion() + " | " + Bukkit.getVersion());
        else if(Bukkit.getVersion().contains("Paper"))
            log("Paper version : " + Bukkit.getBukkitVersion() + " | " + Bukkit.getVersion());
        else
            log("CraftBukkit version : " + Bukkit.getBukkitVersion() + " | " + Bukkit.getVersion());
        log("Plugin version : v" + Fk.getInstance().getDescription().getVersion());
        log("---- Commands since reload ----");
        for(Map.Entry<String, Boolean> cmd : FkCommandExecutor.logs.entrySet())
            log("  > " + cmd.getKey() + (cmd.getValue() ? "" : "  [Error occurred]"));
        log("---- Rules ----");
        for(Map.Entry<Rule<?>, Object> rule : Fk.getInstance().getFkPI().getRulesManager().getRulesList().entrySet())
            log("  > " + rule.getKey().getName() + ": " + rule.getValue().toString());
        log("---- Game ---");
        log("  > State: " + Fk.getInstance().getGame().getState());
        log("  > Day: " + Fk.getInstance().getGame().getDay());
        log("  > Time: " + Fk.getInstance().getGame().getTime());
        log("  > Nether: " + Fk.getInstance().getGame().isNetherEnabled());
        log("  > PvP: " + Fk.getInstance().getGame().isPvpEnabled());
        log("  > End: " + Fk.getInstance().getGame().isEndEnabled());
        log("  > Assaults: " + Fk.getInstance().getGame().isAssaultsEnabled());
        log("---- Teams ---");
        for(Team team : FkPI.getInstance().getTeamManager().getTeams())
        {
            Location loc = team.getBase() == null ? null : team.getBase().getCenter();
            log("  > " + team.getName() + ": " + (loc == null ? '/' : "(" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ") - " + team.getBase().getRadius()));
        }
        log("---- Chests ---");
        for(LockedChest chest : Fk.getInstance().getFkPI().getLockedChestsManager().getChests())
            log("  > " + chest.toString());
        log("---- Plugins ---");
        log("  > " + Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Plugin::getName).collect(Collectors.joining(", ")));

        if(send)
        {
            try
            {
                List<File> toDebug = new ArrayList<>(Arrays.asList(Objects.requireNonNull(Fk.getInstance().getDataFolder().listFiles(), "Unable to access plugin files"))).stream().filter(file -> file.getName().endsWith("yml")).collect(Collectors.toList());
                toDebug.add(new File("logs/latest.log"));
                for(File file : toDebug)
                {
                    writer.println();
                    writer.println();
                    writer.println();
                    writer.println("----------------------------------------------------------");
                    writer.println("           " + file.getName());
                    writer.println("----------------------------------------------------------");
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line = null;
                    while((line = reader.readLine()) != null)
                        writer.println(line);
                    reader.close();

                }

                writer.flush();
                writer.println();
                writer.println("--" + boundary);
                writer.flush();
                return newLogConnection.getResponseCode() == 200;
            }catch(IOException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}