package fr.devsylone.fkpi.util;

import fr.devsylone.fallenkingdom.Fk;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Color
{
	private static final Set<Color> LEGACY_VALUES = new HashSet<>();
	public static final Color BLEU = new Color("bleu", "bleue", ChatColor.BLUE, DyeColor.BLUE, 0x5555ff);
	public static final Color CYAN = new Color("cyan", "cyan", ChatColor.DARK_AQUA, DyeColor.CYAN, 0x00aaaa);
	public static final Color AQUA = new Color("aqua", "aqua", ChatColor.AQUA, DyeColor.LIGHT_BLUE, 0x55ffff);
	public static final Color ROUGE = new Color("rouge", "rouge", ChatColor.RED, DyeColor.RED, 0xff5555);
	public static final Color LIME = new Color("lime", "lime", ChatColor.GREEN, DyeColor.LIME, 0x55ff55);
	public static final Color VERT = new Color("vert", "verte", ChatColor.DARK_GREEN, DyeColor.GREEN, 0x00aa00);
	public static final Color VIOLET = new Color("violet", "violette", ChatColor.DARK_PURPLE, DyeColor.PURPLE, 0xaa00aa);
	public static final Color MAGENTA = new Color("magenta", "magenta", ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA, 0xa7375f);
	public static final Color ROSE = new Color("rose", "rose", ChatColor.LIGHT_PURPLE, DyeColor.PINK, 0xff55ff);
	public static final Color ORANGE = new Color("orange", "orange", ChatColor.GOLD, DyeColor.ORANGE, 0xffaa00);
	public static final Color JAUNE = new Color("jaune", "jaune", ChatColor.YELLOW, DyeColor.YELLOW, 0xffff55);
	public static final Color BLANC = new Color("blanc", "blanche", ChatColor.WHITE, DyeColor.WHITE, 0xffffff);
	public static final Color NOIR = new Color("noir", "noire", ChatColor.BLACK, DyeColor.BLACK, 0x000000);
	public static final Color GRIS = new Color("gris", "grise", ChatColor.GRAY, DyeColor.GRAY, 0xaaaaaa);
	public static final Color NO_COLOR = new Color("no color", "no color", ChatColor.WHITE, DyeColor.WHITE, 0xffffff);

	private final String maleColor;
	private final String femColor;
	private final java.awt.Color value;

	private final ChatColor bukkitChatColor;
	private net.md_5.bungee.api.ChatColor bungeeChatColor;
	private final DyeColor dyeColor;
	
	public static final int GENRE_F = 0;
	public static final int GENRE_M = 1;

	Color(String maleColor, String femColor, ChatColor chatColor, DyeColor dyeColor, int value)
	{
		this.maleColor = maleColor;
		this.femColor = femColor;
		this.value = new java.awt.Color(value);
		this.bukkitChatColor = chatColor;
		// noinspection deprecation
		this.bungeeChatColor = net.md_5.bungee.api.ChatColor.class.isEnum() ? net.md_5.bungee.api.ChatColor.valueOf(chatColor.name()) : net.md_5.bungee.api.ChatColor.of(this.value);
		this.dyeColor = dyeColor;
		LEGACY_VALUES.add(this);
	}

	Color(Color base, java.awt.Color awtColor)
	{
		this.maleColor = base.maleColor;
		this.femColor = base.femColor;
		this.value = awtColor;
		this.bukkitChatColor = base.bukkitChatColor;
		this.bungeeChatColor = net.md_5.bungee.api.ChatColor.class.isEnum() ? base.bungeeChatColor : net.md_5.bungee.api.ChatColor.of(awtColor);
		this.dyeColor = base.dyeColor;
	}

	public static Color of(String name)
	{
		if(name.startsWith("#") && name.length() == 7)
		{
			java.awt.Color awtColor = java.awt.Color.decode(name);
			Color nearest = nearestTo(awtColor);
			return new Color(nearest, awtColor);
		}

		// Lorsqu'un nom de couleur est donné ou que l'on charge une ancienne save
		for(Color c : LEGACY_VALUES)
			if(name.equalsIgnoreCase(c.maleColor) || name.equalsIgnoreCase(c.femColor) || c.dyeColor.name().equalsIgnoreCase(name))
				return c;

		return Color.NO_COLOR;
	}

	public String getGenredName(int genre)
	{
		if(Fk.getInstance().getLanguageManager().getLocalePrefix().equalsIgnoreCase("fr"))
			return genre == GENRE_M ? maleColor : femColor;
		return dyeColor.name().toLowerCase().replace('_', ' ');
	}

	public net.md_5.bungee.api.ChatColor getChatColor()
	{
		return bungeeChatColor;
	}

	public ChatColor getBukkitChatColor()
	{
		return bukkitChatColor;
	}

	public DyeColor getDyeColor()
	{
		return dyeColor;
	}

	public String getHexString()
	{
		return String.format("#%06x", value.getRGB() & 0xFFFFFF);
	}

	public int getRGB()
	{
		return value.getRGB() & 0xFFFFFF;
	}

	public static Color[] values()
	{
		return LEGACY_VALUES.toArray(new Color[0]);
	}

	public static Color nearestTo(java.awt.Color any)
	{
		int matchedDistance = Integer.MAX_VALUE;
		Color match = null;
		for(Color potential : LEGACY_VALUES) {
			int distance = distanceSquared(potential, any);
			if(distance < matchedDistance) {
				match = potential;
				matchedDistance = distance;
			}
			if(distance == 0) {
				break;
			}
		}
		return match;
	}

	private static int distanceSquared(Color self, java.awt.Color other)
	{
		int rAvg = (self.value.getRed() + other.getRed()) / 2;
		int dR = self.value.getRed() - other.getRed();
		int dG = self.value.getGreen() - other.getGreen();
		int dB = self.value.getBlue() - other.getBlue();
		return ((2 + (rAvg / 256)) * (dR * dR)) + (4 * (dG * dG)) + ((2 + ((255 - rAvg) / 256)) * (dB * dB));
	}

	@Override
	public boolean equals(Object other)
	{
		if(!(other instanceof Color)) return false;
		Color color1 = (Color) other;
		return maleColor.equals(color1.maleColor) &&
				femColor.equals(color1.femColor) &&
				value == color1.value;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(maleColor, femColor, value);
	}
}