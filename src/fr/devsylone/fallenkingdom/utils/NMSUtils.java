package fr.devsylone.fallenkingdom.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.Bukkit;

public class NMSUtils
{
	private static final String SERVER_VERSION = getServerVersion();
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

	@Deprecated
	public static void register(String clazz) throws ClassNotFoundException
	{
		clazz=clazz.replaceAll("_version_", SERVER_VERSION);
		
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

	@Deprecated
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


	public static final String OBC_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();
	public static final String NMS_PACKAGE = "net.minecraft.server";
	public static final String NM_PACKAGE = "net.minecraft";

	private static final boolean NMS_REPACKAGED = optionalClass(NM_PACKAGE + ".network.protocol.Packet").isPresent();

	public static boolean isRepackaged() {
		return NMS_REPACKAGED;
	}

	public static String nmsClassName(String className) {
		return NMS_PACKAGE + '.' + SERVER_VERSION + '.' + className;
	}

	public static String nmsClassName(String post1_17package, String className) {
		if (NMS_REPACKAGED) {
			String classPackage = post1_17package == null ? NM_PACKAGE : NM_PACKAGE + '.' + post1_17package;
			return classPackage + '.' + className;
		}
		return nmsClassName(className);
	}

	@Deprecated
	public static Class<?> nmsClass(String className) throws ClassNotFoundException {
		return Class.forName(nmsClassName(className));
	}

	public static Class<?> nmsClass(String post1_17package, String className) throws ClassNotFoundException {
		return Class.forName(nmsClassName(post1_17package, className));
	}

	public static Optional<Class<?>> nmsOptionalClass(String className) {
		return optionalClass(nmsClassName(className));
	}

	public static Optional<Class<?>> nmsOptionalClass(String post1_17package, String className) {
		return optionalClass(nmsClassName(post1_17package, className));
	}

	public static String obcClassName(String className) {
		return OBC_PACKAGE + '.' + className;
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
		} catch (NullPointerException | ClassNotFoundException e) {
			// Certains serveurs hybrides lèvent une NPE lors de la transformation du nom de la classe.
			return Optional.empty();
		}
	}

	public static Object enumValueOf(Class<?> enumClass, String enumName) {
		return Enum.valueOf(enumClass.asSubclass(Enum.class), enumName);
	}

	public static Object enumValueOf(Class<?> enumClass, String enumName, int enumFallbackOrdinal) {
		try {
			return enumValueOf(enumClass, enumName);
		} catch (IllegalArgumentException e) {
			Object[] constants = enumClass.getEnumConstants();
			if (constants.length > enumFallbackOrdinal) {
				return constants[enumFallbackOrdinal];
			}
			throw e;
		}
	}

	public static Field getField(Class<?> holder, Class<?> fieldType, Predicate<Field> fieldPredicate) throws NoSuchFieldException {
		for (Field field : holder.getDeclaredFields()) {
			if (field.getType().isAssignableFrom(fieldType) && fieldPredicate.test(field)) {
				field.setAccessible(true);
				return field;
			}
		}
		throw new NoSuchFieldException("On " + holder.getCanonicalName());
	}

	public static Method getMethod(Class<?> holder, Class<?> returnType, Class<?>... parametersTypes) throws NoSuchMethodException {
		for (Method method : holder.getDeclaredMethods()) {
			if (Arrays.equals(method.getParameterTypes(), parametersTypes) && method.getReturnType().isAssignableFrom(returnType)) {
				return method;
			}
		}
		throw new NoSuchMethodException("On " + holder.getCanonicalName());
	}
}
