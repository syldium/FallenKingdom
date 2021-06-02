package fr.devsylone.fkpi.region;

import fr.devsylone.fkpi.pos.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class BlockRegionTest {

    @Test
    public void fromPositions_empty() {
        final BlockRegion region = BlockRegion.fromPositions(Collections.emptySet());
        assertSame(BlockRegion.EMPTY, region);
    }

    @Test
    public void fromPositions_single() {
        final BlockPos pos = new BlockPos(140, 50, 90);
        final BlockRegion region = BlockRegion.fromPositions(Collections.singleton(pos));
        assertEquals(new CuboidBlockRegion(pos, 0), region);
    }

    @Test
    public void fromPositions_multiple() {
        final BlockRegion region = BlockRegion.fromPositions(Arrays.asList(
                new BlockPos(40, 20, 70),
                new BlockPos(50, 12,  70),
                new BlockPos(30, 15, 32)
        ));
        assertEquals(new CuboidBlockRegion(30, 12, 32, 50, 20, 70), region);
    }

    @Test
    public void expand_empty() {
        final ExpandableBlockRegion region = BlockRegion.EMPTY;
        assertEquals(new CuboidBlockRegion(50, 10, 20, 0), region.expand(50, 10, 20));
    }
}
