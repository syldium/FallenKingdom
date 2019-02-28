package fr.devsylone.fallenkingdom.utils;

import org.bukkit.Bukkit;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.FkCommandExecutor;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import fr.devsylone.fkpi.rules.Rule;

public class DebuggerUtils
{

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
			String[] folders = path.split("/");
			serverName = folders[folders.length - 3];
		}catch(Exception e)
		{
			serverName = "server";
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
		log("---- Comandes depuis reload ----");
		if(FkCommandExecutor.logs!=null)
			for(String cmdfor : FkCommandExecutor.logs.keySet())
				log("  > " + cmdfor + (((Boolean) FkCommandExecutor.logs.get(cmdfor)).booleanValue() ? "" : "  [Error occured]"));
		else
			log("Les logs étaient non-initialisés");
		log("---- Rules ----");
		for(Rule rule : Fk.getInstance().getFkPI().getRulesManager().getRulesList())
			log("  > " + rule.toString());
		log("---- Game ---");
		log("  > State: " + Fk.getInstance().getGame().getState());
		log("  > Day: " + Fk.getInstance().getGame().getDays());
		log("  > Time: " + Fk.getInstance().getGame().getFormattedTime());
		log("---- Chests ---");
		for(LockedChest chest : Fk.getInstance().getFkPI().getLockedChestsManager().getChestList())
			log("  > " + chest.toString());
	}

}
