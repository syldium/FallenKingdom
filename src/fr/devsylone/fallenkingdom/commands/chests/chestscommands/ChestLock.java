package fr.devsylone.fallenkingdom.commands.chests.chestscommands;

import com.google.common.collect.Sets;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.XAdvancement;
import fr.devsylone.fallenkingdom.version.advancement.BukkitAdvancement;
import fr.devsylone.fallenkingdom.version.advancement.PaperAdvancement;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.devsylone.fallenkingdom.version.Version.classExists;

public class ChestLock extends FkPlayerCommand implements Listener {

    private final String INVENTORY_NAME = ChatColor.YELLOW + "Advancements";

    private final List<ItemStack> categories = new ArrayList<>();
    private final Map<String, List<ItemStack>> representations = new HashMap<>();
    private final Set<Inventory> inventories = new HashSet<>();

    public ChestLock() {
        super("lock", "[advancement] [chest]", Messages.CMD_MAP_CHEST_LOCK, CommandRole.ADMIN);
        // noinspection ConstantConditions -> test env
        if (Bukkit.getServer() != null) {
            Bukkit.getPluginManager().registerEvents(this, Fk.getInstance());
        }
    }

    @Override
    public CommandResult execute(Fk plugin, Player sender, FkPlayer fkp, List<String> args, String label) {
        if (args.size() < 1) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                if (representations.size() < 1) {
                    if (XAdvancement.isAdvancement()) {
                        buildAdvancementsRepresentation();
                    } else {
                        buildAchievementRepresentation();
                    }
                }
                Inventory inventory = Bukkit.createInventory(sender, 9 * 3, INVENTORY_NAME);
                inventory.addItem(categories.toArray(new ItemStack[0]));
                inventories.add(inventory);
                if (sender != null && sender.isOnline()) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> sender.openInventory(inventory));
                }
            });
            return CommandResult.SUCCESS;
        }

        String advancement = args.get(0);
        if (XAdvancement.exist(advancement)) {
            if (args.size() < 2) {
                return setLock(sender, advancement);
            }
            return setLock(ArgumentParser.getLockedChest(args.get(1)), advancement);
        }

        TranslatableComponent component = new TranslatableComponent(XAdvancement.isAdvancement() ? "advancement.advancementNotFound" : "commands.achievement.unknownAchievement");
        component.addWith(advancement);
        component.setColor(ChatColor.RED);
        sender.spigot().sendMessage(component);
        return CommandResult.INVALID_ARGS;
    }

    private void buildAchievementRepresentation() {
        // Simple listing des achievements
        ItemStack itemStack = new ItemStack(Material.GRASS);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.YELLOW + "Minecraft");
        meta.setLore(Collections.singletonList(ChatColor.RESET.toString() + ChatColor.GRAY + "Achievements"));
        itemStack.setItemMeta(meta);
        categories.add(itemStack);
        representations.put("Achievements", new ArrayList<>());
        for (Object achievement : XAdvancement.getAchievements()) {
            representations.get("Achievements").add(XAdvancement.getAchievementIcon(achievement));
        }
    }

    private void buildAdvancementsRepresentation() {
        if (classExists("io.papermc.paper.advancement.AdvancementDisplay")) {
            PaperAdvancement.buildAdvancements(this.categories, this.representations);
        } else {
            BukkitAdvancement.buildAdvancements(this.categories, this.representations);
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event)
    {
        if(event.getClickedInventory() instanceof PlayerInventory || !inventories.contains(event.getInventory()) || event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null)
            return;

        event.setCancelled(true);
        List<String> lore = event.getCurrentItem().getItemMeta().getLore();
        if (lore == null || lore.size() < 1) {
            return;
        }
        String root = ChatColor.stripColor(lore.get(0));
        List<ItemStack> representations;
        if (root.equals("Achievements")) { // Pseudo catégorie
            representations = this.representations.values().iterator().next();
        } else {
            representations = this.representations.get(root);
        }

        if (representations == null) {
            setLock(event.getWhoClicked(), root);
            return;
        }

        inventories.remove(event.getInventory());
        Inventory inventory = Bukkit.createInventory(event.getWhoClicked(), ((representations.size() + 8) / 9) * 9, INVENTORY_NAME);
        for (ItemStack itemStack : representations) {
            inventory.addItem(itemStack);
        }
        inventories.add(inventory);
        event.getWhoClicked().openInventory(inventory);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        inventories.remove(event.getInventory());
    }

    private CommandResult setLock(LivingEntity livingEntity, String advancement) {
        Block target = livingEntity.getTargetBlock(Sets.newHashSet(Material.AIR), 10);
        LockedChest chest = FkPI.getInstance().getLockedChestsManager().getChestAt(target.getLocation());
        if (chest == null) {
            ChatUtils.sendMessage(livingEntity, Messages.CMD_ERROR_NOT_LOCKED_CHEST);
            return CommandResult.INVALID_ARGS;
        }
        return setLock(chest, advancement);
    }

    private CommandResult setLock(LockedChest chest, String advancement) {
        chest.setRequiredAdvancement(advancement);
        broadcast(Messages.CMD_LOCKED_CHEST_LOCKED.getMessage()
                .replace("%name%", chest.getName())
                .replace("%day%", String.valueOf(chest.getUnlockDay()))
                .replace("%advancement%", advancement)
        );
        return CommandResult.SUCCESS;
    }
}
