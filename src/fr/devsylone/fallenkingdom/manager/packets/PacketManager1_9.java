package fr.devsylone.fallenkingdom.manager.packets;

import java.util.Objects;
import java.util.UUID;

import fr.devsylone.fallenkingdom.version.tracker.DataTracker;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketManager1_9 extends PacketManager
{

	private static final Class<?> ITEM_SLOT;

	static {
		try {
			ITEM_SLOT = NMSUtils.nmsClass("world.entity", "EnumItemSlot");
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public PacketManager1_9()
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
			NMSUtils.register("net.minecraft.server._version_.Chunk");
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutMapChunk");
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

	protected int sendSpawn(Player p, Location loc)
	{
		Objects.requireNonNull(p, "Unable to create an entity for an offline player.");

		if(loc == null)
			loc = p.getLocation();

		int id = entityIdSupplier.get();
		playerById.put(id, p.getUniqueId());
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
	protected void sendMetadata(int id, boolean visible, String customName)
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
			PacketUtils.sendPacket(getPlayer(id), metadata);
		}catch(ReflectiveOperationException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	protected void sendTeleport(int id, Location newLoc)
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

			PacketUtils.sendPacket(getPlayer(id), tp);
		}catch(ReflectiveOperationException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	protected void sendDestroy(int id)
	{
		try
		{
			Object destroy = NMSUtils.getClass("PacketPlayOutEntityDestroy").getDeclaredConstructor().newInstance();
			PacketUtils.setField("a", new int[] {id}, destroy);

			PacketUtils.sendPacket(getPlayer(id), destroy);
		}catch(ReflectiveOperationException ex)
		{
			ex.printStackTrace();
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

			Object armors = NMSUtils.getClass("PacketPlayOutEntityEquipment").getConstructor(int.class, itemSlot.getClass(), NMSUtils.getClass("ItemStack")).newInstance(id, itemSlot, nmsItem);
			PacketUtils.sendPacket(getPlayer(id), armors);
		}catch(ReflectiveOperationException ex)
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

			Object change = NMSUtils.getClass("PacketPlayOutBlockChange").getConstructor(NMSUtils.getClass("World"), NMSUtils.getClass("BlockPosition")).newInstance(PacketUtils.getNMSWorld(p.getWorld()), blockPositionSet);
			PacketUtils.setField("block", NMSUtils.getClass("World").getDeclaredMethod("getType", NMSUtils.getClass("BlockPosition")).invoke(PacketUtils.getNMSWorld(p.getWorld()), blockPositionGet), change);

			PacketUtils.sendPacket(p, change);
		}catch(ReflectiveOperationException ex)
		{
			ex.printStackTrace();
		}
		loc.getWorld().getBlockAt(loc.getBlockX(), 0, loc.getBlockZ()).setType(oldMat);
	}

	@Override
	public void sendChunkReset(Player p, Chunk c)
	{
		try
		{
			Object chunkBulk = NMSUtils.getClass("PacketPlayOutMapChunk").getConstructor(NMSUtils.getClass("Chunk"), int.class).newInstance(NMSUtils.getClass("World").getDeclaredMethod("getChunkAt", int.class, int.class).invoke(PacketUtils.getNMSWorld(p.getWorld()), c.getX(), c.getZ()), 25);

			PacketUtils.sendPacket(p, chunkBulk);

			Material origin = p.getWorld().getBlockAt(c.getX() * 16, 250, c.getZ() * 16).getType();
			p.getWorld().getBlockAt(c.getX() * 16, 250, c.getZ() * 16).setType(Material.BEACON);
			p.getWorld().getBlockAt(c.getX() * 16, 250, c.getZ() * 16).setType(origin);
		}catch(ReflectiveOperationException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	protected void sendTitlePacket(Player p, TitleType type, String text, int fadeIn, int stay, int fadeOut)
	{
		try
		{
			Object title;
			if(type == TitleType.TIMES)
				title = NMSUtils.getClass("PacketPlayOutTitle").getConstructor(int.class, int.class, int.class).newInstance(fadeIn, stay, fadeOut);
			else
				title = NMSUtils.getClass("PacketPlayOutTitle").getConstructor(NMSUtils.getClass("EnumTitleAction"), NMSUtils.getClass("IChatBaseComponent")).newInstance(NMSUtils.getClass("EnumTitleAction").getDeclaredField(type.name()).get(null), NMSUtils.getClass("ChatSerializer").getDeclaredMethod("a", String.class).invoke(null, text));

			PacketUtils.sendPacket(p, title);
		}catch(ReflectiveOperationException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void openBook(final Player p, String nbtTags)
	{
		final int slot = p.getInventory().getHeldItemSlot();
		final ItemStack original = p.getInventory().getItem(slot);

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "replaceitem entity " + p.getName() + " slot.hotbar." + slot + " minecraft:written_book 1 0 " + nbtTags);

		Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> {
			try
			{
				if(original != null && original.getType() != Material.AIR)
					p.getWorld().dropItem(p.getLocation(), original).setPickupDelay(0);

				p.getInventory().setHeldItemSlot(slot);

				ByteBuf buf = Unpooled.buffer(256);
				buf.setByte(0, (byte) 0);
				buf.writerIndex(1);
				Object payload = NMSUtils.getClass("PacketPlayOutCustomPayload").getDeclaredConstructor(String.class, NMSUtils.getClass("PacketDataSerializer")).newInstance("MC|BOpen", NMSUtils.getClass("PacketDataSerializer").getDeclaredConstructor(ByteBuf.class).newInstance(buf));

				PacketUtils.sendPacket(p, payload);
			}catch(ReflectiveOperationException ex)
			{
				ex.printStackTrace();
			}
		}, 5L);
	}

	protected Object getEnumItemSlot(ItemSlot slot) {
		return NMSUtils.enumValueOf(ITEM_SLOT, slot.name());
	}
}
