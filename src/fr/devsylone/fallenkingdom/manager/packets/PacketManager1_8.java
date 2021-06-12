package fr.devsylone.fallenkingdom.manager.packets;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.devsylone.fallenkingdom.version.component.Components;
import fr.devsylone.fallenkingdom.version.component.FkComponent;
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

public class PacketManager1_8 extends PacketManager
{
	public PacketManager1_8()
	{
		try
		{
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutEntityMetadata");
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutSpawnEntity");
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutEntityDestroy");
			NMSUtils.register("net.minecraft.server._version_.DataWatcher$WatchableObject");
			NMSUtils.register("net.minecraft.server._version_.PlayerConnection");
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutEntityTeleport");
			NMSUtils.register("net.minecraft.server._version_.Packet");
			NMSUtils.register("net.minecraft.server._version_.ItemStack");
			NMSUtils.register("net.minecraft.server._version_.Block");
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutEntityEquipment");
			NMSUtils.register("net.minecraft.server._version_.BlockPosition");
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutBlockChange");
			NMSUtils.register("net.minecraft.server._version_.World");
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutMapChunkBulk");
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
		if(loc == null && p != null)
			loc = p.getLocation();

		int id = entityIdSupplier.get();
		playerById.put(id, p.getUniqueId());
		try
		{
			Object spawn = NMSUtils.getClass("PacketPlayOutSpawnEntity").getDeclaredConstructor().newInstance();
			PacketUtils.setField("a", id, spawn);
			PacketUtils.setField("b", (int) (loc.getX() * 32), spawn);
			PacketUtils.setField("c", (int) (loc.getY() * 32), spawn);
			PacketUtils.setField("d", (int) (loc.getZ() * 32), spawn);
			PacketUtils.setField("e", 0, spawn);
			PacketUtils.setField("f", 0, spawn);
			PacketUtils.setField("g", 0, spawn);
			PacketUtils.setField("h", 0, spawn);
			PacketUtils.setField("i", 0, spawn);
			PacketUtils.setField("j", 78, spawn);
			PacketUtils.setField("k", 0, spawn);

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
			Object metadata = NMSUtils.getClass("PacketPlayOutEntityMetadata").newInstance();

			PacketUtils.setField("a", id, metadata);
			List<Object> datas = new ArrayList<Object>();

			Constructor<?> woConstructor = NMSUtils.getClass("WatchableObject").getDeclaredConstructor(int.class, int.class, Object.class);

			if(!visible)
				datas.add(woConstructor.newInstance(0, 0, (byte) 32));
			if(customName != null && !customName.isEmpty())
			{
				datas.add(woConstructor.newInstance(4, 2, customName));
				datas.add(woConstructor.newInstance(0, 3, (byte) 1));
			}

			for(Object wo : datas)
				NMSUtils.getClass("WatchableObject").getMethod("a", boolean.class).invoke(wo, false);

			PacketUtils.setField("b", datas, metadata);

			PacketUtils.sendPacket(getPlayer(id), metadata);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	protected void sendTeleport(int id, Location newLoc)
	{
		try
		{
			Constructor<?> tpConstructor = NMSUtils.getClass("PacketPlayOutEntityTeleport").getDeclaredConstructor(int.class, int.class, int.class, int.class, byte.class, byte.class, boolean.class);
			int x = (int) (newLoc.getX() * (double) 32);
			int y = (int) ((newLoc.getY() - (double) 0.6) * (double) 32);
			int z = (int) (newLoc.getZ() * (double) 32);
			Object tp = tpConstructor.newInstance(id, x, y, z, (byte) 0, (byte) 0, true);

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
			Object destroy = NMSUtils.getClass("PacketPlayOutEntityDestroy").newInstance();
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
			Object armors = NMSUtils.getClass("PacketPlayOutEntityEquipment").getConstructor(int.class, int.class, NMSUtils.getClass("ItemStack")).newInstance(id, getItemSlot(slot), nmsItem);

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
			Object chunkBulk = NMSUtils.getClass("PacketPlayOutMapChunkBulk").getConstructor(List.class).newInstance(Arrays.asList(NMSUtils.getClass("World").getDeclaredMethod("getChunkAt", int.class, int.class).invoke(PacketUtils.getNMSWorld(p.getWorld()), c.getX(), c.getZ())));

			PacketUtils.sendPacket(p, chunkBulk);
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
			Object title = null;
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
	public void openBook(final Player p, ItemStack book, FkComponent title, FkComponent author, FkComponent... pages)
	{
		final int slot = p.getInventory().getHeldItemSlot();
		final ItemStack original = p.getInventory().getItem(slot);

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "replaceitem entity " + p.getName() + " slot.hotbar." + slot + " minecraft:written_book 1 0 " + Components.stringifyBook(title, author, pages));

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

	private int getItemSlot(ItemSlot slot) {
		return slot.ordinal() > 0 ? slot.ordinal() - 1 : 0;
	}
}
