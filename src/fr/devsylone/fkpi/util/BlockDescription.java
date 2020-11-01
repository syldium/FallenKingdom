package fr.devsylone.fkpi.util;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Objects;

@Getter
public class BlockDescription
{
    private final Material material;
    private byte data = -1;

    public BlockDescription(String blockName)
    {
        if (blockName.contains(":")) {
            this.material = matchMaterial(blockName.split(":")[0]);
            this.data = Byte.parseByte(blockName.split(":")[1]);
        } else {
            this.material = matchMaterial(blockName);
        }
    }

    public BlockDescription(String blockName, byte data)
    {
        this.material = matchMaterial(blockName);
        this.data = data;
    }

    public BlockDescription(Material material)
    {
        this.material = material;
    }

    @SuppressWarnings("deprecated")
    public BlockDescription(Block block)
    {
        this.material = block.getType();
        if (!XMaterial.isNewVersion())
            this.data = block.getData(); // 1.12.2-
    }

    public BlockDescription(ItemStack itemStack)
    {
        this.material = itemStack.getType();
        if (XMaterial.isNewVersion()) return;
        try {
            Method getDataMethod = ItemStack.class.getMethod("getData");
            Object data = getDataMethod.invoke(itemStack);

            Method getDataMethod2 = data.getClass().getMethod("getData");
            Object data2 = getDataMethod2.invoke(data);
            if (data2 instanceof Byte) {
                this.data = (byte) data2;
            }
        } catch (ReflectiveOperationException ignored) {

        }
    }

    private Material matchMaterial(String name) {
        Material material = Material.matchMaterial(name);
        Preconditions.checkArgument(material != null, "Unknown material: " + name);
        return material;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockDescription)) return false;
        BlockDescription other = (BlockDescription) obj;
        if (this.material != other.material) {
            return false;
        }
        if (this.data < 0 || other.data < 0) {
            return true;
        }
        return this.data == other.data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.material, this.data);
    }

    @Override
    public String toString()
    {
        if (this.data == -1) {
            return this.material.name();
        } else {
            return this.material + ":" + this.data;
        }
    }
}
