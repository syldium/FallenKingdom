package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.PluginInventory;
import fr.devsylone.fallenkingdom.utils.XItemStack;
import fr.devsylone.fallenkingdom.version.Environment;
import fr.devsylone.fallenkingdom.version.potion.PotionIterator;
import fr.devsylone.fkpi.rules.Rule;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;

import com.cryptomorin.xseries.XMaterial;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.util.XPotionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.papermc.paper.datacomponent.item.ItemLore.lore;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand;

public class DisabledPotions extends FkPlayerCommand {

    private @Nullable Editor editor;

    public DisabledPotions() {
        super("disabledPotions", Messages.CMD_MAP_RULES_DISABLED_POTIONS, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label) {
        if (this.editor == null) {
            this.editor = new Editor(plugin);
        }
        sender.openInventory(this.editor.getInventory());
        return CommandResult.SUCCESS;
    }

    private class Editor implements PluginInventory {

        private final fr.devsylone.fkpi.rules.DisabledPotions rule;
        private final Inventory inventory;
        private final ItemStack disableAmplifiedPotionsItem;

        public Editor(@NotNull Fk plugin) {
            this.rule = plugin.getFkPI().getRulesManager().getRule(Rule.DISABLED_POTIONS);
            this.inventory = plugin.getServer().createInventory(this, 6 * 9, Messages.INVENTORY_POTION_TITLE.getMessage());

            this.disableAmplifiedPotionsItem = XMaterial.PLAYER_HEAD.parseItem();
            SkullMeta meta = XItemStack.applyBase64Texture((SkullMeta) disableAmplifiedPotionsItem.getItemMeta(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTZmYWI5OTFkMDgzOTkzY2I4M2U0YmNmNDRhMGI2Y2VmYWM2NDdkNDE4OWVlOWNiODIzZTljYzE1NzFlMzgifX19");
            meta.setDisplayName(Messages.INVENTORY_POTION_LEVEL_II.getMessage());
            disableAmplifiedPotionsItem.setItemMeta(meta);

            final ItemStack glassPane = XMaterial.CYAN_STAINED_GLASS_PANE.parseItem();
            for (int i = 0; i < 9; i++) {
                this.inventory.setItem(i, glassPane);
            }
            this.inventory.setItem(4, this.disableAmplifiedPotionsItem);

            Iterator<XPotionData> iterator = PotionIterator.create(PotionType.values());

            int slot = 8;
            while (iterator.hasNext()) {
                XPotionData potionData = iterator.next();
                if (potionData.getType().getEffectType() == null) {
                    continue;
                }

                ItemStack potionItem = XMaterial.POTION.parseItem();
                this.inventory.setItem(++slot, updateItem(potionItem, potionData));
            }
        }

        @Override
        public void onInventoryClick(@NotNull InventoryClickEvent event) {
            if (event.getCurrentItem() == null) {
                return;
            }
            event.setCancelled(true);

            XPotionData potionData = XPotionData.fromItemStack(event.getCurrentItem());
            if (potionData != null) {
                event.setCurrentItem(click(event.getCurrentItem(), potionData));
            } else if (event.getCurrentItem().getType() == this.disableAmplifiedPotionsItem.getType()) {
                final ItemStack[] contents = this.inventory.getContents();
                for (int i = 0; i < contents.length; i++) {
                    final ItemStack item = contents[i];
                    if (item != null && (potionData = XPotionData.fromItemStack(item)) != null && potionData.isUpgraded() && !this.rule.isDisabled(potionData)) {
                        contents[i] = click(item, potionData);
                    }
                }
                this.inventory.setContents(contents);
            }
        }

        private @NotNull ItemStack click(@NotNull ItemStack potionItem, @NotNull XPotionData data) {
            String potionName = data.getType().name();
            if (!PotionIterator.USE_SEPARATE_POTION_TYPES) {
                potionName += data.isExtended() ? " + redstone" : data.isUpgraded() ? " + glowstone" : "";
            }

            if (this.rule.togglePotion(data)) {
                broadcast(Messages.INVENTORY_POTION_ENABLE_CLICK.getMessage().replace("%potion%", potionName));
            } else {
                broadcast(Messages.INVENTORY_POTION_DISABLE_CLICK.getMessage().replace("%potion%", potionName));
            }
            potionItem = XMaterial.POTION.parseItem();
            updateItem(potionItem, data);
            return potionItem;
        }

        private @NotNull ItemStack updateItem(@NotNull ItemStack potionItem, @NotNull XPotionData potionData) {
            if (Environment.HAS_DATA_COMPONENTS) {
                return updateItemViaDataComponent(potionItem, potionData);
            } else {
                return updateItemViaItemMeta(potionItem, potionData);
            }
        }

        private @NotNull ItemStack updateItemViaItemMeta(@NotNull ItemStack potionItem, @NotNull XPotionData potionData) {
            potionData.applyTo(potionItem);
            final PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
            if (this.rule.isDisabled(potionData)) {
                Environment.setEnchantmentGlintOverride(potionMeta, true);
                potionMeta.setLore(Collections.singletonList(Messages.INVENTORY_POTION_DISABLE.getMessage()));
                potionItem.setAmount(64);
            } else {
                Environment.setEnchantmentGlintOverride(potionMeta, false);
                potionMeta.setLore(Collections.singletonList(Messages.INVENTORY_POTION_ENABLE.getMessage()));
                potionItem.setAmount(1);
            }
            potionItem.setItemMeta(potionMeta);
            return potionItem;
        }

        @SuppressWarnings("UnstableApiUsage")
        private @NotNull ItemStack updateItemViaDataComponent(@NotNull ItemStack potionItem, @NotNull XPotionData potionData) {
            potionData.applyTo(potionItem);
            Messages lore;
            if (this.rule.isDisabled(potionData)) {
                lore = Messages.INVENTORY_POTION_DISABLE;
                potionItem.setData(DataComponentTypes.DAMAGE, 9);
                potionItem.setData(DataComponentTypes.MAX_DAMAGE, 10);
                potionItem.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
            } else {
                lore = Messages.INVENTORY_POTION_ENABLE;
                potionItem.resetData(DataComponentTypes.DAMAGE);
                potionItem.resetData(DataComponentTypes.MAX_DAMAGE);
                potionItem.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
            }
            potionItem.setData(DataComponentTypes.LORE, lore().addLine(legacyAmpersand().deserialize(lore.getMessage())));
            return potionItem;
        }

        @Override
        public @NotNull Inventory getInventory() {
            return this.inventory;
        }
    }
}
