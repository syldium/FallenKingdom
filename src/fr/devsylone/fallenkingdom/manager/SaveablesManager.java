package fr.devsylone.fallenkingdom.manager;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.updater.FilesUpdater;
import fr.devsylone.fkpi.util.Saveable;

public class SaveablesManager
{
	public enum State
	{
		LOAD,
		SAVE,
		SLEEP
	}

	private final Map<Saveable, FileConfiguration> saveables = new HashMap<>();
	private final Map<String, FileConfiguration> files = new HashMap<>();

	private State state;
	private long lastSave = 0;

	public SaveablesManager(Fk fk)
	{
		state = State.SLEEP;

		registerSaveable(fk.getGame(), "save.yml");
		registerSaveable(fk.getFkPI(), "save.yml");
		registerSaveable(fk.getPlayerManager(), "save.yml");
		registerSaveable(fk.getStarterInventoryManager(), "save.yml");

		registerSaveable(fk.getPauseRestorer(), "pause_restorer.yml");

		registerSaveable(fk.getScoreboardManager(), "scoreboard.yml");

		registerSaveable(fk.getDeepPauseManager(), "deep_pause.yml");
		
		registerSaveable(fk.getPortalsManager(), "portals.yml");
	}

	public void saveAll()
	{
		state = State.SAVE;
		reset();

		lastSave = System.currentTimeMillis();
		for(Saveable s : saveables.keySet())
			s.save(saveables.get(s).createSection(s.getClass().getSimpleName()));

		getFileConfiguration("save.yml").set("last_version", Fk.getInstance().getDescription().getVersion());

		for(String name : files.keySet())
			try
			{
				files.get(name).save(getFile(name));
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		state = State.SLEEP;
	}

	public void loadAll()
	{
		state = State.LOAD;
		if(files.get("save.yml").isConfigurationSection("Game"))
		{
			new FilesUpdater(Fk.getInstance().getPreviousVersion()).update();

			for(String name : files.keySet())
				try
				{
					files.get(name).save(getFile(name));
				}catch(IOException e)
				{
					e.printStackTrace();
				}

			for(Saveable s : sort(saveables.keySet()))
				s.load(saveables.get(s).contains(s.getClass().getSimpleName()) ? saveables.get(s).getConfigurationSection(s.getClass().getSimpleName()) : saveables.get(s).createSection(s.getClass().getSimpleName()));
		}
		state = State.SLEEP;
		Fk.getInstance().getScoreboardManager().recreateAllScoreboards();
	}

	private void registerSaveable(Saveable o, String file)
	{
		saveables.put(o, getFileConfiguration(file));
	}

	public void reset()
	{
		for(FileConfiguration file : files.values())
			for(String key : file.getKeys(false))
				file.set(key, null);
	}

	public State getState()
	{
		return state;
	}

	public FileConfiguration getFileConfiguration(String path)
	{
		if(files.containsKey(path))
			return files.get(path);

		else
		{
			File file = getFile(path);

			try
			{
				FileConfiguration filec = YamlConfiguration.loadConfiguration(file);

				files.put(path, filec);
				return filec;
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

		return null;
	}

	public File getFile(String path)
	{
		File file = new File(Fk.getInstance().getDataFolder(), path);

		if(!file.exists())
			try
			{
				file.createNewFile();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		return file;
	}

	public List<Saveable> sort(Set<Saveable> toSort)
	{
		Comparator<Saveable> isFkPI = (e1, e2) -> e1.getClass().getSimpleName().equals("FkPI") && !e2.getClass().getSimpleName().equals("FkPI") ? -1 : 0;
		return toSort.stream().sorted(isFkPI).collect(Collectors.toList());
	}

	public long getLastSave()
	{
		return lastSave;
	}
}
