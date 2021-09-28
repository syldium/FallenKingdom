package fr.devsylone.fallenkingdom.util;

import fr.devsylone.fallenkingdom.MockUtils;
import fr.devsylone.fallenkingdom.utils.DistanceTree;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DistanceTreeTest {

    private final World world = requireNonNull(MockUtils.getConstantPlayer().getWorld(), "world");
    private final Location around = new Location(this.world, 100, 50, -100);

    @Test
    public void size() {
        final DistanceTree<Character> tree = new DistanceTree<>(this.around);
        assertTrue(tree.isEmpty(), "The tree should be empty.");
        assertEquals(0, tree.size(), "The tree shouldn't have any nodes.");
        tree.add(new Location(this.world, 110, 50, -100), 'A');
        assertFalse(tree.isEmpty());
        assertEquals(1, tree.size(), "The tree should have only one node.");

        final Location location = new Location(this.world, -80, 60, 100);
        tree.add(location, 'F');
        assertFalse(tree.isEmpty());
        assertEquals(2, tree.size(), "The tree should have two nodes.");
        tree.add(location, 'F');
        assertEquals(2, tree.size(), "The tree shouldn't have changed.");
    }

    @Test
    public void binarySearchTree() {
        final String shouldBeAdded = "The location should have been added to the tree.";
        final DistanceTree<Character> tree = new DistanceTree<>(this.around);
        assertNull(tree.nearest());
        assertTrue(tree.add(new Location(this.world, 50, 80, -100), 'U'), shouldBeAdded);
        assertEquals('U', tree.nearest());
        assertEquals('U', tree.farthest());
        assertEquals(Optional.of('U'), tree.find(0));

        assertTrue(tree.add(new Location(this.world, 90, 80, -100), 'W'), shouldBeAdded);
        assertEquals('W', tree.nearest());
        assertEquals('U', tree.farthest());
        assertEquals(Optional.of('W'), tree.find(0));
        assertEquals(Optional.of('U'), tree.find(1));

        /*
         *   U
         *  / \
         * W   S
         */
        assertTrue(tree.add(new Location(this.world, 100, 80, 30), 'S'), shouldBeAdded);
        assertEquals('W', tree.nearest());
        assertEquals('S', tree.farthest());
        assertEquals(Arrays.asList('W', 'U', 'S'), tree.toList());

        /*
         *   U
         *  / \
         * W   S
         *  \
         *   I
         */
        assertTrue(tree.add(new Location(this.world, 100, 80, -120), 'I'), shouldBeAdded);
        assertEquals('W', tree.nearest());
        assertEquals('S', tree.farthest());
        assertEquals(Optional.of('W'), tree.find(0));
        assertEquals(Optional.of('I'), tree.find(1));
        assertEquals(Optional.of('U'), tree.find(2));
        assertEquals(Optional.of('S'), tree.find(3));
        assertEquals(Arrays.asList('W', 'I', 'U', 'S'), tree.toList());
    }
}
