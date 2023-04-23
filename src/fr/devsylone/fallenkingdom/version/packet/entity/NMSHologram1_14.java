package fr.devsylone.fallenkingdom.version.packet.entity;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

class NMSHologram1_14 extends NMSHologram1_13 {

    public NMSHologram1_14()
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
        Objects.requireNonNull(p, "Unable to create an entity for an offline player.");

        if(loc == null)
            loc = p.getLocation();

        int id = entityIdSupplier.getAsInt();
        try
        {
            Object spawn = NMSUtils.getClass("PacketPlayOutSpawnEntity").getDeclaredConstructor().newInstance();
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
        }catch(ReflectiveOperationException ex)
        {
            ex.printStackTrace();
        }
        return id;
    }
}
