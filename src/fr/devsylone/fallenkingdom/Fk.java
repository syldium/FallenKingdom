package fr.devsylone.fallenkingdom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipOutputStream;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitRunnable;

import fr.devsylone.fallenkingdom.commands.FkAsyncCommandExecutor;
import fr.devsylone.fallenkingdom.commands.FkAsyncRegisteredCommandExecutor;
import fr.devsylone.fallenkingdom.commands.FkCommandExecutor;
import fr.devsylone.fallenkingdom.commands.brigadier.BrigadierSpigotManager;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.manager.CommandManager;
import fr.devsylone.fallenkingdom.manager.LanguageManager;
import fr.devsylone.fallenkingdom.manager.ListenersManager;
import fr.devsylone.fallenkingdom.manager.SaveablesManager;
import fr.devsylone.fallenkingdom.manager.TipsManager;
import fr.devsylone.fallenkingdom.manager.WorldManager;
import fr.devsylone.fallenkingdom.manager.packets.PacketManager;
import fr.devsylone.fallenkingdom.manager.packets.PacketManager1_13;
import fr.devsylone.fallenkingdom.manager.packets.PacketManager1_14;
import fr.devsylone.fallenkingdom.manager.packets.PacketManager1_16;
import fr.devsylone.fallenkingdom.manager.packets.PacketManager1_8;
import fr.devsylone.fallenkingdom.manager.packets.PacketManager1_9;
import fr.devsylone.fallenkingdom.manager.saveable.DeepPauseManager;
import fr.devsylone.fallenkingdom.manager.saveable.PlayerManager;
import fr.devsylone.fallenkingdom.manager.saveable.PortalsManager;
import fr.devsylone.fallenkingdom.manager.saveable.ScoreboardManager;
import fr.devsylone.fallenkingdom.manager.saveable.StarterInventoryManager;
import fr.devsylone.fallenkingdom.pause.PauseRestorer;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolderExpansion;
import fr.devsylone.fallenkingdom.updater.PluginUpdater;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.DebuggerUtils;
import fr.devsylone.fallenkingdom.utils.FkSound;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fallenkingdom.utils.ZipUtils;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.Team;
import lombok.Getter;

@Getter
public class Fk extends JavaPlugin
{
	@Getter
	private static boolean debugMode;

	protected Game game;
	protected CommandManager commandManager;
	protected PlayerManager playerManager;
	protected WorldManager worldManager;
	protected PauseRestorer pauseRestorer;
	protected StarterInventoryManager starterInventoryManager;
	protected ScoreboardManager scoreboardManager;
	protected PacketManager packetManager;
	protected DeepPauseManager deepPauseManager;
	protected TipsManager tipsManager;
	protected SaveablesManager saveableManager;
	protected PortalsManager portalsManager;
	protected LanguageManager languageManager;

	@Getter
	protected static Fk instance;

	protected FkPI fkPI;

	private final List<String> onConnectWarnings = new ArrayList<>();
	private String pluginError = "";

	private String previousVersion = getDescription().getVersion();

	public Fk()
	{
		instance = this;
	}

	// Test only
	public Fk(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
		super(loader, description, dataFolder, file);
	}

