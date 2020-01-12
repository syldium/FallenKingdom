package fr.devsylone.fkpi.util;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public enum Color
{
	

	BLEU("bleu", "bleue", "blue", ChatColor.BLUE, DyeColor.BLUE),
	ROUGE("rouge", "rouge", "red", ChatColor.RED, DyeColor.RED),
	VERT("vert", "verte", "green", ChatColor.GREEN, DyeColor.GREEN),
	VIOLET("violet", "violette", "purple", ChatColor.DARK_PURPLE, DyeColor.PURPLE),
	ROSE("rose", "rose", "rose", ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA),
	JAUNE("jaune", "jaune", "yellow", ChatColor.YELLOW, DyeColor.YELLOW),
	BLANC("blanc", "blanche", "white", ChatColor.WHITE, DyeColor.WHITE),
	NOIR("noir", "noire", "black", ChatColor.BLACK, DyeColor.BLACK),
	ORANGE("orange", "orange", "orange", ChatColor.GOLD, DyeColor.ORANGE),
	GRIS("gris", "grise", "gray", ChatColor.GRAY, DyeColor.GRAY),
	NO_COLOR("no color", "no color", "no color", ChatColor.WHITE, DyeColor.WHITE);

	private String maleColor;
	private String femColor;
	private String enColor;
	private ChatColor chatColor;
	private DyeColor dyeColor;
	
	public static final int GENRE_F = 0;
	public static final int GENRE_M = 1;

	private Color(String maleColor, String femColor, String enColor, ChatColor chatColor, DyeColor dyeColor)
	{
		this.maleColor = maleColor;
		this.femColor = femColor;
		this.enColor = enColor;
		this.chatColor = chatColor;
		this.dyeColor = dyeColor;
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

	public DyeColor getDyeColor()
	{
		return dyeColor;
	}
}
