package fr.devsylone.fallenkingdom.scoreboard;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.PlaceHolderUtils;

public enum PlaceHolder
{

	DAY("getDays", Fk.getInstance().getGame(), "Jour", "DAY", "DAYS", "JOUR", "JOURS", "D", "J"),
	HOUR("getHour", Fk.getInstance().getGame(), "Heure", "HOUR", "HOURS", "HEURE", "HEURES", "H"),
	MINUTE("getMinute", Fk.getInstance().getGame(), "Minute", "MINUTE", "MINUTES", "M"),
	TEAM("getTeamOf", PlaceHolderUtils.class, new String[] {"player"}, "Équipe du joueur", "PLAYER_TEAM", "TEAM", "EQUIPE"),
	DEATHS("getDeaths", PlaceHolderUtils.class, new String[] {"player"}, "Nombre de morts", "PLAYER_DEATHS", "DEATHS", "MORTS"),
	KILLS("getKills", PlaceHolderUtils.class, new String[] {"player"}, "Nombre de kills", "PLAYER_KILLS", "KILLS"),

	BASE_DISTANCE("getBaseDistance", PlaceHolderUtils.class, new String[] {"player"}, "Distance de la base", "PLAYER_DISTANCE_TO_BASE", "BASE_DISTANCE", "DISTANCE", "DIST"),
	BASE_DIRECTION("getBaseDirection", PlaceHolderUtils.class, new String[] {"player", "arrows"}, "Direction de la base", "PLAYER_BASE_DIRECTION", "BASE_DIRECTION", "DIRECTION", "ARROW", "ARROWS"),

	PVPCAP("isPvpEnabled", Fk.getInstance().getGame(), "Pvp actifs ?", "PVP?", "PVP_ACTIVATED"),
	TNTCAP("isAssaultsEnabled", Fk.getInstance().getGame(), "Assauts actifs ?", "TNT?"),
	NETHERCAP("isNetherEnabled", Fk.getInstance().getGame(), "Nether ouvert ?", "NETHER?"),
	ENDCAP("isEndEnabled", Fk.getInstance().getGame(), "End ouvert ?", "END?");

	private Method method;
	private Object instance;
	private String description;
	private String[] rawKeys;
	private String[] keys;
	private String[] methodParametersName;

	private PlaceHolder(String stringMethod, Class<?> clazz, Object instance, String[] methodParametersName, String description, String... rawKeys)
	{
		try
		{
			for(Method m : clazz.getMethods())
				if(m.getName().equalsIgnoreCase(stringMethod))
				{
					method = m;
					break;
				}

			this.instance = instance;
			this.description = description;
			this.rawKeys = rawKeys;
			this.keys  = new String[rawKeys.length];
			for(int i = 0; i < rawKeys.length; i++)
				keys[i] = "(?i)" + Pattern.quote("{" + rawKeys[i] + "}");

			this.methodParametersName = methodParametersName;
		}catch(SecurityException e)
		{
			e.printStackTrace();
		}catch(IllegalArgumentException e)
		{
			e.printStackTrace();
		}
	}

	private PlaceHolder(String stringMethod, Class<?> clazz, String description, String... rawKeys)
	{
		this(stringMethod, clazz, null, null, description, rawKeys);
	}

	private PlaceHolder(String stringMethod, Object instance, String description, String... rawKeys)
	{
		this(stringMethod, instance.getClass(), instance, null, description, rawKeys);
	}

	private PlaceHolder(String stringMethod, Class<?> clazz, String[] methodParametersName, String description, String... rawKeys)
	{
		this(stringMethod, clazz, null, methodParametersName, description, rawKeys);
	}

	private PlaceHolder(String stringMethod, Object instance, String[] methodParametersName, String description, String... rawKeys)
	{
		this(stringMethod, instance.getClass(), instance, methodParametersName, description, rawKeys);
	}

	public String replace(String chainToProcess, Player p)
	{
		Object returned;
		try
		{
			Parameter[] parametersType = method.getParameters();
			Object[] parameters = new Object[parametersType.length];
			for(int i = 0; i < parametersType.length; i++)
			{
				if(parametersType[i].getType().equals(Player.class))
					parameters[i] = p;
				else if(parametersType[i].getType().equals(String.class) && methodParametersName[i].equalsIgnoreCase("player"))
					parameters[i] = p.getName();
				else
					for(String s : Fk.getInstance().getScoreboardManager().getCustomStrings().keySet())
						if(methodParametersName[i].equalsIgnoreCase(s))
						{
							parameters[i] = Fk.getInstance().getScoreboardManager().getCustomStrings().get(s);
							break;
						}

			}

			returned = method.invoke(instance, parameters);
			HashMap<String, String> defined = Fk.getInstance().getScoreboardManager().getCustomStrings();

			for(String key : keys)
			{
				if(chainToProcess.matches(".*" + key + ".*"))
				{

					String replacement = "";
					try
					{
						if(returned instanceof String)//SI c'est un String
						{
							replacement = (String) returned;
							for(String def : defined.keySet())
							{
								if(replacement.contains("{" + def + "}"))
								{
									if(!chainToProcess.contains(defined.get(def)))
										replacement = replacement.replace("{" + def + "}", defined.get(def));
									else
									{
										replacement = replacement.replace("{" + def + "}", "");
									}
								}
							}
						}
						else if(returned instanceof Integer)//SI c'est un Integer
							replacement = String.valueOf((Integer) returned);

						else if(returned instanceof Boolean && defined.containsKey("stringTrue") && defined.containsKey("stringFalse"))//SI c'est un Boolean
							replacement = String.valueOf((boolean) returned ? defined.get("stringTrue") : defined.get("stringFalse"));

						chainToProcess = chainToProcess.replaceAll(key, replacement);

					}catch(IllegalArgumentException e)
					{
						e.printStackTrace();
					}
				}
			}
		}catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e1)
		{
			e1.printStackTrace();
		}

		return chainToProcess;
	}

	public boolean isInLine(String str)
	{
		for(String s : keys)
		{
			if(str.matches(".*" + s + ".*"))
			{
				return true;
			}
		}
		return false;
	}

	public String getShortestKey()
	{
		String cur = rawKeys[0];
		for(String s : rawKeys)
			if(cur.length() > s.length())
				cur = s;

		return cur;
	}
	
	public String getDescription()
	{
		return description;
	}

}
