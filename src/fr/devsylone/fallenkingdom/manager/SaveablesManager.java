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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private final Set<Saveable> saveables =  new HashSet<>(); // L'ordre de chargement est important
	private FilesUpdater filesUpdater;
	private long lastSave = 0;

	public SaveablesManager(@NotNull Fk fk) {
		this.plugin = fk;

		FkConfig mainConfig = loadFile("save.yml");

        // Register all required saveables
		registerSaveable(fk.getFkPI());
		registerSaveable(fk.getGame());
		registerSaveable(fk.getPlayerManager());
		registerSaveable(fk.getStarterInventoryManager());
		registerSaveable(fk.getPauseRestorer());
		registerSaveable(fk.getDisplayService());
		registerSaveable(fk.getDeepPauseManager());
		registerSaveable(fk.getPortalsManager());

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
	public void delayedSaveAll(FkConfig config) {
		updateMemoryConfig(config);
        config.delayedSave();
	}

	/**
	 * Lit les fichiers depuis le système de fichiers et appelle {@link Saveable#loadNullable(ConfigurationSection)}.
	 *
	 * <p>Si un fichier pose problème, il est réinitialisé. Noter que les classes {@link Saveable} devraient vérifier en
	 * amont les données, pour capturer le plus tôt possible la moindre exception.</p>
	 */
	public void loadAll(@NotNull FkConfig config) {
        config.load();

        boolean corrupted = false;
		for (Saveable saveable : saveables) {
			try {
				saveable.loadNullable(
						config.contains(saveable.getClass().getSimpleName())
								? config.getConfigurationSection(saveable.getClass().getSimpleName())
								: config.createSection(saveable.getClass().getSimpleName())
				);
			} catch (Throwable throwable) {
				throwable.printStackTrace();
                corrupted = true;
			}
		}

		if (corrupted) {
			plugin.getOnConnectWarnings().add(Messages.CORRUPT_CONFIG_FILES.getMessage());
			File zip = new File(plugin.getDataFolder(), "invalid-" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(Calendar.getInstance().getTimeInMillis()) + ".zip");
			try {
				zip.createNewFile();

				try (FileOutputStream fileStream = new FileOutputStream(zip);
					 ZipOutputStream zipStream = new ZipOutputStream(fileStream)) {
					ZipUtils.zipConfig(plugin.getDataFolder().toPath(), zipStream);
					zipStream.flush();
				}
                config.delete();
			} catch (IOException e) {
				plugin.getLogger().log(Level.SEVERE, Messages.CONSOLE_UNABLE_TO_MAKE_BACKUP.getMessage(), e);
			}
		}

		plugin.getDisplayService().updateAll();
	}

	private void registerSaveable(Saveable o) {
		saveables.add(o);
	}

	public @NotNull FkConfig loadFile(@NotNull String filename) {
		return new FkConfig(new File(plugin.getDataFolder(), filename));
	}

	private void updateMemoryConfig(FkConfig config) {
		lastSave = System.currentTimeMillis();
		for (Saveable saveable: saveables) {
			ConfigurationSection section = config.createSection(saveable.getClass().getSimpleName());
			saveable.save(section);
		}
	}

	public long getLastSave()
	{
		return lastSave;
	}
}
