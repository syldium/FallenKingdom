package fr.devsylone.fkpi.util;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public enum Color
{
	BLEU("bleu", "bleue", ChatColor.BLUE, DyeColor.BLUE),
	ROUGE("rouge", "rouge", ChatColor.RED, DyeColor.RED),
	VERT("vert", "verte", ChatColor.GREEN, DyeColor.GREEN),
	VIOLET("violet", "violette", ChatColor.DARK_PURPLE, DyeColor.PURPLE),
	ROSE("rose", "rose", ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA),
	JAUNE("jaune", "jaune", ChatColor.YELLOW, DyeColor.YELLOW),
	BLANC("blanc", "blanche", ChatColor.WHITE, DyeColor.WHITE),
	NOIR("noir", "noire", ChatColor.BLACK, DyeColor.BLACK),
	ORANGE("orange", "orange", ChatColor.GOLD, DyeColor.ORANGE),
	GRIS("gris", "grise", ChatColor.GRAY, DyeColor.GRAY),
	NO_COLOR("no color", "no color", ChatColor.WHITE, DyeColor.WHITE);

	private String maleColor;
	private String femColor;
	private ChatColor chatColor;
	private DyeColor dyeColor;
	
	public static final int GENRE_F = 0;
	public static final int GENRE_M = 1;

	Color(String maleColor, String femColor, ChatColor chatColor, DyeColor dyeColor)
	{
		this.maleColor = maleColor;
		this.femColor = femColor;
		this.chatColor = chatColor;
		this.dyeColor = dyeColor;
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

	public DyeColor getDyeColor()
	{
		return dyeColor;
	}
}