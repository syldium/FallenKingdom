package fr.devsylone.fkpi.util;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.TippedArrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static fr.devsylone.fallenkingdom.version.Version.VersionType.V1_16;

@SuppressWarnings("deprecation")
public final class XPotionData
{
	private final PotionType type;

	private final boolean extended;

	private final boolean upgraded;

	private static final boolean VERSION1_8 = Bukkit.getBukkitVersion().contains("1.8");
	private static final boolean SEPARATE_POTION_TYPES;

	static {
		boolean separatePotionTypes = false;
		try {
			PotionMeta.class.getMethod("setBasePotionType", PotionType.class);
			separatePotionTypes = true;
		} catch (NoSuchMethodException e) {
			// ignore
		}
		SEPARATE_POTION_TYPES = separatePotionTypes;
	}

	public XPotionData(PotionType type, boolean extended, boolean upgraded)
	{
		Preconditions.checkNotNull(type, "Potion Type must not be null");
		if (!SEPARATE_POTION_TYPES) {
			Preconditions.checkArgument(!(upgraded && type.getMaxLevel() == 1), "Potion Type is not upgradable");
		}
		Preconditions.checkArgument(!(extended && type.isInstant()), "Potion Type is not extendable" + type);
		Preconditions.checkArgument(!(upgraded && extended), "Potion cannot be both extended and upgraded");
		this.type = type;
		this.extended = extended;
		this.upgraded = upgraded;
	}

	public PotionType getType()
	{
		return this.type;
	}

	public boolean isUpgraded()
	{
		return this.upgraded;
	}

	public boolean isExtended()
	{
		return this.extended;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 23 * hash + ((this.type != null) ? this.type.hashCode() : 0);
		hash = 23 * hash + (this.extended ? 1 : 0);
		hash = 23 * hash + (this.upgraded ? 1 : 0);
		return hash;
	}

	@Override
	public String toString()
	{
		return "XPotionData [type=" + type + ", extended=" + extended + ", upgraded=" + upgraded + "]";
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof XPotionData)) return false;
		XPotionData other = (XPotionData) obj;
		return(this.upgraded == other.upgraded && this.extended == other.extended && this.type == other.type);
	}

	public static @Nullable XPotionData fromItemStack(ItemStack potionItem) {
		if (potionItem == null) {
			return null;
		}
		final ItemMeta itemMeta = potionItem.getItemMeta();
		if (!(itemMeta instanceof PotionMeta)) {
			return null;
		}

		final PotionMeta meta = (PotionMeta) itemMeta;
		if (SEPARATE_POTION_TYPES) {
			final PotionType type = meta.getBasePotionType();
			return type == null ? null : fromModernPotionType(type);
		} else if (VERSION1_8) {
			Potion1_8 potion = Potion1_8.fromItemStack(potionItem);
			return potion.getType() == null ? null : new XPotionData(potion.getType(), potion.hasExtendedDuration(), potion.getType().getMaxLevel() > 1 && potion.getLevel() > 1);
		} else {
			return fromPotionData(meta.getBasePotionData());
		}
	}

	/**
	 * Crée des données de potion à partir de la version 1.20.4.
	 */
	public static @NotNull XPotionData fromModernPotionType(@NotNull PotionType type)
	{
		return new XPotionData(type, type.getKey().getKey().startsWith("long_"), type.getKey().getKey().startsWith("strong_"));
	}

	public static XPotionData fromPotionData(PotionData data)
	{
		return data == null ? null : new XPotionData(data.getType(), data.isExtended(), data.isUpgraded());
	}

	public static @Nullable XPotionData fromProjectile(@NotNull Projectile projectile) {
		if (projectile instanceof ThrownPotion) {
			return fromItemStack(((ThrownPotion) projectile).getItem());
		} else if (projectile instanceof TippedArrow) {
			return fromPotionData(((TippedArrow) projectile).getBasePotionData());
		} else if (projectile instanceof Arrow) {
			if (SEPARATE_POTION_TYPES) {
				final PotionType type = ((Arrow) projectile).getBasePotionType();
				if (type != null) {
					return fromModernPotionType(type);
				}
			} else if (V1_16.isHigherOrEqual()) {
				return fromPotionData(((Arrow) projectile).getBasePotionData());
			}
		}
		return null;
	}

	public void applyTo(ItemStack potionItem)
	{
		if (potionItem == null) {
			return;
		}
		final ItemMeta itemMeta = potionItem.getItemMeta();
		if (!(itemMeta instanceof PotionMeta)) {
			return;
		}

		if (VERSION1_8) {
			Potion1_8 potion = new Potion1_8(type, upgraded ? 2 : 1, false, type.isInstant() && extended);
			potion.apply(potionItem);
		} else {
			PotionMeta meta = (PotionMeta) itemMeta;
			if (SEPARATE_POTION_TYPES) {
				meta.setBasePotionType(type);
			} else {
				meta.setBasePotionData(new PotionData(type, extended, upgraded));
			}
			potionItem.setItemMeta(meta);
		}
	}
	
	public static boolean isExtendable(PotionType type)
	{
		if(VERSION1_8)
			return !type.isInstant();
		else
			return type.isExtendable();
	}
	
	public static boolean isUpgradable(PotionType type)
	{
		if(VERSION1_8)
			return type.getMaxLevel() > 1;
		else
			return type.isUpgradeable();
	}
}
