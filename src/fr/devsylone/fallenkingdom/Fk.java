package fr.devsylone.fallenkingdom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.devsylone.fallenkingdom.commands.FkCommandExecutor;
import fr.devsylone.fallenkingdom.commands.FkTabCompleter;
import fr.devsylone.fallenkingdom.connection.CBServerSocket;
import fr.devsylone.fallenkingdom.connection.ServerSocket;
import fr.devsylone.fallenkingdom.connection.SpServerSocket;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.manager.CommandManager;
import fr.devsylone.fallenkingdom.manager.ListenersManager;
import fr.devsylone.fallenkingdom.manager.SaveablesManager;
import fr.devsylone.fallenkingdom.manager.TipsManager;
import fr.devsylone.fallenkingdom.manager.packets.PacketManager;
import fr.devsylone.fallenkingdom.manager.packets.PacketManager1_13;
import fr.devsylone.fallenkingdom.manager.packets.PacketManager1_14;
import fr.devsylone.fallenkingdom.manager.packets.PacketManager1_8;
import fr.devsylone.fallenkingdom.manager.packets.PacketManager1_9;
import fr.devsylone.fallenkingdom.manager.saveable.DeepPauseManager;
import fr.devsylone.fallenkingdom.manager.saveable.PlayerManager;
import fr.devsylone.fallenkingdom.manager.saveable.PortalsManager;
import fr.devsylone.fallenkingdom.manager.saveable.ScoreboardManager;
import fr.devsylone.fallenkingdom.manager.saveable.StarterInventoryManager;
import fr.devsylone.fallenkingdom.pause.PauseRestorer;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.updater.PluginUpdater;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.DebuggerUtils;
import fr.devsylone.fallenkingdom.utils.FkSound;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.ZipUtils;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.teams.Team;

public class Fk extends JavaPlugin
{
	private static boolean DEBUG_MODE;

	private Game game;
	private CommandManager cmdManager;
	private PlayerManager pManager;
	private PauseRestorer pRestorer;
	private StarterInventoryManager siManager;
	private ScoreboardManager sbManager;
	private PacketManager pcktManager;
	private DeepPauseManager dpManager;
	private TipsManager tipsManager;
	private SaveablesManager saveableManager;
	private PortalsManager portalManager;

	private ServerSocket server;

	private static Fk instance;

	private FkPI fkpi;

	private List<String> onConnectWarnings;
	private String pluginError = "";

	private String lastVersion;

	private boolean uptodate = true;

	private final boolean isNewVersion = NMSUtils.nmsOptionalClass("ScoreboardServer$Action").isPresent();

	public static Fk getInstance()
	{
		return instance;
	}

	public Fk()
	{
		instance = this;
		onConnectWarnings = new ArrayList<String>();
		lastVersion = getDescription().getVersion();
	}

