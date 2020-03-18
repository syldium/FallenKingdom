package fr.devsylone.fallenkingdom.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class Utils
{
	public static boolean isCurrentVersionGreater()
	{
		return true;
	}

	public static ItemStack getPredicatedBrewedPotion(ItemStack potionItem, ItemStack ingredientItem)
	{
		if(Bukkit.getVersion().contains("1.8"))
			throw new RuntimeException("Version must be >= 1.9");
		ItemStack resultItem = null;
		try
		{
			Class<?> craftItemStackClass = NMSUtils.nmsClass("ItemStack");
			Method asNMSCopyMethod = NMSUtils.obcClass("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class);
			Method asCraftMirrorMethod = NMSUtils.obcClass("inventory.CraftItemStack").getDeclaredMethod("asCraftMirror", craftItemStackClass);

			Object craftPotion = asNMSCopyMethod.invoke(null, potionItem);
			Object craftIngredient = asNMSCopyMethod.invoke(null, ingredientItem);
			Object craftResult = NMSUtils.nmsClass("PotionBrewer").getMethod("d", craftItemStackClass, craftItemStackClass).invoke(null, craftIngredient, craftPotion);

			resultItem = (ItemStack) asCraftMirrorMethod.invoke(null, craftResult);
		}catch(IllegalAccessException | ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		
		return resultItem;
	}
}
