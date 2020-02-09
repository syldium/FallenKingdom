package fr.devsylone.fkpi.util;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.lang.reflect.Method;

public class BlockDescription implements Serializable
{
    private String blockName;
    private byte data = -1;

    public BlockDescription(String blockName)
    {
        if (blockName.contains(":")) {
            this.blockName = blockName.split(":")[0].toUpperCase();
            this.data = Byte.parseByte(blockName.split(":")[1]);
        } else {
            this.blockName = blockName.toUpperCase();
        }
    }

    public BlockDescription(String blockName, byte data)
    {
        this.blockName = blockName.toUpperCase();
        this.data = data;
    }

    public BlockDescription(Material material)
    {
        this.blockName = material.name();
    }

    @SuppressWarnings("deprecated")
    public BlockDescription(Block block)
    {
        this.blockName = block.getType().name();
        if (!XMaterial.isNewVersion())
            this.data = block.getData(); // 1.12.2-
    }

    public BlockDescription(ItemStack itemStack)
    {
        this.blockName = itemStack.getType().name();
        if (XMaterial.isNewVersion()) return;
        try {
            Method getDataMethod = ItemStack.class.getMethod("getData");
            Object data = getDataMethod.invoke(itemStack);

            Method getDataMethod2 = data.getClass().getMethod("getData");
            Object data2 = getDataMethod2.invoke(data);
            if (data2 instanceof Byte) {
                this.data = (byte) data2;
            }
        } catch (ReflectiveOperationException e) {

        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockDescription)) return false;
        BlockDescription other = (BlockDescription) obj;
        if (!this.blockName.equalsIgnoreCase(other.blockName)) {
            return false;
        }
        if (this.data < 0 || other.data < 0) {
            return true;
        }
        return this.data == other.data;
    }

    @Override
    public String toString()
    {
        if (this.data == -1) {
            return this.blockName;
        } else {
            return this.blockName + ":" + this.data;
        }
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }
}
