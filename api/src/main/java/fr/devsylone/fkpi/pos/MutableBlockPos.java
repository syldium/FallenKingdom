package fr.devsylone.fkpi.pos;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static fr.devsylone.fkpi.pos.MathHelper.floor;

public final class MutableBlockPos implements Vector3i, Cloneable {

    private static final long serialVersionUID = 265706823254171054L;
    private int x, y, z;

    public MutableBlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public MutableBlockPos(double x, double y, double z) {
        this(floor(x), floor(y), floor(z));
    }

    public MutableBlockPos(@NotNull Vector3i pos) {
        this(pos.x(), pos.y(), pos.z());
    }

    @Override
    public int x() {
        return this.x;
    }

    @Override
    public int y() {
        return this.y;
    }

    @Override
    public int z() {
        return this.z;
    }

    @Override @Contract("_, _, _ -> this")
    public @NotNull MutableBlockPos set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override @Contract("_, _, _ -> this")
    public @NotNull MutableBlockPos add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @Override @Contract(value = "-> new", pure = true)
    public @NotNull BlockPos asImmutable() {
        return new BlockPos(this.x, this.y, this.z);
    }

    @Override @Contract(value = "-> this", pure = true)
    public @NotNull MutableBlockPos asMutable() {
        return this;
    }

    @Override @Contract("_ -> this")
    public @NotNull MutableBlockPos setX(int x) {
        this.x = x;
        return this;
    }

    @Override @Contract("_ -> this")
    public @NotNull MutableBlockPos setY(int y) {
        this.y = y;
        return this;
    }

    @Override @Contract("_ -> this")
    public @NotNull MutableBlockPos setZ(int z) {
        this.z = z;
        return this;
    }

    @Override
    public @NotNull MutableBlockPos clone() {
        try {
            return (MutableBlockPos) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MutableBlockPos blockPos = (MutableBlockPos) o;
        return this.x == blockPos.x && this.y == blockPos.y && this.z == blockPos.z;
    }

    @Override
    public int hashCode() {
        return (this.x * 31 + this.y) * 31 + this.z;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}
