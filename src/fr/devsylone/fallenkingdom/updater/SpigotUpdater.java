package fr.devsylone.fallenkingdom.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;

public class SpigotUpdater extends Thread

{
	private final Plugin plugin;
	private boolean enabled = true;
	private URL url;

	public SpigotUpdater(Plugin plugin) throws MalformedURLException
	{
		if(plugin == null)
		{
			throw new FkLightException("Plugin cannot be null");
		}

		this.plugin = plugin;
		this.url = new URL("https://servermods.forgesvc.net/servermods/files?projectIds=276763");

		super.start();
	}

	public synchronized void start()
	{}

	public void run()
	{
		if(!this.plugin.isEnabled())
		{
			return;
		}
		if(!this.enabled)
		{
			return;
		}

		HttpURLConnection connection = null;
		try
		{
			connection = (HttpURLConnection) this.url.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			connection.setRequestMethod("GET");

			InputStreamReader reader = new InputStreamReader(connection.getInputStream());

			JsonArray jsonArray = null;
			try
			{
				jsonArray = new JsonParser().parse(reader).getAsJsonArray();
			}catch(JsonSyntaxException e)
			{}
			String currentVersion = null;
			if(jsonArray != null)
			{
				JsonObject latestFile = jsonArray.get(jsonArray.size() - 1).getAsJsonObject();
				String version = latestFile.get("name").getAsString().split("-")[1];

				if(version != null && !version.isEmpty())
				{
					currentVersion = version;
				}
			}
			if(currentVersion == null)
			{
				this.plugin.getLogger().warning("[Updater] Réponse invalide.");
				this.plugin.getLogger().warning("[Updater] Une erreur est survenue lors de la recherche de mise à jour.");
				return;
			}
			else if(FilesUpdater.isGrowing(plugin.getDescription().getVersion(), currentVersion))
			{
				this.plugin.getLogger().info("[Updater] Une nouvelle version est disponible : " + currentVersion + " ! (Version actuelle : " + this.plugin.getDescription().getVersion() + ")");
				this.plugin.getLogger().info("[Updater] Téléchargement de la nouvelle version...");
				try
				{
					HttpURLConnection check = (HttpURLConnection) new URL("http://fkdevsylone.000webhostapp.com/FK/auto-update/check").openConnection();
					Scanner sc = new Scanner(check.getInputStream());
					String resp = sc.nextLine();
					sc.close();
					if(!resp.equals("ok"))
						throw new FkLightException("Pas ok : " + resp);

					URL website = new URL("http://fkdevsylone.000webhostapp.com/FK/auto-update/version/latest");
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					final File latest = new File(Fk.getInstance().getDataFolder().getParent() + "/FallenKingdom-" + currentVersion + ".jar");
					FileOutputStream fos = new FileOutputStream(latest);
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();

					this.plugin.getLogger().info("[Updater] Version téléchargée");
					this.plugin.getLogger().info("[Updater] Téléchargement de l'updater...");

					website = new URL("http://fkdevsylone.000webhostapp.com/FK/auto-update/version/updater");
					rbc = Channels.newChannel(website.openStream());
					final File updater = new File(Fk.getInstance().getDataFolder().getParent() + "/FkUpdater.jar");
					fos = new FileOutputStream(updater);
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
					this.plugin.getLogger().info("[Updater] Updater téléchargé");
					this.plugin.getLogger().info("[Updater] Mise à jour...");

					Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
					{

						@Override
						public void run()
						{
							try
							{
								Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().loadPlugin(updater));
							}catch(UnknownDependencyException | InvalidPluginException | InvalidDescriptionException e)
							{
								e.printStackTrace();
							}
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fkupdate " + Fk.class.getProtectionDomain().getCodeSource().getLocation().getPath() + " " + latest.getAbsolutePath());

						}
					});

				}catch(Exception e)
				{
					this.plugin.getLogger().info("[Updater] Échec, veuillez la télécharger manuellement ici : http://www.spigotmc.org/resources/38878");
					Fk.getInstance().addOnConnectWarning("Une nouvelle version est diponible : http://www.spigotmc.org/resources/38878");
					//TODO REMOVE
					e.printStackTrace();
				}

			}

		}catch(IOException e)
		{
			if(connection != null)
			{
				try
				{
					int code = connection.getResponseCode();
					this.plugin.getLogger().warning("[Updater] Une erreur est survenue. Code d'erreur : " + code);
				}catch(IOException e1)
				{}
			}
			e.printStackTrace();
		}
	}
}
