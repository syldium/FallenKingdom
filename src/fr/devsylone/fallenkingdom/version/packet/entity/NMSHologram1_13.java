package fr.devsylone.fallenkingdom.version.packet.entity;

import fr.devsylone.fallenkingdom.utils.NMSUtils;

class NMSHologram1_13 extends NMSHologram1_9 {

    public NMSHologram1_13()
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
}
