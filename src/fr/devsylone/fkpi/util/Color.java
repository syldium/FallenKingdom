package fr.devsylone.fkpi.util;

import org.bukkit.ChatColor;

public enum Color
{
	BLEU("bleu", "bleue", ChatColor.BLUE, (byte) 11),
	ROUGE("rouge", "rouge", ChatColor.RED, (byte) 14),
	VERT("vert", "verte", ChatColor.GREEN, (byte) 5),
	VIOLET("violet", "violette", ChatColor.DARK_PURPLE, (byte) 10),
	ROSE("rose", "rose", ChatColor.LIGHT_PURPLE, (byte) 6),
	JAUNE("jaune", "jaune", ChatColor.YELLOW, (byte) 4),
	BLANC("blanc", "blanche", ChatColor.WHITE, (byte) 0),
	NOIR("noir", "noire", ChatColor.BLACK, (byte) 15),
	ORANGE("orange", "orange", ChatColor.GOLD, (byte) 1),
	GRIS("gris", "grise", ChatColor.GRAY, (byte) 8);

	private String m_maleColor;
	private String m_femColor;
	private ChatColor m_chatColor;
	private byte m_data;

	Color(String colorM, String colorF, ChatColor chatColor, byte data)
	{
		m_maleColor = colorM;
		m_femColor = colorF;
		m_chatColor = chatColor;
		m_data = data;
	}

	public static Color forName(String name)
	{
		for(Color c : Color.values())
			if(name.equalsIgnoreCase(c.m_maleColor) || name.equalsIgnoreCase(c.m_femColor))
				return c;

		return null;
	}

	public static String getMaleColor(String color)
	{
		for(Color c : Color.values())
			if(color.equalsIgnoreCase(c.m_maleColor) || color.equalsIgnoreCase(c.m_femColor))
				return c.m_maleColor;

		return null;
	}

	public static String getColor(String color, String genre)
	{
		if(genre == "m")
		{
			for(Color c : Color.values())
			{
				if(color.equalsIgnoreCase(c.m_maleColor) || color.equalsIgnoreCase(c.m_femColor))
					return c.m_maleColor;
			}
		}

		if(genre == "f")
		{
			for(Color c : Color.values())
			{
				if(color.equalsIgnoreCase(c.m_maleColor) || color.equalsIgnoreCase(c.m_femColor))
					return c.m_femColor;
			}
		}

		return "null null";
	}

	public static ChatColor getChatColor(String color)
	{
		for(Color c : Color.values())
			if(color.equalsIgnoreCase(c.m_maleColor) || color.equalsIgnoreCase(c.m_femColor))
				return c.m_chatColor;

		return ChatColor.WHITE;
	}

	public ChatColor getChatColor()
	{
		return m_chatColor;
	}

	public static byte getData(String color)
	{
		for(Color c : Color.values())
			if(color.equalsIgnoreCase(c.m_maleColor) || color.equalsIgnoreCase(c.m_femColor))
				return c.m_data;

		return 0;
	}
}