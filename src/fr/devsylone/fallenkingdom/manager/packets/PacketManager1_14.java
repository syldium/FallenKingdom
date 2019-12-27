package fr.devsylone.fallenkingdom.manager.packets;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PacketManager1_14 extends PacketManager1_13 {

    public PacketManager1_14()
    {
        try
        {
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutEntityMetadata");
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutSpawnEntity");
            NMSUtils.register("net.minecraft.server._version_.EntityTypes");
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
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutOpenBook");
            NMSUtils.register("net.minecraft.server._version_.EnumHand");
            NMSUtils.register("org.bukkit.craftbukkit._version_.inventory.CraftItemStack");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected int sendSpawn(Player p, Location loc)
    {
        if(loc == null && p != null)
            loc = p.getLocation();

        int id = lastid++;
        playerById.put(id, p.getUniqueId());
        try
        {
            Object spawn = NMSUtils.getClass("PacketPlayOutSpawnEntity").newInstance();
            PacketUtils.setField("a", id, spawn);
            PacketUtils.setField("b", UUID.randomUUID(), spawn);
            PacketUtils.setField("c", loc.getX(), spawn);
            PacketUtils.setField("d", loc.getY(), spawn);
            PacketUtils.setField("e", loc.getZ(), spawn);
            PacketUtils.setField("f", 0, spawn);
            PacketUtils.setField("g", 0, spawn);
            PacketUtils.setField("h", 0, spawn);
            PacketUtils.setField("i", 0, spawn);
            PacketUtils.setField("j", 0, spawn);
            PacketUtils.setField("k", NMSUtils.getClass("EntityTypes").getDeclaredField("ARMOR_STAND").get(null), spawn);
            PacketUtils.setField("l", 0, spawn);
            PacketUtils.sendPacket(p, spawn);
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return id;
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
                Object packet = NMSUtils.getClass("PacketPlayOutOpenBook").getDeclaredConstructor(NMSUtils.getClass("EnumHand")).newInstance(NMSUtils.enumValueOf(NMSUtils.getClass("EnumHand"), "MAIN_HAND"));
                PacketUtils.sendPacket(p, packet);
            }catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }, 5L);
    }
}
