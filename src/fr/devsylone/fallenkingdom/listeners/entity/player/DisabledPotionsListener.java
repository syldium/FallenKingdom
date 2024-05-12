package fr.devsylone.fallenkingdom.listeners.entity.player;

import com.cryptomorin.xseries.XMaterial;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.PotionUtils;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.DisabledPotions;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.util.XPotionData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
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

import java.util.Arrays;

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

		DisabledPotions rule = FkPI.getInstance().getRulesManager().getRule(Rule.DISABLED_POTIONS);
		for (ItemStack potion : PotionUtils.getBrewedPotions(potions, ingredient)) {
			if (rule.isDisabled(XPotionData.fromItemStack(potion))) {
				return true;
			}
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
		if(e.getEntity() instanceof ThrownPotion || Version.VersionType.V1_9_V1_12.isHigherOrEqual() && e.getEntity() instanceof Arrow)
		{
			Projectile projectile = e.getEntity();
			XPotionData potionData = XPotionData.fromProjectile(e.getEntity());
			if(potionData == null)
				return;

			if(FkPI.getInstance().getRulesManager().getRule(Rule.DISABLED_POTIONS).isDisabled(potionData))
			{
				if(projectile instanceof Arrow)
					projectile.getWorld().spawnArrow(projectile.getLocation(), projectile.getVelocity(), (float) projectile.getVelocity().length(), 12f);
				e.setCancelled(true);

				if(e.getEntity().getShooter() instanceof CommandSender)
					ChatUtils.sendMessage((CommandSender) e.getEntity().getShooter(), Messages.PLAYER_DISABLED_POTION_CONSUME);
			}
		}
	}
}
