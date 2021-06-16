package fr.devsylone.fallenkingdom.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * 
 * @author Etrenak
 *
 */
public class PacketUtils
{
	private static final Class<?> CRAFT_PLAYER;
	private static final MethodHandle GET_PLAYER_HANDLE;
	private static final MethodHandle GET_PLAYER_CONNECTION_CRAFT_PLAYER;

	private static final MethodHandle SEND_PACKET;

	private static final Class<?> CRAFT_WORLD;
	private static final MethodHandle GET_WORLD_HANDLE;
	private static final MethodHandle GET_CHUNK_HANDLE_AT;
	public static final Class<?> MINECRAFT_WORLD;
	public static final Class<?> MINECRAFT_CHUNK;

	static
	{
		try
		{
			final MethodHandles.Lookup lookup = MethodHandles.lookup();
			CRAFT_PLAYER = NMSUtils.obcClass("entity.CraftPlayer");
			final Method getPlayerHandle = CRAFT_PLAYER.getMethod("getHandle");
			GET_PLAYER_HANDLE = lookup.unreflect(getPlayerHandle);
			final Class<?> playerConnection = NMSUtils.nmsClass("server.network", "PlayerConnection");
			final Field playerConnectionField = Arrays.stream(getPlayerHandle.getReturnType().getFields())
					.filter(field -> field.getType().isAssignableFrom(playerConnection))
					.findFirst().orElseThrow(RuntimeException::new);
			GET_PLAYER_CONNECTION_CRAFT_PLAYER = lookup.unreflectGetter(playerConnectionField);

			final Class<?> packet = NMSUtils.nmsClass("network.protocol", "Packet");
			SEND_PACKET = lookup.unreflect(playerConnection.getMethod("sendPacket", packet));

			CRAFT_WORLD = NMSUtils.obcClass("CraftWorld");
			final Method getWorldHandle = CRAFT_WORLD.getMethod("getHandle");
			GET_WORLD_HANDLE = lookup.unreflect(getWorldHandle);
			MINECRAFT_WORLD = getWorldHandle.getReturnType();
			MINECRAFT_CHUNK = NMSUtils.nmsClass("world.level.chunk", "Chunk");
			Method getChunkAt;
			try {
				getChunkAt = MINECRAFT_WORLD.getMethod("getChunkAt", int.class, int.class);
				if (!getChunkAt.getReturnType().isAssignableFrom(MINECRAFT_CHUNK)) {
					throw new NoSuchMethodException("Wrong getChunkAt method.");
				}
			} catch (NoSuchMethodException remapException) {
				getChunkAt = MINECRAFT_WORLD.getMethod("getChunk", int.class, int.class);
			}
			GET_CHUNK_HANDLE_AT = lookup.unreflect(getChunkAt);
		}catch(ReflectiveOperationException e)
		{
			throw new ExceptionInInitializerError(e);
		}
	}

	public static void setField(String field, Object value, Object instance) throws ReflectiveOperationException
	{
		Class<?> c = instance.getClass();
		Field f = c.getDeclaredField(field);
		f.setAccessible(true);
		f.set(instance, value);
	}

	public static Object getNMSPlayer(Player player)
	{
		try {
			return GET_PLAYER_HANDLE.invoke(player);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			return null;
		}
	}

	public static Object getPlayerConnection(Player player)
	{
		try {
			return GET_PLAYER_CONNECTION_CRAFT_PLAYER.invoke(GET_PLAYER_HANDLE.invoke(player));
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			return null;
		}
	}

	public static Object getNMSWorld(World world) throws ReflectiveOperationException
	{
		try {
			return GET_WORLD_HANDLE.invoke(world);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			return null;
		}
	}

	public static Object getNMSChunk(Chunk chunk)
	{
		try {
			return GET_CHUNK_HANDLE_AT.invoke(GET_WORLD_HANDLE.invoke(chunk.getWorld()), chunk.getX(), chunk.getZ());
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			return null;
		}
	}

	public static Object getNMSEntity(Entity e) throws ReflectiveOperationException
	{
		Object craftEntity = NMSUtils.obcClass("entity.CraftEntity").cast(e);
		return NMSUtils.obcClass("entity.CraftEntity").getDeclaredMethod("getHandle").invoke(craftEntity);
	}

	public static void sendPacket(Object playerConnection, Object packet)
	{
		try {
			SEND_PACKET.invoke(playerConnection, packet);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	public static void sendPacket(Player p, Object packet) throws ReflectiveOperationException
	{
		sendPacket(getPlayerConnection(p), packet);
	}
}
