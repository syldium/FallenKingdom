package fr.devsylone.fkpi.util;

import org.bukkit.ChatColor;

public enum Color
{
	

	BLEU("bleu", "bleue", "blue", ChatColor.BLUE, (byte) 11),
	ROUGE("rouge", "rouge", "red", ChatColor.RED, (byte) 14),
	VERT("vert", "verte", "green", ChatColor.GREEN, (byte) 5),
	VIOLET("violet", "violette", "purple", ChatColor.DARK_PURPLE, (byte) 10),
	ROSE("rose", "rose", "rose", ChatColor.LIGHT_PURPLE, (byte) 6),
	JAUNE("jaune", "jaune", "yellow", ChatColor.YELLOW, (byte) 4),
	BLANC("blanc", "blanche", "white", ChatColor.WHITE, (byte) 0),
	NOIR("noir", "noire", "black", ChatColor.BLACK, (byte) 15),
	ORANGE("orange", "orange", "orange", ChatColor.GOLD, (byte) 1),
	GRIS("gris", "grise", "gray", ChatColor.GRAY, (byte) 8),
	NO_COLOR("no color", "no color", "no color", ChatColor.WHITE, (byte) 0);

	private String maleColor;
	private String femColor;
	private String enColor;
	private ChatColor chatColor;
	private byte data;
	
	public static final int GENRE_F = 0;
	public static final int GENRE_M = 1;

	private Color(String maleColor, String femColor, String enColor, ChatColor chatColor, byte data)
	{
		this.maleColor = maleColor;
		this.femColor = femColor;
		this.enColor = enColor;
		this.chatColor = chatColor;
		this.data = data;
	}

	public static Color forName(String name)
	{
		for(Color c : Color.values())
			if(name.equalsIgnoreCase(c.maleColor) || name.equalsIgnoreCase(c.femColor) || name.equalsIgnoreCase(c.enColor))
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
			if(color.equalsIgnoreCase(c.maleColor) || color.equalsIgnoreCase(c.femColor) || name.equalsIgnoreCase(c.enColor))
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
