package fr.devsylone.fallenkingdom.utils;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;

public class NMSUtils
{
	private static String version;
	private static Set<Class<?>> classes;;

	static
	{
		version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		classes = new HashSet<Class<?>>();
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

	public static String getVersion()
	{
		return version;
	}
}
