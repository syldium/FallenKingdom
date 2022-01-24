package fr.devsylone.fallenkingdom.manager.packets;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;

import fr.devsylone.fallenkingdom.version.component.FkBook;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class PacketManager
{
	protected final Map<Integer, UUID> playerById = new HashMap<>();
	protected final IntSupplier entityIdSupplier;

	@SuppressWarnings("deprecation")
	public PacketManager() {
		IntSupplier supplier;
		try {
			Bukkit.getUnsafe().getClass().getDeclaredMethod("nextEntityId");
			supplier = Bukkit.getUnsafe()::nextEntityId;
		} catch (NoSuchMethodException e) {
			supplier = new AtomicInteger(100000)::getAndIncrement;
		}
		entityIdSupplier = supplier;
	}

	protected Player getPlayer(int entityId)
	{
		return Bukkit.getPlayer(playerById.get(entityId));
	}

	protected abstract int sendSpawn(Player p, Location loc);

	protected abstract void sendMetadata(int id, boolean visible, String customName);

	protected abstract void sendTeleport(int id, Location newLoc);

	protected abstract void sendDestroy(int id);

	protected abstract void sendEquipment(int id, ItemSlot slot, Material material);

	public abstract void sendBlockChange(Player p, Location loc, Material newBlock);

	protected abstract void sendTitlePacket(Player p, TitleType type, String text, int fadeIn, int stay, int fadeOut);

	public abstract void openBook(final Player p, FkBook book);

	public int createFloatingText(String text, Player p, Location loc)
	{
		int id = sendSpawn(p, loc);
		sendMetadata(id, false, text);
		return id;
	}

	public void updateFloatingText(int id, String newLine)
	{
		sendMetadata(id, false, newLine);
	}

	public void updateFloatingText(int id, Location loc)
	{
		sendTeleport(id, loc);
	}

	public void remove(int id)
	{
		sendDestroy(id);
	}

	public int displayItem(ItemSlot slot, Player p, Location loc, Material item)
	{
		int id = sendSpawn(p, loc.clone().add(0, -1, 0));
		sendMetadata(id, false, "");
		sendEquipment(id, slot, item);
		return id;
	}

	public void sendTitle(Player p, String title, String subtitle)
	{
		sendTitlePacket(p, TitleType.TIMES, null, 20, 20, 20);
		sendTitlePacket(p, TitleType.SUBTITLE, "{\"text\":\"" + subtitle + "\"}", 0, 0, 0);
		sendTitlePacket(p, TitleType.TITLE, "{\"text\":\"" + title + "\"}", 0, 0, 0);
	}

	public void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut)
	{
		sendTitlePacket(p, TitleType.TIMES, null, fadeIn, stay, fadeOut);
		sendTitlePacket(p, TitleType.SUBTITLE, "{\"text\":\"" + subtitle + "\"}", 0, 0, 0);
		sendTitlePacket(p, TitleType.TITLE, "{\"text\":\"" + title + "\"}", 0, 0, 0);
	}

	public enum ItemSlot {
		MAINHAND,
		OFFHAND,
		FEET,
		LEGS,
		CHEST,
		HEAD
	}
}
