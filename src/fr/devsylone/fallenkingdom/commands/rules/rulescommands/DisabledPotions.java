package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import java.util.Collections;
import java.util.List;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.util.XPotionData;

public class DisabledPotions extends FkPlayerCommand implements Listener
{
	private static final String DISABLED_POTIONS_INVENTORY_TITLE = Messages.INVENTORY_POTION_TITLE.getMessage();
	private static final String LORE_ENABLED = Messages.INVENTORY_POTION_ENABLE.getMessage();
	private static final String LORE_DISABLED = Messages.INVENTORY_POTION_DISABLE.getMessage();
	private static final ItemStack DISABLE_AMPLIFIED_POTIONS_ITEM;
	static
	{
		DISABLE_AMPLIFIED_POTIONS_ITEM = XMaterial.PLAYER_HEAD.parseItem();
		SkullMeta meta = SkullUtils.getSkullByValue((SkullMeta) DISABLE_AMPLIFIED_POTIONS_ITEM.getItemMeta(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTZmYWI5OTFkMDgzOTkzY2I4M2U0YmNmNDRhMGI2Y2VmYWM2NDdkNDE4OWVlOWNiODIzZTljYzE1NzFlMzgifX19");
		meta.setDisplayName(Messages.INVENTORY_POTION_LEVEL_II.getMessage());
		DISABLE_AMPLIFIED_POTIONS_ITEM.setItemMeta(meta);
	}

	public DisabledPotions()
	{
		super("disabledPotions", Messages.CMD_MAP_RULES_DISABLED_POTIONS, CommandPermission.ADMIN);
		Bukkit.getPluginManager().registerEvents(this, Fk.getInstance());
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent event)
	{
		if(!event.getView().getTitle().equals(DISABLED_POTIONS_INVENTORY_TITLE) || event.getClickedInventory() instanceof PlayerInventory || event.getCurrentItem() == null)
			return;

		event.setCancelled(true);

		if(event.getCurrentItem().getType() == XMaterial.POTION.parseMaterial())
			click(event.getCurrentItem());

		else if(event.getCurrentItem().getType() == DISABLE_AMPLIFIED_POTIONS_ITEM.getType())
		{
			for(ItemStack item : event.getClickedInventory().getContents())
				if(item != null && item.getAmount() == 1 && item.getItemMeta() instanceof PotionMeta && XPotionData.fromItemStack(item).isUpgraded())
					click(item);
		}

		event.getWhoClicked().openInventory(createInventory());

	}

	private void click(ItemStack potionItem)
	{
		if(potionItem.getType() != XMaterial.POTION.parseMaterial())
			return;

		XPotionData data = XPotionData.fromItemStack(potionItem);

		/*
		 * amount = 1 -> potion actuellement autorisée
		 * amount = 0 ou 64 -> potion actuellement désactivée
		 */
		if(potionItem.getAmount() != 1)
		{
			if(getRule().enablePotion(data))
				broadcast(Messages.INVENTORY_POTION_ENABLE_CLICK.getMessage().replace("%potion%", data.getType().name() + (data.isExtended() ? " + redstone" : data.isUpgraded() ? " + glowstone" : "")));
		}
		else
		{
			if(getRule().disablePotion(data))
				broadcast(Messages.INVENTORY_POTION_DISABLE_CLICK.getMessage().replace("%potion%", data.getType().name() + (data.isExtended() ? " + redstone" : data.isUpgraded() ? " + glowstone" : "")));
		}
	}

	private Inventory createInventory()
	{
		Inventory potionsInv = Bukkit.createInventory(null, 6 * 9, DISABLED_POTIONS_INVENTORY_TITLE);

		for(int i = 0; i < 4; i++)
			potionsInv.setItem(i, XMaterial.CYAN_STAINED_GLASS_PANE.parseItem());

		potionsInv.setItem(4, DISABLE_AMPLIFIED_POTIONS_ITEM);

		for(int i = 5; i < 9; i++)
			potionsInv.setItem(i, XMaterial.CYAN_STAINED_GLASS_PANE.parseItem());

		for(PotionType type : PotionType.values())
		{
			if(type.getEffectType() == null)
				continue;
			
			ItemStack potionItem = XMaterial.POTION.parseItem();
			PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
			XPotionData potionData = new XPotionData(type);
			potionMeta.setLore(Collections.singletonList(getRule().isDisabled(potionData) ? LORE_DISABLED : LORE_ENABLED));
			potionItem.setItemMeta(potionMeta);
			potionData.applyTo(potionItem);
			potionItem.setAmount(getRule().isDisabled(potionData) ? Bukkit.getVersion().contains("1.8") ? 0 : 64 : 1);
			potionsInv.addItem(potionItem);

			if(XPotionData.isUpgradable(type))
			{
				potionData = new XPotionData(type, false, true);
				potionMeta.setLore(Collections.singletonList(getRule().isDisabled(potionData) ? LORE_DISABLED : LORE_ENABLED));
				potionItem.setItemMeta(potionMeta);
				potionData.applyTo(potionItem);
				potionItem.setAmount(getRule().isDisabled(potionData) ? Bukkit.getVersion().contains("1.8") ? 0 : 64 : 1);
				potionsInv.addItem(potionItem);
			}

			if(XPotionData.isExtendable(type))
			{
				potionData = new XPotionData(type, true, false);
				potionMeta.setLore(Collections.singletonList(getRule().isDisabled(potionData) ? LORE_DISABLED : LORE_ENABLED));
				potionItem.setItemMeta(potionMeta);
				potionData.applyTo(potionItem);
				potionItem.setAmount(getRule().isDisabled(potionData) ? Bukkit.getVersion().contains("1.8") ? 0 : 64 : 1);
				potionsInv.addItem(potionItem);
			}
		}
		return potionsInv;
	}

	private fr.devsylone.fkpi.rules.DisabledPotions getRule()
	{
		return FkPI.getInstance().getRulesManager().getRule(Rule.DISABLED_POTIONS);
	}

	@Override
	public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label) {
		sender.openInventory(createInventory());
		return CommandResult.SUCCESS;
	}
}
