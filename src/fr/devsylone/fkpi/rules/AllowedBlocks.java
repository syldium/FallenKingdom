package fr.devsylone.fkpi.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.api.event.RuleChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.cryptomorin.xseries.XMaterial;

public class AllowedBlocks implements RuleValue
{
	private final Map<Material, Set<Byte>> allowed = new EnumMap<>(Material.class);
	private final List<String> reducedList = new ArrayList<>();

	public boolean isAllowed(Material material, byte data)
	{
		Set<Byte> datas = allowed.get(material);
		if (datas == null) {
			return false;
		}
		return datas.isEmpty() || datas.contains(data);
	}

	@SuppressWarnings("deprecation")
	public boolean isAllowed(Block block)
	{
		return isAllowed(block.getType(), XMaterial.isNewVersion() ? 0 : block.getData());
	}

	public List<String> reducedList()
	{
		if (!reducedList.isEmpty() || allowed.isEmpty()) {
			return reducedList;
		}

		boolean allSigns = allowed.keySet().containsAll(SIGNS);
		for (Map.Entry<Material, Set<Byte>> entry : allowed.entrySet()) {
			String name = entry.getKey().name();
			if (entry.getValue().isEmpty()) {
				if (!allSigns || !SIGNS.contains(entry.getKey())) {
					reducedList.add(name);
				}
			} else {
				for (byte data : entry.getValue()) {
					reducedList.add(name + ':' + data);
				}
			}
		}
		if (allSigns) {
			reducedList.add("SIGN (tous types)");
		}
		return reducedList;
	}

	@Override
	public void fillWithDefaultValue()
	{
		add(Material.TORCH);
		add(Material.WALL_TORCH);
		if (XMaterial.isNewVersion()) {
			add(Material.REDSTONE_TORCH);
			add(Material.REDSTONE_WALL_TORCH);
		} else {
			add(Material.valueOf("REDSTONE_TORCH_ON"));
		}

		add(Material.FIRE);
		for (Material sign : SIGNS) {
			add(sign);
		}
	}

	@Override
	public JsonElement toJSON()
	{
		JsonArray jsonArray = new JsonArray();
		for (Material material : allowed.keySet()) {
			jsonArray.add(material.toString());
		}
		return jsonArray;
	}

	public String format()
	{
		StringBuilder formatted = new StringBuilder();
		for(String b : reducedList())
			formatted.append("\n§a✔ ").append(b);
		return formatted.toString();
	}

	public void load(ConfigurationSection config)
	{
		for (String name : config.getStringList("value")) {
			int sep = name.indexOf(':');
			if (sep < 0) {
				Material material = Material.matchMaterial(name);
				if (material == null) {
					Fk.getInstance().getLogger().warning("AllowedBlocks: Unknown material: " + name);
				} else {
					allowed.put(material, Collections.emptySet());
				}
			} else {
				Material material = Material.matchMaterial(name.substring(0, sep));
				if (material == null) {
					Fk.getInstance().getLogger().warning("AllowedBlocks: Unknown material: " + name);
				} else {
					allowed.computeIfAbsent(material, s -> new HashSet<>()).add(Byte.parseByte(name.substring(sep + 1)));
				}
			}
		}
	}

	public void save(ConfigurationSection config)
	{
		List<String> blocksString = new ArrayList<>(allowed.size());
		for (Map.Entry<Material, Set<Byte>> entry : allowed.entrySet()) {
			String name = entry.getKey().name();
			if (entry.getValue().isEmpty()) {
				blocksString.add(name);
			} else {
				for (byte data : entry.getValue()) {
					blocksString.add(name + ':' + data);
				}
			}
		}
		config.set("value", blocksString);
	}

	@Override
	public String toString()
	{
		return "Blocks[" + allowed.entrySet().stream()
				.map(entry -> entry.getKey() + "(" + entry.getValue() + ")")
				.collect(Collectors.joining(", ")) + "]";
	}

    public void add(Material material)
	{
		Bukkit.getPluginManager().callEvent(new RuleChangeEvent<>(Rule.ALLOWED_BLOCKS, this));
		if (!Collections.emptySet().equals(allowed.put(material, Collections.emptySet()))) {
			reducedList.clear();
		}
    }

	public void add(Material material, byte data)
	{
		if (data < 0) {
			add(material);
			return;
		}

		Bukkit.getPluginManager().callEvent(new RuleChangeEvent<>(Rule.ALLOWED_BLOCKS, this));
		 if (allowed.computeIfAbsent(material, s -> new HashSet<>()).add(data)) {
			 reducedList.clear();
		 }
	}

	public void remove(Material material)
	{
		Bukkit.getPluginManager().callEvent(new RuleChangeEvent<>(Rule.ALLOWED_BLOCKS, this));
		if (allowed.remove(material) != null) {
			reducedList.clear();
		}
	}

	public void remove(Material material, byte data)
	{
		if (data < 0) {
			remove(material);
			return;
		}

		Set<Byte> datas = allowed.get(material);
		if (datas != null) {
			Bukkit.getPluginManager().callEvent(new RuleChangeEvent<>(Rule.ALLOWED_BLOCKS, this));
			boolean removed = datas.remove(data);
			if (removed && datas.isEmpty()) {
				allowed.remove(material);
			}
			if (removed) {
				reducedList.clear();
			}
		}
	}

	private static final Set<Material> SIGNS = new HashSet<>();

	static {
		try {
			Class.forName("org.bukkit.Tag");
			SIGNS.addAll(new HashSet<>(Tag.SIGNS.getValues()));
		} catch (ClassNotFoundException e) {
			if (XMaterial.isNewVersion()) {
				SIGNS.add(XMaterial.OAK_SIGN.parseMaterial());
			} else {
				SIGNS.add(Material.valueOf("SIGN_POST"));
			}
			SIGNS.add(XMaterial.OAK_WALL_SIGN.parseMaterial());
		}
	}
}