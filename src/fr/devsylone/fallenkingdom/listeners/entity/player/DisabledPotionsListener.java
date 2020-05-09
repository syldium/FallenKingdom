package fr.devsylone.fallenkingdom.listeners.entity.player;

import java.lang.reflect.Method;
import java.util.Arrays;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import com.cryptomorin.xseries.XMaterial;

import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.Utils;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.util.XPotionData;

public class DisabledPotionsListener implements Listener
{
	@EventHandler
	public void event(BrewEvent e)
	{
		if(!Fk.getInstance().getWorldManager().isAffected(e.getBlock().getWorld()))
			return;
		if(isForbiddenBrewing(Arrays.copyOf(e.getContents().getContents(), 3), e.getContents().getContents()[3]))
			e.setCancelled(true);
	}

	@EventHandler
	public void event(InventoryDragEvent e)
	{
		if(!(e.getInventory() instanceof BrewerInventory) || !Fk.getInstance().getWorldManager().isAffected(e.getWhoClicked().getWorld()))
			return;
		ItemStack[] potions = Arrays.copyOf(e.getInventory().getContents(), 3);
		ItemStack ingredient = e.getInventory().getItem(3);
		if(e.getRawSlots().contains(3))
			ingredient = e.getOldCursor();
		else if(e.getRawSlots().contains(0) || e.getRawSlots().contains(1) || e.getRawSlots().contains(2))
			potions = new ItemStack[] {null, e.getOldCursor(), null};

		if(isForbiddenBrewing(potions, ingredient))
		{
			ChatUtils.sendMessage(e.getWhoClicked(), Messages.PLAYER_DISABLED_POTION_CRAFT);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void event(InventoryClickEvent e)
	{
		if(!(e.getInventory() instanceof BrewerInventory) || !Fk.getInstance().getWorldManager().isAffected(e.getWhoClicked().getWorld()) || e.getRawSlot() == 4) // 4 = blaze fuel
			return;

		ItemStack newItem = null;
		if((e.getClick() == ClickType.LEFT || e.getClick() == ClickType.RIGHT) && (e.getSlotType() == SlotType.FUEL || e.getSlotType() == SlotType.CRAFTING))
			newItem = e.getCursor();

		else if(e.getClick() == ClickType.NUMBER_KEY && e.getView().getBottomInventory().getItem(e.getHotbarButton()) != null && (e.getSlotType() == SlotType.FUEL && e.getView().getBottomInventory().getItem(e.getHotbarButton()).getType() != XMaterial.POTION.parseMaterial() || e.getSlotType() == SlotType.CRAFTING && e.getView().getBottomInventory().getItem(e.getHotbarButton()).getType() == XMaterial.POTION.parseMaterial()))
			newItem = e.getView().getBottomInventory().getItem(e.getHotbarButton());

		else if(e.isShiftClick() && e.getSlotType() != SlotType.FUEL && e.getSlotType() != SlotType.CRAFTING)
			newItem = e.getCurrentItem();

		if(newItem == null) // pas de new item dans le BrewingInventory
			return;

		ItemStack[] potions = Arrays.copyOf(e.getInventory().getContents(), 3);
		ItemStack ingredient = e.getInventory().getItem(3);

		if(newItem.getType() == XMaterial.POTION.parseMaterial())
			potions = new ItemStack[] {null, newItem, null};
		else
			ingredient = newItem;

		if(isForbiddenBrewing(potions, ingredient))
		{
			if(!Bukkit.getVersion().contains("1.8") && newItem.getType() == XMaterial.BLAZE_POWDER.parseMaterial() && e.isShiftClick())
				e.getWhoClicked().sendMessage(ChatUtils.PREFIX + Messages.PLAYER_DISABLED_POTION_BLAZE_POWDER_SHIFT.getMessage());
			else
				ChatUtils.sendMessage(e.getWhoClicked(), Messages.PLAYER_DISABLED_POTION_CRAFT);
			e.setCancelled(true);
		}
	}

	public boolean isForbiddenBrewing(ItemStack[] potions, ItemStack ingredient)
	{
		if(ingredient == null || Arrays.equals(potions, new ItemStack[]{null, null, null}))
			return false;

		if(Bukkit.getVersion().contains("1.8"))
			try
			{
				Class<?> tileEntityStandClass = NMSUtils.nmsClass("TileEntityBrewingStand");
				Object fakeTileEntityStand = tileEntityStandClass.getDeclaredConstructor().newInstance();
				for(int i = 0; i < potions.length; i++)
					if(potions[i] != null)
						tileEntityStandClass.getDeclaredMethod("setItem", int.class, NMSUtils.nmsClass("ItemStack")).invoke(fakeTileEntityStand, i, NMSUtils.obcClass("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null, potions[i]));
				tileEntityStandClass.getDeclaredMethod("setItem", int.class, NMSUtils.nmsClass("ItemStack")).invoke(fakeTileEntityStand, 3, NMSUtils.obcClass("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null, ingredient));

				Method isBrewingRecipeMethod = tileEntityStandClass.getDeclaredMethod("n");
				isBrewingRecipeMethod.setAccessible(true);
				if(!(boolean) isBrewingRecipeMethod.invoke(fakeTileEntityStand))
					return false;

				Method emulateBrewingMethod = tileEntityStandClass.getDeclaredMethod("o");
				emulateBrewingMethod.setAccessible(true);
				emulateBrewingMethod.invoke(fakeTileEntityStand);
				for(int i = 0; i < 3; i++) // items[3] = ingredient
				{
					ItemStack potion = (ItemStack) NMSUtils.obcClass("inventory.CraftItemStack").getDeclaredMethod("asCraftMirror", NMSUtils.nmsClass("ItemStack")).invoke(null, tileEntityStandClass.getDeclaredMethod("getItem", int.class).invoke(fakeTileEntityStand, i));
					if(FkPI.getInstance().getRulesManager().getRule(Rule.DISABLED_POTIONS).isDisabled(XPotionData.fromItemStack(potion)))
						return true;
				}
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}

		else
			for(ItemStack potion : potions)
				if(potion != null && (potion.getType() == XMaterial.POTION.parseMaterial() || potion.getType() == XMaterial.SPLASH_POTION.parseMaterial() || potion.getType() == XMaterial.LINGERING_POTION.parseMaterial()))
				{
					ItemStack predicatedResult = Utils.getPredicatedBrewedPotion(potion, ingredient);
					if(predicatedResult.getItemMeta() instanceof PotionMeta && FkPI.getInstance().getRulesManager().getRule(Rule.DISABLED_POTIONS).isDisabled(XPotionData.fromItemStack(predicatedResult)))
						return true;
				}
		return false;
	}

	@EventHandler
	public void event(PlayerItemConsumeEvent e)
	{
		if(e.getItem() != null && e.getItem().getItemMeta() instanceof PotionMeta && FkPI.getInstance().getRulesManager().getRule(Rule.DISABLED_POTIONS).isDisabled(XPotionData.fromItemStack(e.getItem())))
		{
			e.setCancelled(true);
			ChatUtils.sendMessage(e.getPlayer(), Messages.PLAYER_DISABLED_POTION_CONSUME);
		}
	}

	@EventHandler
	public void event(ProjectileLaunchEvent e)
	{
		if(e.getEntityType() == EntityType.SPLASH_POTION && FkPI.getInstance().getRulesManager().getRule(Rule.DISABLED_POTIONS).isDisabled(XPotionData.fromItemStack(((ThrownPotion) e.getEntity()).getItem())))
		{
			e.setCancelled(true);
			if(e.getEntity().getShooter() instanceof CommandSender)
				ChatUtils.sendMessage((CommandSender) e.getEntity().getShooter(), Messages.PLAYER_DISABLED_POTION_CONSUME);
		}
	}
}
