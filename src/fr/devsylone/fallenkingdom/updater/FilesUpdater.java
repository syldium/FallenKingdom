package fr.devsylone.fallenkingdom.updater;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fkpi.util.Color;

public class FilesUpdater
{
	private ConfigurationSection getSection;
	private ConfigurationSection setSection;

	private FileConfiguration file;
	private String lastv;

	public FilesUpdater(String lastVersion)
	{
		lastv = lastVersion;
	}

	public void update()
	{
		if(isGrowing(lastv, "2.6.0"))
		{
			file = Fk.getInstance().getSaveableManager().getFileConfiguration("save.yml");

			/*
			 * RulesManager
			 */

			getSection = file.getConfigurationSection("FkPI.RulesManager.Rules");
			setSection = getSection;

			getSection = file.getConfigurationSection("FkPI.RulesManager.Rules");
			setSection = getSection;

			//Version < 2.2.0 + Edit 2.5.0 boolean -> Integer
			if(!isSection("ChargedCreepers"))
				set("ChargedCreepers", 10050001);

			//Version < 2.5.0
			if(!isSection("PlaceBlockInCave"))
				set("PlaceBlockInCave", true);

			//Version < 2.5.0
			if(!isSection("TntJump"))
				set("TntJump", true);

			//Version < 2.5.0
			if(get("ChargedCreepers") instanceof Boolean)
				if(getSection.getBoolean("ChargedCreepers"))
					set("ChargedCreepers", 10050001);
				else
					set("ChargedCreepers", 50001);

			//Version 2.5.0 Material.SIGN c'est l'item
			if(setSection.getStringList("AllowedBlocks").contains(XMaterial.OAK_SIGN.parseMaterial().name()))
			{
				List<String> list = setSection.getStringList("AllowedBlocks");
				list.remove(XMaterial.OAK_SIGN.parseMaterial().name());
				set("AllowedBlocks", list);
			}

			// Version < je sais plus
			if(!isSection("NetherCap"))
				set("NetherCap", 1);

			//Version < 2.5.0
			if(!isSection("EndCap"))
				set("EndCap", 1);

			for(String key : setSection.getKeys(false))
				set(key + ".value", get(key));

			set("PlaceBlockInCave.minimumBlocks", 3);

			/*
			 * PlayerManager
			 */

			getSection = file.getConfigurationSection("PlayerManager");
			setSection = file.createSection("PlayerManager.Players");

			Set<String> players = new HashSet<String>();

			for(String p : players)
			{
				set(p + ".Deaths", get("Deaths." + p) == null ? 0 : get("Deaths." + p));
				set(p + ".Kills", get("Kills." + p) == null ? 0 : get("Kills." + p));
				set(p + ".State", "INGAME");
				set(p + ".KnowsSbEdit", "false");

				if(isSection("Portal." + p))
				{
					set(p + ".Portal.World", get("Portal." + p + ".World"));
					set(p + ".Portal.X", get("Portal." + p + ".X"));
					set(p + ".Portal.Y", get("Portal." + p + ".Y"));
					set(p + ".Portal.Z", get("Portal." + p + ".Z"));
				}

			}

		}

		if(isGrowing(lastv, "2.11.0"))
		{
			file = Fk.getInstance().getSaveableManager().getFileConfiguration("locked_chests.yml");

			getSection = file.getConfigurationSection("LockedChestsManager");
			file = Fk.getInstance().getSaveableManager().getFileConfiguration("save.yml");
			setSection = file.createSection("FkPI.LockedChestsManager");

			if(getSection != null)
			{
				for(String key : getSection.getKeys(true))
					setSection.set(key, getSection.get(key));
			}
			setSection = file.createSection("FkPI.ChestsRoomsManager");
			set("CaptureTime", 60);
			set("Offset", 2);

			if(!GameState.valueOf(file.getString("Game.State")).equals(GameState.BEFORE_STARTING))
				set("Enabled", false);
			else
				set("Enabled", true);

		}

		if(isGrowing(lastv, "2.16.0"))
		{
			file = Fk.getInstance().getSaveableManager().getFileConfiguration("save.yml");

			//			setSection = file.createSection("FkPI.RulesManager.Rules.HealthBelowName");
			//
			//			setSection = file.createSection("FkPI.ChestsRoomsManager");
			//			setSection.set("value", true);

			getSection = file.getConfigurationSection("FkPI.TeamManager");
			setSection = getSection;
			for(String team : getSection.getKeys(false))
				set(team + ".Color", Color.forName(team));

			file = Fk.getInstance().getSaveableManager().getFileConfiguration("scoreboard.yml");

			List<String> lines = file.getStringList("ScoreboardManager.Sidebar");
			for(int i=0;i<lines.size();i++)
				if(lines.get(i).startsWith("Base : "))
				{
					lines.set(i, lines.get(i).replaceAll("Base : ", "{BASE_PORTAL}"));
					break;
				}
			System.out.println(String.join(" - ", lines));
			file.set("ScoreboardManager.Sidebar", lines);
		}
	}

	public boolean isSection(String path)
	{
		return getSection.isConfigurationSection(path);
	}

	public boolean contains(String path)
	{
		return getSection.contains(path);
	}

	public void set(String path, Object value)
	{
		setSection.set(path, value);
	}

	public Object get(String path)
	{
		return getSection.get(path);
	}

	public static boolean isGrowing(String v1, String v2)
	{
		String[] parsedv1 = v1.split("\\.");
		String[] parsedv2 = v2.split("\\.");

		for(int i = 0; i < Math.min(parsedv1.length, parsedv2.length); i++)
			if(!parsedv1[i].equals(parsedv2[i]))
				return Integer.parseInt(parsedv1[i]) < Integer.parseInt(parsedv2[i]);

		return parsedv1.length < parsedv2.length;
	}
}
