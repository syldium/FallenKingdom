package fr.devsylone.fallenkingdom.utils;

import java.io.File;
import java.util.Map;

import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.rules.RuleValue;
import org.bukkit.Bukkit;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.lockedchests.LockedChest;

public class DebuggerUtils
{

	public static String getLastLineStackTrace(Thread t)
	{
		for(StackTraceElement element : t.getStackTrace())
			if(!element.getClassName().equals(DebuggerUtils.class.getName()) && !element.getClassName().contains("Thread"))
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
		StringBuilder totalStackTrace = new StringBuilder();

		for(StackTraceElement element : elements)
		{
			if(element.getClassName().equals(DebuggerUtils.class.getName()) || element.getClassName().contains("Thread"))
				totalStackTrace.append("Current trace : \n");

			else if(!element.getClassName().contains("devsylone"))
			{
				totalStackTrace.append("And more...");
				break;
			}

			else
				totalStackTrace.append(" |- ").append(element.toString()).append("\n");
		}
		return totalStackTrace.toString();
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
	}

	public static void debugGame()
	{
		log("--------------------------------------");
		log("OS : " + System.getProperty("os.name"));
		log("Java version : " + System.getProperty("java.version"));
		if(Bukkit.getVersion().contains("Spigot"))
			log("Spigot version : " + Bukkit.getBukkitVersion() + " | " + Bukkit.getVersion());
		else
			log("CraftBukkit version : " + Bukkit.getBukkitVersion() + " | " + Bukkit.getVersion());
		log("Plugin version : v" + Fk.getInstance().getDescription().getVersion());
		log("Latest save: " + (Fk.getInstance().getSaveableManager().getLastSave() > 0 ? (System.currentTimeMillis() - Fk.getInstance().getSaveableManager().getLastSave())/1000 + "s" : "unknown"));
		log("---- Commandes depuis reload ----");
		/*if(FkCommandExecutor.logs != null)
			for(String cmdfor : FkCommandExecutor.logs.keySet())
				log("  > " + cmdfor + (FkCommandExecutor.logs.get(cmdfor) ? "" : "  [Error occured]"));*/
		/*else
			log("Les logs étaient non-initialisés");*/
		log("---- Rules ----");
		for(Map.Entry<Rule<?>, Object> rule : Fk.getInstance().getFkPI().getRulesManager().getRulesList().entrySet())
		{
			String value = rule.getValue() instanceof RuleValue ? ((RuleValue) rule.getValue()).format() : String.valueOf(rule.getValue());
			log("  > " + rule.getKey().getName() + ": " + value);
		}
		log("---- Game ---");
		log("  > State: " + Fk.getInstance().getGame().getState());
		log("  > Day: " + Fk.getInstance().getGame().getDays());
		log("  > Time: " + Fk.getInstance().getGame().getFormattedTime());
		log("---- Chests ---");
		for(LockedChest chest : Fk.getInstance().getFkPI().getLockedChestsManager().getChestList())
			log("  > " + chest.toString());
	}

}
