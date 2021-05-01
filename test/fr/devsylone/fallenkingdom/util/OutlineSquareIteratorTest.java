package fr.devsylone.fallenkingdom.util;

import fr.devsylone.fkpi.util.OutlineSquareIterator;
import org.bukkit.Location;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

public class OutlineSquareIteratorTest {

    @Test
    public void traceContinuousSquare() {
        Location start = new Location(null, 50, 100, 150);
        List<Location> locations = new ArrayList<>();
        OutlineSquareIterator iterator = new OutlineSquareIterator(4, start.clone());
        iterator.forEachRemaining(locations::add);
        assertEquals(16, locations.size());
        assertEquals(16, iterator.size());
        assertFalse(iterator.hasNext());

        iterator = new OutlineSquareIterator(4, start.clone());
        Location next = iterator.next();
        while (iterator.hasNext()) {
            final int lastX = next.getBlockX();
            final int lastZ = next.getBlockZ();
            next = iterator.next();
            if (square(lastX - next.getBlockX()) + square(lastZ - next.getBlockZ()) != 1) {
                fail("The locations must follow each other.");
            }
        }
    }

    static int square(int n) {
        return n * n;
    }
}
