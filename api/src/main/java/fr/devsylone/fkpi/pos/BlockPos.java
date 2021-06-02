package fr.devsylone.fkpi.pos;

import com.google.errorprone.annotations.Immutable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static fr.devsylone.fkpi.pos.MathHelper.floor;

@Immutable
public final class BlockPos implements Vector3i {

    private static final long serialVersionUID = -8908158901699382030L;
    public static final BlockPos ZERO = new BlockPos(0, 0, 0);

    private final int x, y, z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos(double x, double y, double z) {
        this(floor(x), floor(y), floor(z));
    }

    public BlockPos(@NotNull Vector3i pos) {
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

    @Override @Contract(value = "_, _, _ -> new", pure = true)
    public @NotNull BlockPos set(int x, int y, int z) {
        return new BlockPos(x, y, z);
    }

    @Override @Contract(value = "_ -> new", pure = true)
    public @NotNull BlockPos setX(int x) {
        return new BlockPos(x, this.y, this.z);
    }

    @Override @Contract(value = "_ -> new", pure = true)
    public @NotNull BlockPos setY(int y) {
        return new BlockPos(this.x, y, this.z);
    }

    @Override @Contract(value = "_ -> new", pure = true)
    public @NotNull BlockPos setZ(int z) {
        return new BlockPos(this.x, this.y, z);
    }

    @Override @Contract(value = "_, _, _ -> new", pure = true)
    public @NotNull BlockPos add(int x, int y, int z) {
        return new BlockPos(this.x + x, this.y + y, this.z + z);
    }

    @Override @Contract(value = "-> this", pure = true)
    public @NotNull BlockPos asImmutable() {
        return this;
    }

    @Override @Contract(value = "-> new", pure = true)
    public @NotNull MutableBlockPos asMutable() {
        return new MutableBlockPos(this.x, this.y, this.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final BlockPos blockPos = (BlockPos) o;
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
