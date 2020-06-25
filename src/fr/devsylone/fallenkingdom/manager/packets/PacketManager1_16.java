package fr.devsylone.fallenkingdom.manager.packets;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class PacketManager1_16 extends PacketManager1_14
{

    public PacketManager1_16()
    {
        try
        {
            NMSUtils.register("com.mojang.datafixers.util.Pair");
        }catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void sendEquipment(int id, int slot, Material material)
    {
        try
        {
            ItemStack bukkitItem = new ItemStack(material);
            Object itemSlot = NMSUtils.getClass("EnumItemSlot").getDeclaredField(slot == BIG_ITEM ? "HEAD" : "MAINHAND").get(null);
            Object nmsItem = NMSUtils.getClass("CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null, bukkitItem);
            Object pair = NMSUtils.getClass("Pair").getMethod("of", Object.class, Object.class).invoke(null, itemSlot, nmsItem);

            Object armors = NMSUtils.getClass("PacketPlayOutEntityEquipment").getConstructor(int.class, List.class).newInstance(id, Collections.singletonList(pair));
            PacketUtils.sendPacket(getPlayer(id), armors);
        }catch(ReflectiveOperationException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void sendChunkReset(Player p, Chunk c)
    {
        try
        {
            Object nmsChunk = NMSUtils.getClass("World").getDeclaredMethod("getChunkAt", int.class, int.class).invoke(PacketUtils.getNMSWorld(p.getWorld()), c.getX(), c.getZ());
            Object chunkPacket = NMSUtils.getClass("PacketPlayOutMapChunk").getConstructor(NMSUtils.getClass("Chunk"), int.class, boolean.class).newInstance(nmsChunk, 25, false);

            PacketUtils.sendPacket(p, chunkPacket);
        }catch(ReflectiveOperationException ex)
        {
            ex.printStackTrace();
        }
    }
}
