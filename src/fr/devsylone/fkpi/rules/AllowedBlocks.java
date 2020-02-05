package fr.devsylone.fkpi.rules;

import fr.devsylone.fallenkingdom.utils.XMaterial;
import fr.devsylone.fkpi.util.BlockDescription;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AllowedBlocks extends Rule
{
	public AllowedBlocks(List<BlockDescription> value)
	{
		super("AllowedBlocks", value);
		this.value = value;
	}

	public AllowedBlocks()
	{
		this(new ArrayList<BlockDescription>());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BlockDescription> getValue()
	{
		return (List<BlockDescription>) value;
	}

	@Override
	public String toString()
	{
		String str = "Name [" + name + "], Blocks [";
		for(BlockDescription block : getValue())
			str += block+", ";
		str = str.substring(0, str.length() -2);
		str +="]";
		
		return str;
	}

	@Override
	public void load(ConfigurationSection config)
	{
		List<String> blocksString = config.getStringList("value");
		value = blocksString.stream().map(b -> new BlockDescription(b)).collect(Collectors.toList());
	}

	@Override
	public void save(ConfigurationSection config)
	{
		List<String> blocksString = getValue().stream().map(b -> b.toString()).collect(Collectors.toList());
		config.set("value", blocksString);
	}

	public boolean isAllowed(BlockDescription block)
	{
		return getValue().contains(block);
	}

	public List<BlockDescription> reducedList()
	{
		List<BlockDescription> list = new ArrayList<>(getValue());
		if (list.containsAll(allSigns())) {
			list.removeIf(b -> b.getBlockName().contains("SIGN"));
			list.add(new BlockDescription("SIGN (tous types)"));
		}
		return list;
	}

	public void fillWithDefaultValues()
	{
		getValue().add(new BlockDescription("TORCH"));
		getValue().add(new BlockDescription("WALL_TORCH"));
		if (XMaterial.isNewVersion()) {
			getValue().add(new BlockDescription(Material.REDSTONE_TORCH));
			getValue().add(new BlockDescription(Material.REDSTONE_WALL_TORCH));
		} else {
			getValue().add(new BlockDescription("REDSTONE_TORCH_ON"));
		}

		getValue().add(new BlockDescription("FIRE"));
		getValue().addAll(allSigns());
		getValue().add(new BlockDescription("TNT"));

	}

	public static List<BlockDescription> allSigns()
	{
		List<BlockDescription> signs = new ArrayList<>();
		if (XMaterial.isNewVersion())
			signs.add(new BlockDescription(XMaterial.OAK_SIGN.parseMaterial()));
		else
			signs.add(new BlockDescription("SIGN_POST"));
		signs.add(new BlockDescription(XMaterial.OAK_WALL_SIGN.parseMaterial()));
		if (Material.getMaterial("ACACIA_SIGN") != null)
		{
			signs.add(new BlockDescription(Material.ACACIA_SIGN));
			signs.add(new BlockDescription(Material.ACACIA_WALL_SIGN));
			signs.add(new BlockDescription(Material.BIRCH_SIGN));
			signs.add(new BlockDescription(Material.BIRCH_WALL_SIGN));
			signs.add(new BlockDescription(Material.DARK_OAK_SIGN));
			signs.add(new BlockDescription(Material.DARK_OAK_WALL_SIGN));
			signs.add(new BlockDescription(Material.JUNGLE_SIGN));
			signs.add(new BlockDescription(Material.JUNGLE_WALL_SIGN));
			signs.add(new BlockDescription(Material.SPRUCE_SIGN));
			signs.add(new BlockDescription(Material.SPRUCE_WALL_SIGN));
		}
		return signs;
	}
}