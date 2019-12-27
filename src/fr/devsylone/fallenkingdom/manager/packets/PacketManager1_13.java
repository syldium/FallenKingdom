package fr.devsylone.fallenkingdom.manager.packets;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PacketManager1_13 extends PacketManager1_9 {

    public PacketManager1_13()
    {
        try
        {
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutEntityMetadata");
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutSpawnEntity");
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutEntityDestroy");
            NMSUtils.register("net.minecraft.server._version_.DataWatcher$Item");
            NMSUtils.register("net.minecraft.server._version_.DataWatcherObject");
            NMSUtils.register("net.minecraft.server._version_.DataWatcherRegistry");
            NMSUtils.register("net.minecraft.server._version_.DataWatcherSerializer");
            NMSUtils.register("net.minecraft.server._version_.PlayerConnection");
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutEntityTeleport");
            NMSUtils.register("net.minecraft.server._version_.Packet");
            NMSUtils.register("net.minecraft.server._version_.ItemStack");
            NMSUtils.register("net.minecraft.server._version_.Block");
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutEntityEquipment");
            NMSUtils.register("net.minecraft.server._version_.EnumItemSlot");
            NMSUtils.register("net.minecraft.server._version_.BlockPosition");
            NMSUtils.register("net.minecraft.server._version_.IBlockAccess");
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutBlockChange");
            NMSUtils.register("net.minecraft.server._version_.World");
            NMSUtils.register("net.minecraft.server._version_.Chunk");
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutMapChunk");
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutTitle");
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutTitle$EnumTitleAction");
            NMSUtils.register("net.minecraft.server._version_.IChatBaseComponent");
            NMSUtils.register("net.minecraft.server._version_.IChatBaseComponent$ChatSerializer");
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutCustomPayload");
            NMSUtils.register("net.minecraft.server._version_.MinecraftKey");
            NMSUtils.register("net.minecraft.server._version_.PacketDataSerializer");
            NMSUtils.register("org.bukkit.craftbukkit._version_.inventory.CraftItemStack");
        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void sendMetadata(int id, boolean visible, String customName)
    {
        try {

            Object metadata = NMSUtils.getClass("PacketPlayOutEntityMetadata").newInstance();

            PacketUtils.setField("a", id, metadata);
            List<Object> datas = new ArrayList<>();

            Constructor<?> itemConstructor = NMSUtils.getClass("DataWatcher$Item").getDeclaredConstructor(NMSUtils.getClass("DataWatcherObject"), Object.class);
            Constructor<?> dwoConstructor = NMSUtils.getClass("DataWatcherObject").getDeclaredConstructor(int.class, NMSUtils.getClass("DataWatcherSerializer"));

            if (!visible)
                datas.add(itemConstructor.newInstance(dwoConstructor.newInstance(0, NMSUtils.getClass("DataWatcherRegistry").getDeclaredField("a").get(null)), (byte) 32));

            if(customName != null && !customName.isEmpty())
            {
                datas.add(itemConstructor.newInstance(dwoConstructor.newInstance(2, NMSUtils.getClass("DataWatcherRegistry").getDeclaredField("f").get(null)), Optional.of(Array.get(NMSUtils.obcClass("util.CraftChatMessage").getDeclaredMethod("fromString", String.class).invoke(null, customName), 0))));
                datas.add(itemConstructor.newInstance(dwoConstructor.newInstance(3, NMSUtils.getClass("DataWatcherRegistry").getDeclaredField("i").get(null)), true));
            }

            for(Object item : datas)
                NMSUtils.getClass("DataWatcher$Item").getMethod("a", boolean.class).invoke(item, false);

            PacketUtils.setField("b", datas, metadata);

            PacketUtils.sendPacket(getPlayer(id), metadata);
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void sendBlockChange(Player p, Location loc, Material newBlock)
    {
        Material oldMat = loc.getWorld().getBlockAt(loc.getBlockX(), 0, loc.getBlockZ()).getType();
        loc.getWorld().getBlockAt(loc.getBlockX(), 0, loc.getBlockZ()).setType(newBlock);
        try
        {
            Object blockPositionSet = NMSUtils.getClass("BlockPosition").getConstructor(int.class, int.class, int.class).newInstance(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            Object blockPositionGet = NMSUtils.getClass("BlockPosition").getConstructor(int.class, int.class, int.class).newInstance(loc.getBlockX(), 0, loc.getBlockZ());

            Object change = NMSUtils.getClass("PacketPlayOutBlockChange").getConstructor(NMSUtils.getClass("IBlockAccess"), NMSUtils.getClass("BlockPosition")).newInstance(PacketUtils.getNMSWorld(p.getWorld()), blockPositionSet);
            PacketUtils.setField("block", NMSUtils.getClass("World").getDeclaredMethod("getType", NMSUtils.getClass("BlockPosition")).invoke(PacketUtils.getNMSWorld(p.getWorld()), blockPositionGet), change);

            PacketUtils.sendPacket(p, change);
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
        loc.getWorld().getBlockAt(loc.getBlockX(), 0, loc.getBlockZ()).setType(oldMat);
    }

    @Override
    public void openBook(final Player p, String nbtTags)
    {
        final int slot = p.getInventory().getHeldItemSlot();
        final ItemStack original = p.getInventory().getItem(slot);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "replaceitem entity " + p.getName() + " container." + slot + " minecraft:written_book" + nbtTags);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> {
            try
            {
                if(original != null && original.getType() != Material.AIR)
                    p.getWorld().dropItem(p.getLocation(), original).setPickupDelay(0);

                p.getInventory().setHeldItemSlot(slot);

                ByteBuf buf = Unpooled.buffer(256);
                buf.setByte(0, (byte) 0);
                buf.writerIndex(1);

                Object minecraftKey = NMSUtils.getClass("MinecraftKey").getDeclaredConstructor(String.class).newInstance("minecraft:book_open");
                Object packetDataSerializer = NMSUtils.getClass("PacketDataSerializer").getDeclaredConstructor(ByteBuf.class).newInstance(buf);

                Object packet = NMSUtils.getClass("PacketPlayOutCustomPayload").getDeclaredConstructor(NMSUtils.getClass("MinecraftKey"), NMSUtils.getClass("PacketDataSerializer")).newInstance(minecraftKey, packetDataSerializer);
                PacketUtils.sendPacket(p, packet);
            }catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }, 5L);
    }
}
