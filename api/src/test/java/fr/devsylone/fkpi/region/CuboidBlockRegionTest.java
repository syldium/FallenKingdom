package fr.devsylone.fkpi.region;

import fr.devsylone.fkpi.util.BlockPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public class CuboidBlockRegionTest {

    @Test
    public void contains_0radius() {
        final BlockRegion region = new CuboidBlockRegion(new BlockPos(-50, 50, 10), 0);
        assertTrue(region.contains(-50, 50, 10));
        assertFalse(region.contains(-50, 51, 10));
    }

    @Test
    public void contains_5radius() {
        final BlockRegion region = new CuboidBlockRegion(new BlockPos(-50, 50, 10), 5);
        assertTrue(region.contains(-50, 50, 10));
        assertTrue(region.contains(-50, 51, 10));
        assertTrue(region.contains(-55, 50, 12));
        assertFalse(region.contains(-56, 10, 5));
        assertFalse(region.contains(-53, 54, 16));
    }

    @Test
    public void expand_returnSelf() {
        final ExpandableBlockRegion region = new CuboidBlockRegion(-10, -10, -10, 10, 10, 10);
        assertSame(region, region.expand(4, 6, 10));
    }

    @Test
    public void expand() {
        final ExpandableBlockRegion region = new CuboidBlockRegion(-10, -10, -10, 10, 10, 10);
        final BlockRegion expanded = region.expand(0, 0, 20);
        assertNotSame(region, expanded);

        assertTrue(region.contains(0, 3, 10));
        assertFalse(region.contains(0, 3, 20));
        assertTrue(expanded.contains(0, 3, 10));
        assertTrue(expanded.contains(0, 3, 20));
    }


    private static void assertTrue(boolean actual) {
        org.junit.jupiter.api.Assertions.assertTrue(actual, "The region should contain this position");
    }

    private static void assertFalse(boolean actual) {
        org.junit.jupiter.api.Assertions.assertFalse(actual, "The region should not contain this position.");
    }
}
