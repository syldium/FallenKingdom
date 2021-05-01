package fr.devsylone.fallenkingdom.utils;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;

public class NMSUtils
{
	private static final String version = getServerVersion();
	private static final Set<Class<?>> classes = new HashSet<>();

	private static String getServerVersion()
	{
		Class<?> server = Bukkit.getServer().getClass();
		if (!server.getSimpleName().equals("CraftServer")) {
			return "";
		}
		if (server.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
			return "";
		} else {
			String version = server.getName().substring("org.bukkit.craftbukkit.".length());
			return version.substring(0, version.length() - ".CraftServer".length());
		}
	}

	public static void register(String clazz) throws ClassNotFoundException
	{
		clazz=clazz.replaceAll("_version_", version);
		
		if(!clazz.contains("$"))
			classes.add(Class.forName(clazz));
		else {
			try {
				classes.add(Class.forName(clazz));
			}catch(ClassNotFoundException e) {
				String[] splitted=clazz.split("\\.");
				String packageS="";
				for(int i=0;i<splitted.length-1;i++) {
					packageS+="."+splitted[i];
				}
				if(!packageS.isEmpty())
					packageS = packageS.substring(1);
				String className=clazz.split("\\$")[1];
				classes.add(Class.forName(packageS+"."+className));
			}
		}
	}

	public static Class<?> getClass(String givenName) throws ClassNotFoundException
	{
		boolean composedName=givenName.contains("$");
		for(Class<?> c : classes) {
			String name=c.getName();
			String simpleName=c.getSimpleName();
			if(!composedName) {
				if(givenName.equalsIgnoreCase(simpleName)) {
					return c;
				}
			}else {
				if(name.endsWith("."+givenName))
					return c;
			}
		}

		throw new ClassNotFoundException("Cannot found this class : " + givenName + "(Is it registered ?)");
	}


	public static final String OBC_PACKAGE = "org.bukkit.craftbukkit";
	public static final String NMS_PACKAGE = "net.minecraft.server";
	public static String nmsClassName(String className) {
		return NMS_PACKAGE + '.' + version + '.' + className;
	}

	public static Class<?> nmsClass(String className) throws ClassNotFoundException {
		return Class.forName(nmsClassName(className));
	}

	public static Optional<Class<?>> nmsOptionalClass(String className) {
		return optionalClass(nmsClassName(className));
	}

	public static String obcClassName(String className) {
		return OBC_PACKAGE + '.' + version + '.' + className;
	}

	public static Class<?> obcClass(String className) throws ClassNotFoundException {
		return Class.forName(obcClassName(className));
	}

	public static Optional<Class<?>> obcOptionalClass(String className) {
		return optionalClass(obcClassName(className));
	}

	public static Optional<Class<?>> optionalClass(String className) {
		try {
			return Optional.of(Class.forName(className));
		} catch (NullPointerException|ClassNotFoundException e) {
			// Vous aimez les trucs bizarres ? Class.forName() déclenche des fois des lang.NullPointerException, alors que ce n'est pas associé à sa signature...
			return Optional.empty();
		}
	}

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> E enumValueOf(Class<?> enumClass, String enumName) {
		return Enum.valueOf((Class<E>) enumClass, enumName.toUpperCase(Locale.ROOT));
	}

	public static String getVersion()
	{
		return version;
	}
}
