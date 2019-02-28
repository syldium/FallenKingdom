package fr.devsylone.fkpi.rules;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class AllowedBlocks extends Rule
{
	public AllowedBlocks(List<String> value)
	{
		super("AllowedBlocks", value);
		this.value = value;
	}

	public AllowedBlocks()
	{
		this(new ArrayList<String>());
	}

	@Override
	public List<String> getValue()
	{
		return (List<String>) value;
	}

	@Override
	public String toString()
	{
		String str = "Name [" + name + "], Blocks [";
		for(String block : getValue())
			str += block+", ";
		str = str.substring(0, str.length() -2);
		str +="]";
		
		return str;
	}
}