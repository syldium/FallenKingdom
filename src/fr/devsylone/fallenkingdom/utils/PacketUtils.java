package fr.devsylone.fallenkingdom.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

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
	private static final Method GET_SERVER_HANDLE;
	public static final Class<?> MINECRAFT_WORLD;
	public static final Class<?> MINECRAFT_CHUNK;
	public static final Class<?> MINECRAFT_SERVER;
	public static final Class<?> MINECRAFT_BLOCK_POSITION;
	private static final MethodHandle NEW_BLOCK_POSITION;

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
					.findFirst().orElseThrow(() -> new NoSuchFieldException("Cannot find ServerPlayer#connection"));
			GET_PLAYER_CONNECTION_CRAFT_PLAYER = lookup.unreflectGetter(playerConnectionField);

			final Class<?> packet = NMSUtils.nmsClass("network.protocol", "Packet");
			Method sendPacket;
			try {
				sendPacket = NMSUtils.getMethod(playerConnection, void.class, packet);
			} catch (NoSuchMethodException v1_20_2) {
				sendPacket = NMSUtils.getMethod(playerConnection.getSuperclass(), void.class, packet);
			}
			SEND_PACKET = lookup.unreflect(sendPacket);

			CRAFT_WORLD = NMSUtils.obcClass("CraftWorld");
			final Method getWorldHandle = CRAFT_WORLD.getMethod("getHandle");
			GET_WORLD_HANDLE = lookup.unreflect(getWorldHandle);
			MINECRAFT_WORLD = getWorldHandle.getReturnType();
			MINECRAFT_CHUNK = NMSUtils.nmsClass("world.level.chunk", "Chunk");
			Method getChunkAt;
			try {
				getChunkAt = MINECRAFT_WORLD.getMethod("getChunkAt", int.class, int.class);
			} catch (NoSuchMethodException v1_18) {
				getChunkAt = NMSUtils.getMethod(MINECRAFT_WORLD, MINECRAFT_CHUNK, int.class, int.class);
			}
			GET_CHUNK_HANDLE_AT = lookup.unreflect(getChunkAt);
			MINECRAFT_SERVER = NMSUtils.nmsClass("server", "MinecraftServer");
			GET_SERVER_HANDLE = MINECRAFT_SERVER.getDeclaredMethod("getServer");
			MINECRAFT_BLOCK_POSITION = NMSUtils.nmsClass("core", "BlockPosition");
			NEW_BLOCK_POSITION = lookup.unreflectConstructor(MINECRAFT_BLOCK_POSITION.getConstructor(int.class, int.class, int.class));
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

	public static Object getPlayerConnection(Player player) throws ReflectiveOperationException
	{
		try {
			return GET_PLAYER_CONNECTION_CRAFT_PLAYER.invoke(GET_PLAYER_HANDLE.invoke(player));
		} catch (Throwable throwable) {
			throw new ReflectiveOperationException(throwable);
		}
	}

	public static Object getNMSWorld(World world) throws ReflectiveOperationException
	{
		try {
			return GET_WORLD_HANDLE.invoke(world);
		} catch (Throwable throwable) {
			throw new ReflectiveOperationException(throwable);
		}
	}

	public static Object getNMSBlockPos(int x, int y, int z) throws ReflectiveOperationException
	{
		try {
			return NEW_BLOCK_POSITION.invoke(x, y, z);
		} catch (Throwable throwable) {
			throw new ReflectiveOperationException(throwable);
		}
	}

	public static Object getNMSServer() throws ReflectiveOperationException
	{
		try {
			return GET_SERVER_HANDLE.invoke(null);
		} catch (Throwable throwable) {
			throw new ReflectiveOperationException(throwable);
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
