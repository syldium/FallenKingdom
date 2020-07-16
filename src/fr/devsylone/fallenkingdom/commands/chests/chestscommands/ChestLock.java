package fr.devsylone.fallenkingdom.commands.chests.chestscommands;

import com.google.common.collect.Sets;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkPlayerCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.XAdvancement;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChestLock extends FkPlayerCommand implements Listener {

    private final String INVENTORY_NAME = ChatColor.YELLOW + "Advancements";

    private final Map<AdvancementRoot, List<ItemStack>> representations = new HashMap<>();

    public ChestLock() {
        super("lock", "[advancement] [chest]", Messages.CMD_MAP_CHEST_LOCK, CommandPermission.ADMIN);
        Bukkit.getPluginManager().registerEvents(this, Fk.getInstance());
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
                for (AdvancementRoot root : representations.keySet()) {
                    ItemStack item = root.itemStack;
                    if (item != null)
                        inventory.addItem(item);
                }
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
        AdvancementRoot root = new AdvancementRoot("Achievements");
        ItemStack itemStack = new ItemStack(Material.GRASS);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.YELLOW + "Minecraft");
        meta.setLore(Collections.singletonList(ChatColor.RESET.toString() + ChatColor.GRAY + "Achievements"));
        itemStack.setItemMeta(meta);
        root.setItemStack(itemStack);
        representations.put(root, new ArrayList<>());
        for (Object achievement : XAdvancement.getAchievements()) {
            representations.get(root).add(XAdvancement.getAchievementIcon(achievement));
        }
    }

    private void buildAdvancementsRepresentation() {
        // Listing par catégorie des advancements
        Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
        while (iterator.hasNext()) {
            Advancement advancement = iterator.next();

            // Récupération de la catégorie
            int index = advancement.getKey().toString().lastIndexOf("/");
            if (index < 0) {
                index = advancement.getKey().toString().length();
            }
            String root = advancement.getKey().toString().substring(0, index);

            if (root.equals("minecraft:recipes")) {
                continue;
            }

            AdvancementRoot advancementRoot = null;
            for (AdvancementRoot r : representations.keySet()) {
                if (r.name.equals(root)) {
                    advancementRoot = r;
                }
            }
            if (advancementRoot == null) {
                advancementRoot = new AdvancementRoot(root);
            }
            List<ItemStack> group = representations.computeIfAbsent(advancementRoot, l -> new ArrayList<>());
            if (advancement.getKey().toString().contains("root")){
                advancementRoot.setItemStack(XAdvancement.getAdvancementIcon(advancement));
            }
            group.add(XAdvancement.getAdvancementIcon(advancement));
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event)
    {
        if(!event.getView().getTitle().contains(INVENTORY_NAME) || event.getClickedInventory() instanceof PlayerInventory || event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null)
            return;

        event.setCancelled(true);
        List<String> lore = event.getCurrentItem().getItemMeta().getLore();
        if (lore == null || lore.size() < 1) {
            return;
        }
        String root = ChatColor.stripColor(lore.get(0));
        List<ItemStack> representations = null;
        if (root.equals("Achievements")) { // Pseudo catégorie
            representations = this.representations.values().iterator().next();
        } else {
            for (AdvancementRoot r : this.representations.keySet()) {
                if (root.equals(r.name + "/root")) {
                    representations = this.representations.get(r);
                }
            }
        }

        if (representations == null) {
            setLock(event.getWhoClicked(), root);
            return;
        }

        Inventory inventory = Bukkit.createInventory(event.getWhoClicked(), ((representations.size() + 8) / 9) * 9, INVENTORY_NAME);
        for (ItemStack itemStack : representations) {
            inventory.addItem(itemStack);
        }
        event.getWhoClicked().openInventory(inventory);
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

    static class AdvancementRoot {

        private final String name;
        private ItemStack itemStack;

        AdvancementRoot(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AdvancementRoot that = (AdvancementRoot) o;
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
