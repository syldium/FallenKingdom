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
	GRIS("gris", "grise", ChatColor.GRAY, (byte) 8),
	NO_COLOR("no color", "no color", ChatColor.WHITE, (byte) 0);

	private String maleColor;
	private String femColor;
	private ChatColor chatColor;
	private byte data;
	
	public static final int GENRE_F = 0;
	public static final int GENRE_M = 1;

	private Color(String maleColor, String femColor, ChatColor chatColor, byte data)
	{
		this.maleColor = maleColor;
		this.femColor = femColor;
		this.chatColor = chatColor;
		this.data = data;
	}

	public static Color forName(String name)
	{
		for(Color c : Color.values())
			if(name.equalsIgnoreCase(c.maleColor) || name.equalsIgnoreCase(c.femColor))
				return c;

		return null;
	}
	
	public String getGenredName(int genre)
	{
		return genre == GENRE_M ? maleColor : femColor;
	}

	public static String getGenredName(String color, int genre)
	{
		return forName(color).getGenredName(genre);
	}

	public static ChatColor getChatColor(String color)
	{
		for(Color c : Color.values())
			if(color.equalsIgnoreCase(c.maleColor) || color.equalsIgnoreCase(c.femColor))
				return c.chatColor;

		return ChatColor.WHITE;
	}

	public ChatColor getChatColor()
	{
		return chatColor;
	}

	public byte getData()
	{
		return data;
	}
}