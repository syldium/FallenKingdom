package fr.devsylone.fallenkingdom.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.bukkit.Bukkit;
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
	static
	{
		try
		{
			NMSUtils.register("org.bukkit.craftbukkit._version_.entity.CraftPlayer");
			NMSUtils.register("net.minecraft.server._version_.PlayerConnection");
			NMSUtils.register("org.bukkit.craftbukkit._version_.CraftWorld");
			NMSUtils.register("net.minecraft.server._version_.World");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setField(String field, Object value, Object instance) throws ReflectiveOperationException
	{
		Class<?> c = instance.getClass();
		Field f = c.getDeclaredField(field);
		f.setAccessible(true);
		f.set(instance, value);
	}

	public static void printFields(Object o, String indent) throws IllegalAccessException
	{

		if(o == null)
		{
			print(indent + Messages.CONSOLE_NULL.getMessage());
			return;
		}
		Class<?> c = o.getClass();

		if(indent.length() > 4 * 2)
			return;

		if(o.getClass().isEnum())
		{
			print("§c" + Messages.CONSOLE_ENUM.getMessage() + indent + "...");
			return;
		}

		int i = 0;

		for(Field f : c.getDeclaredFields())
		{
			f.setAccessible(true);

			if(f.get(o) == null)
				print(indent + "§c" + Messages.CONSOLE_NULL.getMessage());
			else
			{
				Class<?> fType = f.get(o).getClass();

				if(fType.isArray())
				{
					if(Array.getLength(f.get(o)) > 1000)
						print("§b" + indent + f.getName() + "=" + f.get(o).getClass().getSimpleName() + ": " + f.get(o) + "§e>1000");

					else
						for(int k = 0; k < Array.getLength(f.get(o)); k++)
						{
							Object current = Array.get(f.get(o), k);
							if(current == null)
								print("§a" + indent + f.getName() + "[" + k + "]=§c" + Messages.CONSOLE_NULL.getMessage());

							else if(isPrimitive(current.getClass()))
								print("§b" + indent + f.getName() + "[" + k + "]=" + current);

							else
							{
								print("§a" + indent + f.getName() + "[" + k + "]=" + current.getClass().getSimpleName());
								printFields(current, indent + "    ");
							}
						}
				}

				else if(isPrimitive(fType))
					print("§b" + indent + f.getName() + "=" + f.get(o).getClass().getSimpleName() + ": " + f.get(o));

				else if(Iterable.class.isAssignableFrom(fType))
				{
					for(Object it : (Iterable<?>) f.get(o))
					{
						if(it == null)
							print("§a" + indent + f.getName() + "[" + i++ + "]=§c" + Messages.CONSOLE_NULL.getMessage());

						else if(isPrimitive(it.getClass()))
							print("§b" + indent + f.getName() + "[" + i++ + "]=" + it);

						else
						{
							print("§a" + indent + f.getName() + "[" + i++ + "]=" + it.getClass().getSimpleName());
							printFields(it, indent + "    ");
						}
					}
				}
				else
				{
					print("§a" + indent + f.getName() + "=" + f.get(o).getClass().getSimpleName());
					printFields(f.get(o), indent + "    ");
				}
			}
		}
	}

	public static void print(Object o)
	{
		Bukkit.broadcastMessage(o.toString());
	}

	public static boolean isPrimitive(Class<?> c)
	{
		return Arrays.asList(boolean.class, int.class, float.class, double.class, long.class, byte.class, short.class, char.class, String.class, Boolean.class, Integer.class, Float.class, Double.class, Long.class, Byte.class, Short.class, Character.class).contains(c);
	}

	public static Object getPlayerConnection(Player p) throws  ReflectiveOperationException
	{
		Object craftPlayer = NMSUtils.getClass("CraftPlayer").cast(p);
		Object entityPlayer = NMSUtils.getClass("CraftPlayer").getDeclaredMethod("getHandle").invoke(craftPlayer);
		Object playerConnection = NMSUtils.getClass("PlayerConnection").cast(entityPlayer.getClass().getField("playerConnection").get(entityPlayer));
		return playerConnection;
	}

	public static Object getNMSWorld(World w) throws ReflectiveOperationException
	{
		Object craftWorld = NMSUtils.getClass("CraftWorld").cast(w);
		Object nmsWorld = NMSUtils.getClass("CraftWorld").getDeclaredMethod("getHandle").invoke(craftWorld);
		return nmsWorld;
	}

	public static Object getNMSEntity(Entity e) throws ReflectiveOperationException
	{
		Object craftEntity = NMSUtils.obcClass("entity.CraftEntity").cast(e);
		Object nmsEntity = NMSUtils.obcClass("entity.CraftEntity").getDeclaredMethod("getHandle").invoke(craftEntity);
		return nmsEntity;
	}

	public static void sendPacket(Object playerConnection, Object packet) throws ReflectiveOperationException
	{
		NMSUtils.getClass("PlayerConnection").getDeclaredMethod("sendPacket", NMSUtils.getClass("Packet")).invoke(playerConnection, packet);
	}

	public static void sendPacket(Player p, Object packet) throws ReflectiveOperationException
	{
		sendPacket(getPlayerConnection(p), packet);
	}

	public static void sendJSON(Player p, String json)
	{
		try
		{
			Class<?> chatBaseComponent = NMSUtils.getClass("IChatBaseComponent");
			Class<?> packetPlayOutChat = NMSUtils.getClass("PacketPlayOutChat");
			Object toSend = NMSUtils.getClass("ChatSerializer").getMethod("a", String.class).invoke(null, json);
			sendPacket(p, packetPlayOutChat.getConstructor(chatBaseComponent).newInstance(toSend));
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
