package fr.devsylone.fallenkingdom.utils;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class UpdateUtils
{
	public static void deleteUpdater(String path)
	{
		for(Plugin p : Bukkit.getPluginManager().getPlugins())
		{
			if(p.getName().equalsIgnoreCase("FkUpdater"))
			{
				Bukkit.getPluginManager().disablePlugin(p);
				break;
			}
		}
		new File(path).delete();
		
		Bukkit.getConsoleSender().sendMessage("[Updater] §4§lRestart du serveur");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
	}
}
