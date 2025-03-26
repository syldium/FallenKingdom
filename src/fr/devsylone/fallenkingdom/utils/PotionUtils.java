package fr.devsylone.fallenkingdom.utils;

import fr.devsylone.fallenkingdom.version.Version;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static fr.devsylone.fallenkingdom.utils.PacketUtils.MINECRAFT_SERVER;

public class PotionUtils
{
	// 1.8 TileEntityBrewingStand
	private static final Constructor<?> TILE_BREWING_STAND;
	private static final Method TILE_BREWING_STAND_SET_ITEM;
	private static final Method TILE_BREWING_STAND_GET_ITEM;

	// 1.8 TileEntityBrewingStand#o(): void - 1.9+ PotionBrewer#d(ItemStack, ItemStack): ItemStack
	private static final Method POTION_BREW;

	// 1.21+ MinecraftServer#brewingRecipeRegistry(): BrewingRecipeRegistry
	private static final Method GET_BREWING_REGISTRY;

	static {
		try {
			if (Version.VersionType.V1_9_V1_12.isHigherOrEqual()) {
				TILE_BREWING_STAND = null;
				TILE_BREWING_STAND_SET_ITEM = null;
				TILE_BREWING_STAND_GET_ITEM = null;

				Class<?> brewingRegistry = NMSUtils.nmsClass("world.item.alchemy", "PotionBrewing", "PotionBrewer");
				POTION_BREW = NMSUtils.getMethod(brewingRegistry, XItemStack.ITEM_STACK, XItemStack.ITEM_STACK, XItemStack.ITEM_STACK);
				Method getBrewingRegistry = null;
				try {
					getBrewingRegistry = NMSUtils.getMethod(MINECRAFT_SERVER, brewingRegistry); // 1.21+
				} catch (NoSuchMethodException ignored) {}
				GET_BREWING_REGISTRY = getBrewingRegistry;
			} else {
				Class<?> tileEntityBrewingStand = NMSUtils.nmsClass("world.level.block.entity", "TileEntityBrewingStand");
				TILE_BREWING_STAND = tileEntityBrewingStand.getConstructor();
				TILE_BREWING_STAND_SET_ITEM = tileEntityBrewingStand
						.getMethod("setItem", int.class, XItemStack.ITEM_STACK);
				TILE_BREWING_STAND_GET_ITEM = tileEntityBrewingStand
						.getMethod("getItem", int.class);
				POTION_BREW = tileEntityBrewingStand.getDeclaredMethod("o");
				POTION_BREW.setAccessible(true);
				GET_BREWING_REGISTRY = null;
			}
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static ItemStack[] getBrewedPotions(ItemStack[] potions, ItemStack ingredient)
	{
		try {
			if (TILE_BREWING_STAND == null) {
				Object registry = GET_BREWING_REGISTRY == null ? null : GET_BREWING_REGISTRY.invoke(PacketUtils.getNMSServer());
				ItemStack[] result = new ItemStack[potions.length];
				for (int slot = 0; slot < potions.length; slot++) {
					Object craftPotion = XItemStack.asCraftItem(potions[slot]);
					Object craftIngredient = XItemStack.asCraftItem(ingredient);
					result[slot] = XItemStack.asBukkitItem(POTION_BREW.invoke(registry, craftIngredient, craftPotion));
				}
				return result;
			}

			Object fakeTileEntityStand = TILE_BREWING_STAND.newInstance();
			for (int slot = 0; slot < potions.length; slot++) {
				if (potions[slot] != null) {
					TILE_BREWING_STAND_SET_ITEM.invoke(fakeTileEntityStand, slot, XItemStack.asCraftItem(potions[slot]));
				}
			}
			TILE_BREWING_STAND_SET_ITEM.invoke(fakeTileEntityStand, 3, XItemStack.asCraftItem(ingredient));

			POTION_BREW.invoke(fakeTileEntityStand);
			ItemStack[] result = new ItemStack[potions.length];
			for (int slot = 0; slot < potions.length; slot++) {
				result[slot] = XItemStack.asBukkitItem(TILE_BREWING_STAND_GET_ITEM.invoke(fakeTileEntityStand, slot));
			}
			return result;
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
