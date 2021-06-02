package fr.devsylone.fallenkingdom.team;

import fr.devsylone.fkpi.team.ChestRoom;
import fr.devsylone.fkpi.pos.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChestRoomTest {

    @Test
    public void registerChest() {
        final ChestRoom room = new ChestRoomImpl(Collections.emptySet());
        final BlockPos chest = new BlockPos(230, 71, -119);
        assertFalse(room.contains(chest), "The chest room should not contain the chest.");
        assertTrue(room.register(chest));
        assertTrue(room.contains(chest), "The chest room should contain the chest.");
    }

    @Test
    public void unregisterChest() {
        final BlockPos chest = new BlockPos(-405, 78, 30);
        final ChestRoom room = new ChestRoomImpl(Collections.singleton(chest));
        assertTrue(room.contains(chest), "The chest room should contain the chest.");
        assertTrue(room.unregister(chest));
        assertFalse(room.contains(chest), "The chest room should not contain the chest.");
    }
}
