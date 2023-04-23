package fr.devsylone.fallenkingdom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import fr.devsylone.fallenkingdom.display.GlobalDisplayService;
import fr.devsylone.fallenkingdom.updater.GitHubAssetInfo;
import fr.devsylone.fallenkingdom.updater.UpdateChecker;
import fr.devsylone.fallenkingdom.utils.FkConfig;
import fr.devsylone.fallenkingdom.version.LuckPermsContext;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
import fr.devsylone.fallenkingdom.manager.saveable.DeepPauseManager;
import fr.devsylone.fallenkingdom.manager.saveable.PlayerManager;
import fr.devsylone.fallenkingdom.manager.saveable.PortalsManager;
import fr.devsylone.fallenkingdom.manager.saveable.ScoreboardManager;
import fr.devsylone.fallenkingdom.manager.saveable.StarterInventoryManager;
import fr.devsylone.fallenkingdom.pause.PauseRestorer;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolderExpansion;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.FkSound;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.Team;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

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
	protected GlobalDisplayService displayService;
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

	@Override
	public void onEnable() {
		try {
			this.onEnable0();
		} catch (Throwable throwable) {
			this.pluginError = throwable.getMessage();
			throw throwable;
		}
	}

	public void onEnable0()
	{
		try
		{
			/*
			 * Mode debug
			 */
			debugMode = new File(getDataFolder(), "debug").exists();
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

		languageManager = new LanguageManager();
		languageManager.init(this);

		if (!check())
			return;

		worldManager = new WorldManager(this);

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
			new BrigadierSpigotManager<>(this, this.commandManager, command);

		/*
		 * MANAGER
		 */
		displayService = new GlobalDisplayService();
		playerManager = new PlayerManager(displayService);
		pauseRestorer = new PauseRestorer();
		starterInventoryManager = new StarterInventoryManager();
		scoreboardManager = new ScoreboardManager();
		//packetManager = initPacketManager();
		deepPauseManager = new DeepPauseManager();
		tipsManager = new TipsManager();
		tipsManager.startBroadcasts();
		portalsManager = new PortalsManager();

		game = new Game();

		saveableManager = new SaveablesManager(this);
		saveableManager.update();

		/*
		 * Update & load
		 */

		if(!saveableManager.getFileConfiguration("save.yml").contains("last_version"))
			saveableManager.getFileConfiguration("save.yml").set("last_version", "2.5.0");

		previousVersion = saveableManager.getFileConfiguration("save.yml").getString("last_version");

		saveableManager.loadAll();
		game.updateDayDuration(displayService);

		saveDefaultConfig();

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
			new PlaceHolderExpansion().register();

		try {
			Class.forName("net.luckperms.api.context.ContextCalculator");
			new LuckPermsContext(this);
		} catch (ClassNotFoundException ignored) {
			// Not installed...
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}

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

		if (fkPI.getRulesManager().getRule(Rule.HEALTH_BELOW_NAME)) {
			fkPI.getTeamManager().nametag().createHealthObjective();
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

		UpdateChecker updater = new UpdateChecker(this);
		if (updater.getCurrentVersion().isRelease()) {
			updater.runTaskAsynchronously(this);
		}

		getServer().getScheduler().runTaskTimer(this, saveableManager::delayedSaveAll, 5L * 60L * 20L, 5L * 60L * 20L);
	}

	@Override
	public void onDisable()
	{
		for (Player player : this.getServer().getOnlinePlayers()) {
			final FkPlayer fkPlayer = this.playerManager.getPlayerIfExist(player);
			if (fkPlayer != null) {
				this.displayService.hide(player, fkPlayer);
			}
		}
		this.fkPI.teardown();

		this.saveableManager.delayedSaveAll();
		FkConfig.awaitSaveEnd();

		if (this.game.isPaused()) {
			getDeepPauseManager().unprotectItems();
			getDeepPauseManager().resetAIs();
		}
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

		playerManager = new PlayerManager(displayService);
		portalsManager = new PortalsManager();
		deepPauseManager.unprotectItems();
		deepPauseManager.resetAIs();

		// Reset saveFile & Restorer

		saveableManager.reset();

		pauseRestorer = new PauseRestorer();

		displayService.loadNullable(null);
		displayService.updateAll();

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

		for(FkPlayer p : getPlayerManager().players())
		{
			p.clearDeaths();
			p.clearKills();
		}

		for(Team team : fkPI.getTeamManager().getTeams())
		{
			if(team.getBase() != null)
				team.getBase().resetChestRoom();
		}
		displayService.hideAll();
		displayService.updateAll();
	}

	private boolean check()
	{
		if(getConfig().get("Charged_creepers") != null)
			addError(Messages.CONSOLE_CHARGED_CREEPERS_NOT_USE.getMessage());

		if(!Version.hasSpigotApi())
			addError("Le serveur n'est pas supporté par le plugin. Seuls les serveurs basés sur Spigot sont supportés.\nThe server is not supported by the plugin. Only Spigot based servers are supported.\nDer Server wird vom Plugin nicht unterstützt. Es werden nur Spigot-basierte Server unterstützt.");

		if(Version.isTooOldApi())
			addError("§rLa version du serveur n'est pas compatible avec le plugin,\nmerci d'utiliser au minimum la version §l§n1.8.3 de Spigot.\n\n§rThe server version isn't compatible with the plugin,\nplease use at least the §l§n1.8.3 version of Spigot.\n\nDie Version des Servers ist nicht mit dem Plugin kompatibel. Bitte benutzen Sie mindestens die §l§nSpigot-Version 1.8.3.");

		if (!pluginError.isEmpty())
		{
			getLogger().severe("------------------------------------------");
			getLogger().warning(pluginError);
			getLogger().severe("------------------------------------------");

			onConnectWarnings.add(pluginError);
		}
		return pluginError.isEmpty();
	}

	private void metrics() throws NoClassDefFoundError // gson en 1.8.0
	{
		Metrics metrics = new Metrics(this, 6738);
		metrics.addCustomChart(new SingleLineChart("server_running_1-8_version", () -> Bukkit.getVersion().contains("1.8") ? 1 : 0));
		metrics.addCustomChart(new SimplePie("lang_used", languageManager::getLocalePrefix));
	}

	public void addOnConnectWarning(String warning)
	{
		onConnectWarnings.add(warning);
	}

	public boolean updatePlugin(@NotNull GitHubAssetInfo assetInfo) {
		try (BufferedInputStream in = new BufferedInputStream(new URL(assetInfo.browserDownloadUrl()).openStream());
			 FileOutputStream fileOutputStream = new FileOutputStream(this.getDataFolder().getParentFile().getName() + '/' + assetInfo.name())) {
			byte[] dataBuffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}

			return this.getFile().delete();
		} catch (IOException ex) {
			this.getLogger().log(Level.SEVERE, "Unable to download the update.", ex);
			return false;
		}
	}

	public @NotNull Path getPluginFolder() {
		return this.getDataFolder().toPath();
	}

	public @NotNull Path getRunDir() {
		File pluginsDir = getDataFolder().getParentFile().getAbsoluteFile();
		File runDir = pluginsDir.getParentFile().getAbsoluteFile();
		return runDir.toPath();
	}
}
