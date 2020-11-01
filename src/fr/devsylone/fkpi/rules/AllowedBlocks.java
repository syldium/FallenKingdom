package fr.devsylone.fkpi.rules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.api.event.RuleChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;

import com.cryptomorin.xseries.XMaterial;

import fr.devsylone.fkpi.util.BlockDescription;

public class AllowedBlocks implements RuleValue
{
	private final Set<BlockDescription> allowed = new HashSet<>();

	public Set<BlockDescription> getValue()
	{
		return allowed;
	}

	public boolean isAllowed(BlockDescription block)
	{
		return getValue().contains(block);
	}

	public Set<String> reducedList()
	{
		if (allowed.containsAll(SIGNS)) {
			return Stream.concat(
					Stream.of("SIGN (tous types)"),
					allowed.stream()
							.filter(bd -> !SIGNS.contains(bd))
							.map(BlockDescription::toString)
			).collect(Collectors.toSet());
		}
		return allowed.stream().map(BlockDescription::toString).collect(Collectors.toSet());
	}

	@Override
	public void fillWithDefaultValue()
	{
		allowed.add(new BlockDescription("TORCH"));
		allowed.add(new BlockDescription("WALL_TORCH"));
		if (XMaterial.isNewVersion()) {
			allowed.add(new BlockDescription(XMaterial.REDSTONE_TORCH.parseMaterial()));
			allowed.add(new BlockDescription(XMaterial.REDSTONE_WALL_TORCH.parseMaterial()));
		} else {
			allowed.add(new BlockDescription("REDSTONE_TORCH_ON"));
		}

		allowed.add(new BlockDescription("FIRE"));
		allowed.addAll(SIGNS);
	}

	@Override
	public JsonElement toJSON()
	{
		JsonArray jsonArray = new JsonArray();
		for (BlockDescription block : getValue()) {
			jsonArray.add(block.toString());
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
		for (String blockString : config.getStringList("value")) {
			try {
				allowed.add(new BlockDescription(blockString));
			} catch (IllegalArgumentException e) {
				Fk.getInstance().getLogger().warning("AllowedBlocks: " + e.getMessage());
			}
		}
	}

	public void save(ConfigurationSection config)
	{
		List<String> blocksString = getValue().stream().map(BlockDescription::toString).collect(Collectors.toList());
		config.set("value", blocksString);
	}

	@Override
	public String toString()
	{
		return "Blocks[" + getValue().stream()
				.map(BlockDescription::toString)
				.collect(Collectors.joining(", ")) + "]";
	}

    public void add(BlockDescription blockDescription)
	{
		Bukkit.getPluginManager().callEvent(new RuleChangeEvent<>(Rule.ALLOWED_BLOCKS, this));
		allowed.add(blockDescription);
    }

	public void removeIf(Predicate<? super BlockDescription> filter)
	{
		Bukkit.getPluginManager().callEvent(new RuleChangeEvent<>(Rule.ALLOWED_BLOCKS, this));
		allowed.removeIf(filter);
	}

	private static Set<BlockDescription> SIGNS = new HashSet<>();

	static {
		try {
			SIGNS.addAll(Tag.SIGNS.getValues().stream().map(BlockDescription::new).collect(Collectors.toSet()));
		} catch (Exception e) {
			if (XMaterial.isNewVersion()) {
				SIGNS.add(new BlockDescription(XMaterial.OAK_SIGN.parseMaterial()));
			} else {
				SIGNS.add(new BlockDescription("SIGN_POST"));
			}
			SIGNS.add(new BlockDescription(XMaterial.OAK_WALL_SIGN.parseMaterial()));
		}
	}
}