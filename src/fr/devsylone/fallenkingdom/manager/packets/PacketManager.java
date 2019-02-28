package fr.devsylone.fallenkingdom.manager.packets;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class PacketManager
{
	public static final int BIG_ITEM = 4; // Slot helmet
	public static final int SMALL_ITEM = 0; //Slot Hand

	protected HashMap<Integer, UUID> playerById;
	protected static int lastid = 100000;

	public PacketManager()
	{
		playerById = new HashMap<Integer, UUID>();
	}

	protected Player getPlayer(int entityId)
	{
		return Bukkit.getPlayer(playerById.get(entityId));
	}

	protected abstract int sendSpawn(Player p, Location loc);

	protected abstract void sendMetadata(int id, boolean visible, String customName);

	protected abstract void sendTeleport(int id, Location newLoc);

	protected abstract void sendDestroy(int id);

	protected abstract void sendEquipment(int id, int slot, String itemName);

	public abstract void sendBlockChange(Player p, Location loc, Material newBlock);

	public abstract void sendChunkReset(Player p, Chunk c);

	protected abstract void sendTitlePacket(Player p, TitleType type, String text, int fadeIn, int stay, int fadeOut);

	public abstract void openBook(final Player p, String nbtTags);

	public int createFloattingText(String text, Player p, Location loc)
	{
		int id = sendSpawn(p, loc);
		sendMetadata(id, false, text);
		return id;
	}

	public void updateFloattingText(int id, String newLine)
	{
		sendMetadata(id, false, newLine);
	}

	public void updateFloattingText(int id, Location loc)
	{
		sendTeleport(id, loc);
	}

	public void remove(int id)
	{
		sendDestroy(id);
	}

	public int displayItem(int size, Player p, Location loc, Material item)
	{
		int id = sendSpawn(p, loc.clone().add(0, -1, 0));
		sendMetadata(id, false, "");
		sendEquipment(id, size, "minecraft:" + item.name().toLowerCase());
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
}
