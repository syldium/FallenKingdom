package fr.devsylone.fallenkingdom.version.packet.entity;

import java.util.Objects;
import java.util.UUID;

import fr.devsylone.fallenkingdom.version.tracker.DataTracker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;

class NMSHologram1_9 extends NMSHologram
{

	private static final Class<?> ITEM_SLOT;

	static {
		try {
			ITEM_SLOT = NMSUtils.nmsClass("world.entity", "EnumItemSlot");
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public NMSHologram1_9()
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
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutBlockChange");
			NMSUtils.register("net.minecraft.server._version_.World");
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutTitle");
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutTitle$EnumTitleAction");
			NMSUtils.register("net.minecraft.server._version_.IChatBaseComponent");
			NMSUtils.register("net.minecraft.server._version_.IChatBaseComponent$ChatSerializer");
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutCustomPayload");
			NMSUtils.register("net.minecraft.server._version_.PacketDataSerializer");
			NMSUtils.register("org.bukkit.craftbukkit._version_.inventory.CraftItemStack");

		}catch(Exception e)
		{
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
			PacketUtils.setField("k", 78, spawn);
			PacketUtils.setField("l", 0, spawn);

			PacketUtils.sendPacket(p, spawn);
		}catch(ReflectiveOperationException ex)
		{
			ex.printStackTrace();
		}
		return id;
	}

	@Override
	protected void sendMetadata(Player p, int id, boolean visible, String customName)
	{
		try
		{

			Object metadata = NMSUtils.getClass("PacketPlayOutEntityMetadata").getDeclaredConstructor().newInstance();

			PacketUtils.setField("a", id, metadata);
			PacketUtils.setField("b", new DataTracker()
							.invisible()
							.customName(customName)
							.customNameVisible(true)
							.trackedValues(),
					metadata);
			PacketUtils.sendPacket(p, metadata);
		}catch(ReflectiveOperationException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	protected void sendTeleport(Player p, int id, Location newLoc)
	{
		try
		{
			Object tp = NMSUtils.getClass("PacketPlayOutEntityTeleport").getDeclaredConstructor().newInstance();
			PacketUtils.setField("a", id, tp);
			PacketUtils.setField("b", newLoc.getX(), tp);
			PacketUtils.setField("c", newLoc.getY(), tp);
			PacketUtils.setField("d", newLoc.getZ(), tp);
			PacketUtils.setField("e", (byte) 0, tp);
			PacketUtils.setField("f", (byte) 0, tp);
			PacketUtils.setField("g", true, tp);

			PacketUtils.sendPacket(p, tp);
		}catch(ReflectiveOperationException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	protected void sendDestroy(Player p, int id)
	{
		try
		{
			Object destroy = NMSUtils.getClass("PacketPlayOutEntityDestroy").getDeclaredConstructor().newInstance();
			PacketUtils.setField("a", new int[] {id}, destroy);

			PacketUtils.sendPacket(p, destroy);
		}catch(ReflectiveOperationException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	protected void sendEquipment(Player p, int id, ItemSlot slot, Material material)
	{
		try
		{
			ItemStack bukkitItem = new ItemStack(material);
			Object nmsItem = NMSUtils.getClass("CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null, bukkitItem);
			Object itemSlot = getEnumItemSlot(slot);

			Object armors = NMSUtils.getClass("PacketPlayOutEntityEquipment").getConstructor(int.class, itemSlot.getClass(), NMSUtils.getClass("ItemStack")).newInstance(id, itemSlot, nmsItem);
			PacketUtils.sendPacket(p, armors);
		}catch(ReflectiveOperationException ex)
		{
			ex.printStackTrace();
		}
	}

	protected static Object getEnumItemSlot(ItemSlot slot) {
		return NMSUtils.enumValueOf(ITEM_SLOT, slot.name());
	}
}
