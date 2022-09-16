package fr.devsylone.fallenkingdom.manager;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.display.GlobalDisplayService;
import fr.devsylone.fallenkingdom.updater.FilesUpdater;
import fr.devsylone.fallenkingdom.utils.FkConfig;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.ZipUtils;
import fr.devsylone.fkpi.util.Saveable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.ZipOutputStream;

/**
 * Gère la sauvegarde des différentes instances {@link Saveable}.
 *
 * <p>Si certaines méthodes passent sur d'autres threads, elles doivent toutes être appelées depuis le thread
 * principal.</p>
 */
public class SaveablesManager {

	private final Fk plugin;
	private final Map<String, FkConfig> files = new HashMap<>();
	private final Map<Saveable, FkConfig> saveables = new LinkedHashMap<>(); // L'ordre de chargement est important
	private FilesUpdater filesUpdater;
	private long lastSave = 0;

	public SaveablesManager(@NotNull Fk fk) {
		this.plugin = fk;

		FkConfig mainConfig = getFileConfiguration("save.yml");

		registerSaveable(fk.getFkPI(), "save.yml");
		registerSaveable(fk.getGame(), "save.yml");
		registerSaveable(fk.getPlayerManager(), "save.yml");
		registerSaveable(fk.getStarterInventoryManager(), "save.yml");

		registerSaveable(fk.getPauseRestorer(), "pause_restorer.yml");

		registerSaveable(fk.getDisplayService(), GlobalDisplayService.FILENAME);

		registerSaveable(fk.getDeepPauseManager(), "deep_pause.yml");

		registerSaveable(fk.getPortalsManager(), "portals.yml");

		mainConfig.load();
		this.filesUpdater = new FilesUpdater(mainConfig.getString("last_version", fk.getDescription().getVersion()));
		mainConfig.set("last_version", fk.getDescription().getVersion());
		mainConfig.saveSync();
	}

	public void update() {
		this.filesUpdater.update();
		this.filesUpdater = null;
	}

	/**
	 * Met à jour la configuration en mémoire, sérialise les données et passe sur un autre thread pour sauvegarder dans
	 * des fichiers.
	 *
	 * <p>Au niveau de {@link Plugin#onDisable()}, appeler {@link FkConfig#awaitSaveEnd()} permet d'éviter que les
	 * données soit écrites à moitié.</p>
	 */
	public void delayedSaveAll() {
		updateMemoryConfig();
		for (FkConfig config : files.values()) {
			config.delayedSave();
		}
	}

	/**
	 * Lit les fichiers depuis le système de fichiers et appelle {@link Saveable#loadNullable(ConfigurationSection)}.
	 *
	 * <p>Si un fichier pose problème, il est réinitialisé. Noter que les classes {@link Saveable} devraient vérifier en
	 * amont les données, pour capturer le plus tôt possible la moindre exception.</p>
	 */
	public void loadAll() {
		for (FkConfig config : files.values()) {
			config.load();
		}

		List<FkConfig> corrupted = new LinkedList<>();
		for (Map.Entry<Saveable, FkConfig> entry : saveables.entrySet()) {
			Saveable saveable = entry.getKey();
			FkConfig configFile = entry.getValue();
			try {
				saveable.loadNullable(
						configFile.contains(saveable.getClass().getSimpleName())
								? configFile.getConfigurationSection(saveable.getClass().getSimpleName())
								: configFile.createSection(saveable.getClass().getSimpleName())
				);
			} catch (Throwable throwable) {
				throwable.printStackTrace();
				corrupted.add(configFile);
			}
		}

		if (!corrupted.isEmpty()) {
			plugin.getOnConnectWarnings().add(Messages.CORRUPT_CONFIG_FILES.getMessage());
			File zip = new File(plugin.getDataFolder(), "invalid-" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(Calendar.getInstance().getTimeInMillis()) + ".zip");
			try {
				zip.createNewFile();

				try (FileOutputStream fileStream = new FileOutputStream(zip);
					 ZipOutputStream zipStream = new ZipOutputStream(fileStream)) {
					ZipUtils.zipConfig(plugin.getDataFolder().toPath(), zipStream);
					zipStream.flush();
				}

				for (FkConfig file : corrupted) {
					file.delete();
				}
			} catch (IOException e) {
				plugin.getLogger().log(Level.SEVERE, Messages.CONSOLE_UNABLE_TO_MAKE_BACKUP.getMessage(), e);
			}
		}

		plugin.getDisplayService().updateAll();
	}

	private void registerSaveable(Saveable o, String file) {
		saveables.put(o, getFileConfiguration(file));
	}

	public void reset() {
		for(FileConfiguration file : saveables.values())
			for(String key : file.getKeys(false))
				file.set(key, null);
	}

	public @NotNull FkConfig getFileConfiguration(@NotNull String path) {
		return this.files.computeIfAbsent(path, this::loadFile);
	}

	public @NotNull FkConfig getTempFileConfiguration(@NotNull String path) {
		FkConfig config = this.files.get(path);
		if (config == null) {
			return this.loadFile(path);
		}
		return config;
	}

	private @NotNull FkConfig loadFile(@NotNull String filename) {
		return new FkConfig(new File(plugin.getDataFolder(), filename));
	}

	private void updateMemoryConfig() {
		lastSave = System.currentTimeMillis();
		for (Map.Entry<Saveable, FkConfig> entry : saveables.entrySet()) {
			Saveable saveable = entry.getKey();
			ConfigurationSection section = entry.getValue().createSection(saveable.getClass().getSimpleName());
			saveable.save(section);
		}
	}

	public long getLastSave()
	{
		return lastSave;
	}
}
