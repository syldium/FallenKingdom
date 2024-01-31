package fr.devsylone.fallenkingdom.listeners.block;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import fr.devsylone.fkpi.lockedchests.LockedChestLoadout;
import fr.devsylone.fkpi.lockedchests.LockedChest.ChestState;
import fr.devsylone.fkpi.managers.LockedChestsManager;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LockedChestInteractListener implements Listener {

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        if (!Fk.getInstance().getWorldManager().isAffected(e.getPlayer().getWorld())
                || !e.getClickedBlock().getType().equals(Material.CHEST)) {
            return;
        }

        Chest chestBlock = (Chest) e.getClickedBlock().getState();
        LockedChestsManager manager = Fk.getInstance().getFkPI().getLockedChestsManager();
        if (manager.getChestAt(chestBlock.getLocation()) == null)
            return;
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            e.setCancelled(true);
            return;
        }
        final LockedChest chest = manager.getChestAt(e.getClickedBlock().getLocation());
        if (chest.getState() == ChestState.DONE) {
            return;
        }

        // Get next chest loadout if it exists, or exit.
        int unlockDay = chest.getUnlockDay();
        LockedChestLoadout loadout = chest.getUnlockLoadout();

        // Players in creative mode bypass chest lock
        if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            e.getPlayer().sendMessage(ChatUtils.ALERT + Messages.PLAYER_OPEN_LOCKED_CHEST_CREATIVE);
            setChestInventory(chestBlock, loadout);
            return;
        }
        if (unlockDay > Fk.getInstance().getGame().getDay()) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Messages.PLAYER_LOCKED_CHEST_TOO_EARLY.getMessage()
                    .replace("%day%", String.valueOf(chest.getUnlockDay())));
            return;
        }
        if (!chest.hasAccess(e.getPlayer())) {
            e.setCancelled(true);
            ChatUtils.sendMessage(e.getPlayer(), Messages.PLAYER_LOCKED_CHEST_NO_ACCESS);
            return;
        }

        // Si le joueur vise la partie supérieure du coffre, l'armorstand va se placer entre lui et
        // le coffre, et le client n'essayera plus de l'ouvrir, ce qui n'est pas voulu.
        switch (chest.getState()) {
            case LOCKED:
                chest.setYFixByBlockFace(e.getBlockFace());
                chest.startUnlocking(e.getPlayer());
                setChestInventory(chestBlock, loadout);
                e.setCancelled(true);
                break;
            case UNLOCKING:
                chest.setYFixByBlockFace(e.getBlockFace());
                chest.updateLastInteract();
                e.setCancelled(true);
                break;
            default:
                break;
        }

    }

    private void setChestInventory(Chest chest, LockedChestLoadout loadout) {
        List<ItemStack> inventory = loadout.getInventory(chest.getLocation());
        if (inventory == null) {
            return;
        }
        chest.getInventory().clear();
        chest.getInventory().setContents(inventory.toArray(new ItemStack[0]));
    }

    public boolean isInvEmpty(Inventory inv) {
        for (ItemStack item : inv.getContents()) {
            if (item != null && !item.getType().equals(Material.AIR)) {
                return false;
            }
        }
        return true;
    }
}
