package fr.devsylone.fallenkingdom.manager;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.updater.FilesUpdater;
import fr.devsylone.fallenkingdom.utils.FkConfig;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
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
	private final Map<Saveable, FkConfig> saveables = new IdentityHashMap<>();
	private long lastSave = 0;

	public SaveablesManager(@NotNull Fk fk) {
		this.plugin = fk;

		FkConfig mainConfig = getFileConfiguration("save.yml");

		registerSaveable(fk.getGame(), "save.yml");
		registerSaveable(fk.getFkPI(), "save.yml");
		registerSaveable(fk.getPlayerManager(), "save.yml");
		registerSaveable(fk.getStarterInventoryManager(), "save.yml");

		registerSaveable(fk.getPauseRestorer(), "pause_restorer.yml");

		registerSaveable(fk.getScoreboardManager(), "scoreboard.yml");

		registerSaveable(fk.getDeepPauseManager(), "deep_pause.yml");

		registerSaveable(fk.getPortalsManager(), "portals.yml");

		new FilesUpdater(fk.getDescription().getVersion()).update();
		mainConfig.set("last_version", fk.getDescription().getVersion());
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
		for (Saveable s : sort(saveables.keySet())) {
			try {
				s.loadNullable(saveables.get(s).contains(s.getClass().getSimpleName()) ? saveables.get(s).getConfigurationSection(s.getClass().getSimpleName()) : saveables.get(s).createSection(s.getClass().getSimpleName()));
			} catch (Throwable throwable) {
				throwable.printStackTrace();
				corrupted.add(saveables.get(s));
			}
		}

		if (!corrupted.isEmpty()) {
			plugin.getOnConnectWarnings().add("§cVotre configuration était corrompue ou invalide, elle a donc été sauvegardée puis supprimée. Désolé :S");
			File zip = new File(plugin.getDataFolder(), "invalid-" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(Calendar.getInstance().getTimeInMillis()) + ".zip");
			try {
				zip.createNewFile();

				try (FileOutputStream fileStream = new FileOutputStream(zip);
					 ZipOutputStream zipStream = new ZipOutputStream(fileStream)) {
					ZipUtils.zipFile(plugin.getDataFolder(), "FallenKingdom", zipStream, false);
					zipStream.flush();
				}

				for (FkConfig file : corrupted) {
					file.delete();
				}
			} catch (IOException e) {
				plugin.getLogger().log(Level.SEVERE, "Unable to make a backup", e);
			}
		}

		plugin.getScoreboardManager().recreateAllScoreboards();
	}

	private void registerSaveable(Saveable o, String file) {
		saveables.put(o, getFileConfiguration(file));
	}

	public void reset() {
		for(FileConfiguration file : saveables.values())
			for(String key : file.getKeys(false))
				file.set(key, null);
	}

	public @NotNull FkConfig getFileConfiguration(String path) {
		return files.computeIfAbsent(path, this::loadFile);
	}

	private @NotNull FkConfig loadFile(String filename) {
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

	public List<Saveable> sort(Set<Saveable> toSort) {
		Comparator<Saveable> isFkPI = (e1, e2) -> e1.getClass().getSimpleName().equals("FkPI") && !e2.getClass().getSimpleName().equals("FkPI") ? -1 : 0;
		return toSort.stream().sorted(isFkPI).collect(Collectors.toList());
	}

	public long getLastSave()
	{
		return lastSave;
	}
}
