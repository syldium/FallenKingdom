package fr.devsylone.fallenkingdom.listeners.block;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.XBlock;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import fr.devsylone.fkpi.lockedchests.LockedChest.ChestState;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.Base;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

public class LockedChestInteractListener implements Listener
{

	@EventHandler
	public void interact(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (!Fk.getInstance().getWorldManager().isAffected(player.getWorld()))
			return;
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK || !XBlock.canBePartOfChestRoom(e.getClickedBlock().getType())) {
			return;
		}
		boolean hasAlwaysAccess = player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR;
		if (FkPI.getInstance().getRulesManager().getRule(Rule.PRIVATE_CHESTS) && !Fk.getInstance().getGame().isAssaultsEnabled() && !hasAlwaysAccess) {
			final Team playerTeam = FkPI.getInstance().getTeamManager().getPlayerTeam(player);
			final Team teamOwner = FkPI.getInstance().getTeamManager().getBase(e.getClickedBlock()).map(Base::getTeam).orElse(null);
			if (playerTeam != null && teamOwner != null && !playerTeam.equals(teamOwner)) {
				e.setCancelled(true);
				ChatUtils.sendMessage(player, Messages.PLAYER_BLOCK_PRIVATE.getMessage().replace("%team%", teamOwner.toString()));
				return;
			}
		}

		final LockedChest chest = FkPI.getInstance().getLockedChestsManager().getChestAt(e.getClickedBlock().getLocation());
		if (chest != null && chest.getState() != ChestState.UNLOCKED) {
			if (Version.VersionType.V1_13.isHigherOrEqual() && e.getClickedBlock().getState() instanceof Chest && isInvEmpty(((Chest) e.getClickedBlock().getState()).getBlockInventory())) {
				LootTable lootTable = chest.getLootTable();
				if (lootTable != null) {
					Chest state = (Chest) e.getClickedBlock().getState();
					state.getInventory().clear();
					state.setLootTable(lootTable);
					state.update(true);
				}
			}

			if(hasAlwaysAccess)
			{
				e.getPlayer().sendMessage(Messages.PREFIX_ALERT.getMessage() + Messages.PLAYER_OPEN_LOCKED_CHEST_CREATIVE);
				return;
			}
			
			e.setCancelled(true);
			if(chest.getUnlockDay() > Fk.getInstance().getGame().getDay())
			{
				e.getPlayer().sendMessage(Messages.PLAYER_LOCKED_CHEST_TOO_EARLY.getMessage().replace("%day%", String.valueOf(chest.getUnlockDay())));
				return;
			}
			if(!chest.hasAccess(e.getPlayer()))
			{
				ChatUtils.sendMessage(e.getPlayer(), Messages.PLAYER_LOCKED_CHEST_NO_ACCESS);
				return;
			}

			// Si le joueur vise la partie supérieure du coffre, l'armorstand va se placer entre lui et le coffre, et le client n'essayera plus de l'ouvrir, ce qui n'est pas voulu.
			chest.setYFixByBlockFace(e.getBlockFace());
			if(chest.getUnlocker() != e.getPlayer().getUniqueId())
				chest.startUnlocking(e.getPlayer());
			else
				chest.updateLastInteract();
		}

	}

	public boolean isInvEmpty(Inventory inv)
	{
		for (ItemStack item : inv.getContents()) {
			if (item != null && !item.getType().equals(Material.AIR)) {
				return false;
			}
		}
		return true;
	}
}
