package fr.devsylone.fkpi.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class OutlineSquareIterator implements Iterator<Location> {

    private int index;
    private int side;
    private int dx = 1;
    private int dz;
    private final int length;
    private final Location location;

    public OutlineSquareIterator(int length, Location location) {
        this.length = length;
        this.location = location;
    }

    public OutlineSquareIterator(int startX, int y, int startZ, int length, World world) {
        this(length, new Location(world, startX, y, startZ));
    }

    @Override
    public boolean hasNext() {
        return this.side < 4;
    }

    @Override
    public @NotNull Location next() {
        if (this.side > 3) {
            throw new NoSuchElementException();
        }
        this.location.setX(this.location.getBlockX() + this.dx);
        this.location.setZ(this.location.getBlockZ() + this.dz);
        if (++this.index >= this.length) {
            this.side++;
            int temp = this.dx;
            this.dx = -this.dz;
            this.dz = temp;
            this.index = 0;
        }
        return this.location;
    }

    public int size() {
        return this.length << 2;
    }
}