	@Override
	public void onEnable()
	{
		check();

		try
		{
			/*
			 * Mode debug
			 */
			DEBUG_MODE = DebuggerUtils.getServerFolderName().endsWith("-debug") || new File(getDataFolder(), "debug").exists();
			if(DEBUG_MODE)
			{
				debug("##########################");
				debug("STARTED IN DEBUG MODE");
				debug("##########################");
			}
		}catch(Exception e)
		{
			DEBUG_MODE = false;
		}

		/*
		 * Random
		 */

		if(!getDataFolder().exists())
			getDataFolder().mkdir();

		/*
		 * FkPI
		 */

		fkpi = new FkPI(this);

		/*
		 * MANAGER
		 */

		cmdManager = new CommandManager();
		cmdManager.registerCommands();
		pManager = new PlayerManager();
		pRestorer = new PauseRestorer();
		siManager = new StarterInventoryManager();
		sbManager = new ScoreboardManager();

		if(Bukkit.getBukkitVersion().contains("1.8"))
			pcktManager = new PacketManager1_8();
		else if(isNewVersion)
			if(Bukkit.getBukkitVersion().contains("1.13"))
				pcktManager = new PacketManager1_13();
			else
				pcktManager = new PacketManager1_14();
		else
			pcktManager = new PacketManager1_9();

		dpManager = new DeepPauseManager();
		tipsManager = new TipsManager();
		tipsManager.startBroadcasts();
		portalManager = new PortalsManager();

		game = new Game();

		saveableManager = new SaveablesManager(this);

		/*
		 * Update & load
		 */

		if(!saveableManager.getFileConfiguration("save.yml").contains("last_version"))
			saveableManager.getFileConfiguration("save.yml").set("last_version", "2.5.0");

		lastVersion = saveableManager.getFileConfiguration("save.yml").getString("last_version");

		try
		{
			saveableManager.loadAll();
		}catch(Exception ex)
		{
			addOnConnectWarning("Votre configuration était corrompue ou invalide, elle a donc été sauvegardée puis supprimée. Désolé :S");
			File zip = new File(getDataFolder(), "invalid-" + new SimpleDateFormat("YYYY-MM-dd HH-mm-ss").format(Calendar.getInstance().getTimeInMillis()) + ".zip");
			ZipOutputStream outputStream;
			try
			{
				zip.createNewFile();
				outputStream = new ZipOutputStream(new FileOutputStream(zip));
				ZipUtils.zipFile(getDataFolder(), "FallenKingdom", outputStream, false);
				outputStream.flush();
				outputStream.close();
			}catch(FileNotFoundException e1)
			{
				e1.printStackTrace();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
			for(File f : getDataFolder().listFiles())
				if(f.getName().endsWith(".yml"))
					f.delete();
			saveableManager = new SaveablesManager(this);
			saveableManager.loadAll();
			ex.printStackTrace();
		}

		/*
		 * ServerSocket & load du config.yml
		 */

		try
		{
			File conf = new File(getDataFolder(), "config.yml");
			if(conf.length() == 0L)
				conf.delete();
			if(!conf.exists())
			{
				Files.copy(getClass().getClassLoader().getResourceAsStream("config.yml"), conf.toPath());
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}

		if(getConfig().getBoolean("Application.Enabled"))
		{
			if(Bukkit.getVersion().contains("Spigot"))
				server = new SpServerSocket();
			else
				server = new CBServerSocket();
			server.start();
		}

		/*
		 * command /fk, events et le reste
		 */

		getCommand("Fk").setExecutor(new FkCommandExecutor());
		getCommand("Fk").setTabCompleter(new FkTabCompleter());

		ListenersManager.registerListeners(this);

		/*
		 * Set le sb a tout le monde si jamais rl
		 */
		for(Player p : Bukkit.getOnlinePlayers())
			pManager.registerNewPlayer(pManager.getPlayer(p));

		/*
		 * IF EternalDay
		 */
		if((Boolean) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("EternalDay").getValue())
			for(World w : Bukkit.getWorlds())
			{
				w.setGameRuleValue("doDaylightCycle", "false");
				w.setTime(6000L);
			}

		/*
		 * Metrics
		 */
		try {
			metrics();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
		}

		/*
		 * Updater
		 */

        PluginUpdater updater = new PluginUpdater(Fk.getInstance());
        updater.runTaskAsynchronously(this);

		new BukkitRunnable() {
			@Override
			public void run() {
				saveableManager.saveAll();
			}
		}.runTaskTimerAsynchronously(this, 5L * 60L * 20L, 5L * 60L * 20L);
	}

	@Override
	public void onDisable()
	{
		saveableManager.saveAll();

		// Même si la partie est en pause et est sauvegardée dans cet état,
		// on essaye de remettre dans un état normal, c'est-à-dire avec l'IA des mobs.
		// Au redémarrage, la commande "game pause" sera exécutée si cela a été enregistré.
		if(game.getState().equals(Game.GameState.PAUSE))
		{
			try
			{
				getCommandManager().getCommand("game resume").execute(null, null, new String[0]);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		if(server != null)
			server.interrupt();

		sbManager.removeAllScoreboards();

		for(FkPlayer p : getPlayerManager().getConnectedPlayers())
			p.getScoreboard().remove();
	}

	public static boolean isDebug()
	{
		return DEBUG_MODE;
	}

	public CommandManager getCommandManager()
	{
		return cmdManager;
	}

	public PlayerManager getPlayerManager()
	{
		return pManager;
	}

	public ScoreboardManager getScoreboardManager()
	{
		return sbManager;
	}

	public PacketManager getPacketManager()
	{
		return pcktManager;
	}

	public PauseRestorer getPauseRestorer()
	{
		return pRestorer;
	}

	public DeepPauseManager getDeepPauseManager()
	{
		return dpManager;
	}

	public StarterInventoryManager getStarterInventoryManager()
	{
		return siManager;
	}

	public TipsManager getTipsManager()
	{
		return tipsManager;
	}

	public SaveablesManager getSaveableManager()
	{
		return saveableManager;
	}

	public ServerSocket getServerSocket()
	{
		return server;
	}

	public PortalsManager getPortalsManager()
	{
		return portalManager;
	}

	public String getPreviousVersion()
	{
		return lastVersion;
	}

	public static void broadcast(String message, String prefix, FkSound sound)
	{
		message = "§r" + message;
		for(FkPlayer p : getInstance().getPlayerManager().getConnectedPlayers())
			p.sendMessage(message, prefix, sound);
	}

	public static void broadcast(String message, String prefix)
	{
		broadcast(message, prefix, null);
	}

	public static void broadcast(String message, FkSound sound)
	{
		broadcast(message, "", sound);
	}

	public static void broadcast(String message)
	{
		broadcast(message, "", null);
	}

	public static void debug(Object message)
	{
		//		if(message.toString().length() < 20)
		//			DebuggerUtils.printCurrentStackTrace();
		if(DEBUG_MODE)
		{
			if(!Fk.getInstance().isEnabled())
				Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> Bukkit.broadcastMessage(ChatUtils.DEBUG + (message == null ? "null" : message.toString())));
			else
				Bukkit.broadcastMessage(ChatUtils.DEBUG + (message == null ? "null" : message.toString()));
		}

	}

	public Game getGame()
	{
		return game;
	}

	public List<String> getOnConnectWarnings()
	{
		return onConnectWarnings;
	}

	public void setUpToDate(boolean arg)
	{
		uptodate = arg;
	}

	public boolean isUpToDate()
	{
		return uptodate;
	}

	public void addOnConnectWarning(String s)
	{
		onConnectWarnings.add(ChatUtils.PREFIX + ChatUtils.ALERT + "§4" + s);
	}

	public void addError(String s)
	{
		pluginError = s;
	}

	public String getError()
	{
		return pluginError;
	}

	public void reset()
	{
		Bukkit.getScheduler().cancelTasks(instance);
		fkpi.reset();
		game = new Game();

		for(FkPlayer p : getPlayerManager().getConnectedPlayers())
			p.getScoreboard().remove();

		pManager = new PlayerManager();
		portalManager = new PortalsManager();
		dpManager.unprotectItems();
		dpManager.resetAIs();

		// Reset saveFile & Restorer

		saveableManager.reset();

		pRestorer = new PauseRestorer();

		// Scoreboards
		sbManager = new ScoreboardManager(); //Le recréer pour le réinitialiser

		getScoreboardManager().recreateAllScoreboards();

		saveableManager = new SaveablesManager(this); // En dernier
	}

	public void stop()
	{
		Bukkit.getScheduler().cancelTasks(instance);

		tipsManager.cancelBroadcasts();
		tipsManager.startBroadcasts();

		game.stop();
		dpManager.resetAIs();
		dpManager.unprotectItems();

		for(FkPlayer p : getPlayerManager().getConnectedPlayers())
		{
			p.clearDeaths();
			p.clearKills();
		}

		for(Team team : fkpi.getTeamManager().getTeams())
		{
			if(team.getBase() != null)
				team.getBase().resetChestoom();
		}
		getScoreboardManager().recreateAllScoreboards();
	}

	private void check()
	{
		List<String> warns = new ArrayList<String>();

		if(getConfig().get("Charged_creepers") != null)
			warns.add("L'option Charged_creepers dans le fichier de configuration n'est plus utilisée, il faut utiliser /fk rules ChargedCreepers");

		if(Bukkit.getVersion().contains("Bukkit"))
			addError("Ce plugin n'est pas compatible avec CraftBukkit, veuillez utiliser spigot.");

		if(Bukkit.getVersion().contains("1.8") && getMinorVersionNumber() < 3)
			addError("Votre version de spigot n'est pas compatible avec le plugin,\nmerci d'utiliser au minimum la version §l§n1.8.3 de spigot");

		if(!System.getProperty("java.version").startsWith("1.8") && !System.getProperty("java.version").startsWith("11.") && !System.getProperty("java.version").startsWith("14"))
			addError("Votre version de java n'est pas compatible avec le plugin. Merci d'utiliser Java 8 ou LTS supérieure");

		for(String warn : warns)
		{
			getLogger().warning("------------------------------------------");
			getLogger().warning(warn);
			getLogger().warning("------------------------------------------");

			addOnConnectWarning(warn);
		}
	}

	public FkPI getFkPI()
	{
		return fkpi;
	}

	public boolean isNewVersion()
	{
		return isNewVersion;
	}

	private int getMinorVersionNumber()
	{
		int count = (int) Bukkit.getVersion().chars().filter(ch -> ch == '.').count();
		if (count > 1) {
			int minorVersionIndex = Bukkit.getVersion().lastIndexOf('.') + 1;
			return Integer.parseInt(Bukkit.getVersion().substring(minorVersionIndex, minorVersionIndex + 1));
		}
		return 0;
	}

	private void metrics() throws NoClassDefFoundError // gson en 1.8.0
	{
		Metrics metrics = new Metrics(this, 6738);
		metrics.addCustomChart(new Metrics.SingleLineChart("server_running_1-8_version", () -> Bukkit.getVersion().contains("1.8") ? 1 : 0));
	}

	public static void main(String[] args)
	{
		System.out.println("Ce fichier est à mettre dans votre dossier plugin et non pas à executer");
	}

}
