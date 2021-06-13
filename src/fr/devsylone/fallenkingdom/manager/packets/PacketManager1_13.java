package fr.devsylone.fallenkingdom.manager.packets;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import fr.devsylone.fallenkingdom.version.component.FkBook;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    public void sendBlockChange(Player p, Location loc, Material newBlock)
    {
        p.sendBlockChange(loc, newBlock.createBlockData());
    }

    @Override
    public void openBook(final Player p, FkBook book)
    {
        final int slot = p.getInventory().getHeldItemSlot();
        final ItemStack original = p.getInventory().getItem(slot);

        p.getInventory().setItemInMainHand(book.asItemStack());

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
            }catch(ReflectiveOperationException ex)
            {
                ex.printStackTrace();
            }
        }, 5L);
    }

    @Override
    public void sendTitle(Player p, String title, String subtitle)
    {
        p.sendTitle(title, subtitle, 20, 20, 20);
    }

    @Override
    public void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut)
    {
        p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
}