	@Override
	public void onEnable()
	{
		try
		{
			/*
			 * Mode debug
			 */
			debugMode = DebuggerUtils.getServerFolderName().endsWith("-debug") || new File(getDataFolder(), "debug").exists();
			if(debugMode)
			{
				debug("##########################");
				debug("STARTED IN DEBUG MODE");
				debug("##########################");
			}
		}catch(Exception e)
		{
			debugMode = false;
		}

		/*
		 * Random
		 */

		if(!getDataFolder().exists())
			getDataFolder().mkdir();

		ListenersManager.registerListeners(this);
		if (!check())
			return;

		languageManager = new LanguageManager();
		languageManager.init(this);

		/*
		 * FkPI
		 */

		fkPI = new FkPI();

		/*
		 * command /fk
		 */

		PluginCommand command = Objects.requireNonNull(getCommand("fk"), "Unable to register /fk command");
		if (Version.isAsyncTabCompleteSupported())
			if (Version.isAsyncPlayerSendCommandsEventSupported())
				this.commandManager = new FkAsyncRegisteredCommandExecutor(this, command);
			else
				this.commandManager = new FkAsyncCommandExecutor(this, command);
		else
			this.commandManager = new FkCommandExecutor(this, command);

		if (Version.isBrigadierSupported() && !Version.isAsyncPlayerSendCommandsEventSupported())
			new BrigadierSpigotManager<>(this).register(this.commandManager, command);

		/*
		 * MANAGER
		 */
		playerManager = new PlayerManager();
		pauseRestorer = new PauseRestorer();
		starterInventoryManager = new StarterInventoryManager();
		scoreboardManager = new ScoreboardManager();
		worldManager = new WorldManager(this);
		packetManager = initPacketManager();
		deepPauseManager = new DeepPauseManager();
		tipsManager = new TipsManager();
		tipsManager.startBroadcasts();
		portalsManager = new PortalsManager();

		game = new Game();

		saveableManager = new SaveablesManager(this);

		/*
		 * Update & load
		 */

		if(!saveableManager.getFileConfiguration("save.yml").contains("last_version"))
			saveableManager.getFileConfiguration("save.yml").set("last_version", "2.5.0");

		previousVersion = saveableManager.getFileConfiguration("save.yml").getString("last_version");

		try
		{
			saveableManager.loadAll();
		}catch(Exception ex)
		{
			onConnectWarnings.add("§cVotre configuration était corrompue ou invalide, elle a donc été sauvegardée puis supprimée. Désolé :S");
			File zip = new File(getDataFolder(), "invalid-" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(Calendar.getInstance().getTimeInMillis()) + ".zip");
			ZipOutputStream outputStream;
			try
			{
				zip.createNewFile();
				outputStream = new ZipOutputStream(new FileOutputStream(zip));
				ZipUtils.zipFile(getDataFolder(), "FallenKingdom", outputStream, false);
				outputStream.flush();
				outputStream.close();
			} catch(IOException e1)
			{
				e1.printStackTrace();
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

		File conf = new File(getDataFolder(), "config.yml");
		if(conf.length() == 0L)
			conf.delete();
		saveDefaultConfig();

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
			new PlaceHolderExpansion().register();

		/*
		 * IF EternalDay
		 */
		if(fkPI.getRulesManager().getRulesList().containsKey(Rule.ETERNAL_DAY) && fkPI.getRulesManager().getRule(Rule.ETERNAL_DAY))
			for(World w : Bukkit.getWorlds())
			{
				if(!Fk.getInstance().getWorldManager().isAffected(w))
					continue;
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

		if(game.getState().equals(Game.GameState.PAUSE))
		{
			getDeepPauseManager().unprotectItems();
			getDeepPauseManager().resetAIs();
		}

		scoreboardManager.removeAllScoreboards();

		for(FkPlayer p : getPlayerManager().getConnectedPlayers())
			p.getScoreboard().remove();
	}

	public static void broadcast(String message, String prefix, FkSound sound)
	{
		if (message == null || message.isEmpty()) {
			return;
		}
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
		if(debugMode)
		{
			if(!Fk.getInstance().isEnabled())
				Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> Bukkit.broadcastMessage(ChatUtils.DEBUG + (message == null ? "null" : message.toString())));
			else
				Bukkit.broadcastMessage(ChatUtils.DEBUG + (message == null ? "null" : message.toString()));
		}

	}

	public void addError(String s)
	{
		pluginError = s;
	}

	public void reset()
	{
		Bukkit.getScheduler().cancelTasks(instance);
		fkPI.reset();
		game = new Game();

		for(FkPlayer p : getPlayerManager().getConnectedPlayers())
			p.getScoreboard().remove();

		playerManager = new PlayerManager();
		portalsManager = new PortalsManager();
		deepPauseManager.unprotectItems();
		deepPauseManager.resetAIs();

		// Reset saveFile & Restorer

		saveableManager.reset();

		pauseRestorer = new PauseRestorer();

		// Scoreboards
		scoreboardManager = new ScoreboardManager(); //Le recréer pour le réinitialiser

		getScoreboardManager().recreateAllScoreboards();

		saveableManager = new SaveablesManager(this); // En dernier
	}

	public void stop()
	{
		Bukkit.getScheduler().cancelTasks(instance);

		tipsManager.cancelBroadcasts();
		tipsManager.startBroadcasts();

		game.stop();
		deepPauseManager.resetAIs();
		deepPauseManager.unprotectItems();

		for(FkPlayer p : getPlayerManager().getConnectedPlayers())
		{
			p.clearDeaths();
			p.clearKills();
		}

		for(Team team : fkPI.getTeamManager().getTeams())
		{
			if(team.getBase() != null)
				team.getBase().resetChestRoom();
		}
		getScoreboardManager().recreateAllScoreboards();
	}

	private boolean check()
	{
		List<String> warns = new ArrayList<>();

		if(getConfig().get("Charged_creepers") != null)
			warns.add("L'option Charged_creepers dans le fichier de configuration n'est plus utilisée, il faut utiliser /fk rules ChargedCreepers");

		if(!Version.hasSpigotApi())
			addError("Le serveur n'est pas supporté par le plugin. Seuls les serveurs basés sur Spigot sont supportés.");

		if(Version.isTooOldApi())
			addError("La version du serveur n'est pas compatible avec le plugin,\nmerci d'utiliser au minimum la version §l§n1.8.3 de Spigot.");

		for(String warn : warns)
		{
			getLogger().warning("------------------------------------------");
			getLogger().warning(warn);
			getLogger().warning("------------------------------------------");

			onConnectWarnings.add(warn);
		}
		return warns.isEmpty();
	}

	public PacketManager initPacketManager() {
		switch (Version.VERSION_TYPE) {
			case V1_8:
				return new PacketManager1_8();
			case V1_9_V1_12:
				return new PacketManager1_9();
			case V1_13:
				return new PacketManager1_13();
			case V1_14_V1_15:
				return new PacketManager1_14();
			case V1_16:
				return new PacketManager1_16();
			default:
				throw new RuntimeException("Could not get packet manager by version!");
		}
	}

	private void metrics() throws NoClassDefFoundError // gson en 1.8.0
	{
		Metrics metrics = new Metrics(this, 6738);
		metrics.addCustomChart(new Metrics.SingleLineChart("server_running_1-8_version", () -> Bukkit.getVersion().contains("1.8") ? 1 : 0));
		metrics.addCustomChart(new Metrics.SimplePie("lang_used", languageManager::getLocalePrefix));
	}

	public void addOnConnectWarning(String warning)
	{
		onConnectWarnings.add(warning);
	}
}
