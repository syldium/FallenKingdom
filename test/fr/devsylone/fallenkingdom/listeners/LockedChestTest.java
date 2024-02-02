package fr.devsylone.fallenkingdom.listeners;

import fr.devsylone.fallenkingdom.MockUtils;
import fr.devsylone.fallenkingdom.game.GameHelper;
import fr.devsylone.fkpi.api.event.PlayerLockedChestInteractEvent;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import fr.devsylone.fkpi.lockedchests.LockedChest.ChestState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LockedChestTest {

    private World world = MockUtils.getServerMockSafe().getWorlds().get(0);
    private Location chestLocation = new Location(world, 100, 60, -200);
    private Block chest = world.getBlockAt(chestLocation);
    private LockedChest lockedChest;

    @BeforeEach
    public void init() {
        chest.setType(Material.CHEST);
        lockedChest = new LockedChest(chestLocation, "Super cool content");
        lockedChest.addChestLoadout(2, 10, -1, null, new ItemStack[]{});
        MockUtils.getPluginMockSafe().getFkPI().getLockedChestsManager().addOrEdit(lockedChest);
    }

    @Test
    public void unlock_TooEarly() {
        GameHelper.setDay(1);
        fireInteractEvent();
        assertEquals(LockedChest.ChestState.LOCKED, lockedChest.getState());
        assertNull(lockedChest.getUnlocker());
    }

    @Test
    public void unlock_Start() {
        GameHelper.setDay(2);
        assertEquals(lockedChest.getUnlockDay(), 2);
        fireInteractEvent();
        assertEventFired(LockedChest.ChestState.UNLOCKING, MockUtils.getConstantPlayer().getUniqueId());
        setLastInteractionTime(600L);
        MockUtils.getServerMockSafe().getScheduler().performOneTick();
        assertEquals(LockedChest.ChestState.UNLOCKING, lockedChest.getState());
        assertEquals(MockUtils.getConstantPlayer().getUniqueId(), lockedChest.getUnlocker());
    }

    @Test
    public void legacyChestsUnlock() {
        GameHelper.setDay(3);
        assertEquals(2, lockedChest.getUnlockDay());
        lockedChest.startUnlocking(MockUtils.getConstantPlayer());
        assertEventFired(LockedChest.ChestState.UNLOCKING, MockUtils.getConstantPlayer().getUniqueId());
        setStartUnlockingTime(20001L);
        MockUtils.getServerMockSafe().getScheduler().performOneTick();
        assertEventFired(LockedChest.ChestState.UNLOCKED, MockUtils.getConstantPlayer().getUniqueId());
    }

    @Test
    public void unlock_Abort() {
        GameHelper.setDay(2);
        lockedChest.startUnlocking(MockUtils.getConstantPlayer());
        setLastInteractionTime(1001L);
        MockUtils.getServerMockSafe().getScheduler().performOneTick();
    }

    @Test
    public void unlock_Success() {
        GameHelper.setDay(2);
        lockedChest.startUnlocking(MockUtils.getConstantPlayer());
        setStartUnlockingTime(20001L);
        MockUtils.getServerMockSafe().getScheduler().performOneTick();
        assertEventFired(LockedChest.ChestState.UNLOCKED, MockUtils.getConstantPlayer().getUniqueId());
    }

    @Test
    public void addLoadoutTest() {
        lockedChest.addChestLoadout(3, 10, -1, null, new ItemStack[0]);
        assertNotEquals(null, lockedChest.getLoadout(2));
        assertNotEquals(null, lockedChest.getLoadout(3));
        assertEquals(lockedChest.getUnlockLoadout(), lockedChest.getLoadout(2));
        lockedChest.addChestLoadout(1, 10, -1, null, new ItemStack[0]);
        assertNotEquals(null, lockedChest.getLoadout(1));
        assertEquals(lockedChest.getUnlockLoadout(), lockedChest.getLoadout(1));
    }

    @Test
    public void removeLoadoutTest() {
        assertEquals(ChestState.LOCKED, lockedChest.getState());
        lockedChest.addChestLoadout(3, 10, -1, null, new ItemStack[0]);
        lockedChest.removeLoadout(2);
        assertEquals(null, lockedChest.getLoadout(2));
        assertEquals(lockedChest.getLoadout(3), lockedChest.getUnlockLoadout());
        assertEquals(ChestState.LOCKED, lockedChest.getState());
        lockedChest.removeLoadout(3);
        assertEquals(null, lockedChest.getUnlockLoadout());
        assertEquals(ChestState.DONE, lockedChest.getState());
        lockedChest.addChestLoadout(1, 10, -1, null, new ItemStack[0]);
        assertEquals(ChestState.LOCKED, lockedChest.getState());
    }

    private void fireInteractEvent() {
        PlayerInteractEvent event = new PlayerInteractEvent(MockUtils.getConstantPlayer(), Action.RIGHT_CLICK_BLOCK, new ItemStack(Material.AIR), chest, BlockFace.EAST);
        MockUtils.getServerMockSafe().getPluginManager().callEvent(event);
    }

    private void setLastInteractionTime(long time) {
        try {
            Field lastInteractField = lockedChest.getClass().getDeclaredField("lastInteract");
            lastInteractField.setAccessible(true);
            lastInteractField.set(lockedChest, System.currentTimeMillis() - time);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private void setStartUnlockingTime(long time) {
        try {
            Field lastInteractField = lockedChest.getClass().getDeclaredField("startUnlocking");
            lastInteractField.setAccessible(true);
            lastInteractField.set(lockedChest, System.currentTimeMillis() - time);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private void assertEventFired(LockedChest.ChestState excepted, UUID unlocker) {
        MockUtils.getServerMockSafe().getPluginManager().assertEventFired(PlayerLockedChestInteractEvent.class, event ->
            lockedChest.equals(event.getChest()) && lockedChest.getState().equals(excepted) && lockedChest.getUnlocker().equals(unlocker)
        );
    }
}
