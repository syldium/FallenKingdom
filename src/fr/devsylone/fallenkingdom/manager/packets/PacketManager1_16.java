package fr.devsylone.fallenkingdom.manager.packets;

import com.mojang.datafixers.util.Pair;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PacketManager1_16 extends PacketManager1_14
{

    private static final boolean VERSION_1_16_2;

    private static final Constructor<?> PACKET_CHUNK;
    private static final Method PAIR_OF;

    static {
        try {
            PACKET_CHUNK = Arrays.stream(NMSUtils.nmsClass("network.protocol.game", "PacketPlayOutMapChunk").getConstructors())
                    .filter(constructor -> constructor.getParameterCount() > 1)
                    .findAny().orElseThrow(RuntimeException::new);
            PAIR_OF = Class.forName("com.mojang.datafixers.util.Pair").getMethod("of", Object.class, Object.class);

            VERSION_1_16_2 = PACKET_CHUNK.getParameterCount() < 3;
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    protected void sendEquipment(int id, ItemSlot slot, Material material)
    {
        try
        {
            ItemStack bukkitItem = new ItemStack(material);
            Object nmsItem = NMSUtils.getClass("CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null, bukkitItem);
            Object itemSlot = getEnumItemSlot(slot);
            Pair<?, ?> pair = Pair.of(itemSlot, nmsItem);

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
            Object chunkPacket = VERSION_1_16_2 ? PACKET_CHUNK.newInstance(nmsChunk, 25) : PACKET_CHUNK.newInstance(nmsChunk, 25, false);
            PacketUtils.sendPacket(p, chunkPacket);
        }catch(ReflectiveOperationException ex)
        {
            ex.printStackTrace();
        }
    }
}
