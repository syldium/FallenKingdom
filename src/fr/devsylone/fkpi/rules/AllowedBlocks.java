package fr.devsylone.fkpi.rules;

import fr.devsylone.fkpi.util.BlockDescription;
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

	public static AllowedBlocks fromStringList(List<String> value)
	{
		return new AllowedBlocks(value.stream().map(v -> {
			if (v.contains(":")) {
				String[] splitted = v.split(":");
				return new BlockDescription(splitted[0], Byte.parseByte(splitted[1]));
			} else {
				return new BlockDescription(v);
			}
		}).collect(Collectors.toList()));
	}

	public AllowedBlocks()
	{
		this(new ArrayList<BlockDescription>());
	}

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
}