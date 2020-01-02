package fr.devsylone.fallenkingdom.scoreboard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.manager.SaveablesManager.State;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;

public class ScoreboardSign
{
	public static final int LINE_NUMBER = 15;
	private static final String fieldHOrI = Bukkit.getBukkitVersion().contains("1.8") ? "h" : "i";
	private static final String fieldGOrH = Bukkit.getBukkitVersion().contains("1.8") ? "g" : "h";
	private boolean created = false;
	private final VirtualTeam[] lines = new VirtualTeam[LINE_NUMBER];
	private final Player player;
	private String objectiveName;

	private static boolean VERSION1_13 = false;

	// Chat
	private static final Method CHAT_SERIALIZER;

	// Scoreboard packets
	private static final Constructor<?> PACKET_SB_OBJ;
	private static final Constructor<?> PACKET_SB_DISPLAY_OBJ;
	private static final Constructor<?> PACKET_SB_SCORE;
	private static final Constructor<?> PACKET_SB_TEAM;

	// Scoreboard enums
	private static final Class<?> ENUM_SB_HEALTH_DISPLAY;
	private static final Class<?> ENUM_SB_ACTION;
	private static final Class<?> ENUM_CHAT_FORMAT;
	private static final Object ENUM_CHAT_FORMAT_RESET;
	private static final Object ENUM_SB_HEALTH_DISPLAY_INTEGER;
	private static final Object ENUM_SB_ACTION_CHANGE;
	private static final Object ENUM_SB_ACTION_REMOVE;


	static {
		try {
			if (NMSUtils.nmsOptionalClass("ScoreboardServer$Action").isPresent()) {
				VERSION1_13 = true;
				CHAT_SERIALIZER = NMSUtils.nmsClass("IChatBaseComponent$ChatSerializer").getDeclaredMethod("a", String.class);
			} else {
				CHAT_SERIALIZER = null;
			}

			PACKET_SB_OBJ = NMSUtils.nmsClass("PacketPlayOutScoreboardObjective").getConstructor();
			PACKET_SB_DISPLAY_OBJ = NMSUtils.nmsClass("PacketPlayOutScoreboardDisplayObjective").getConstructor();
			PACKET_SB_SCORE = NMSUtils.nmsClass("PacketPlayOutScoreboardScore").getConstructor();
			PACKET_SB_TEAM = NMSUtils.nmsClass("PacketPlayOutScoreboardTeam").getConstructor();

			ENUM_SB_HEALTH_DISPLAY = NMSUtils.nmsClass("IScoreboardCriteria$EnumScoreboardHealthDisplay");
			if (VERSION1_13) {
				ENUM_SB_ACTION = NMSUtils.nmsClass("ScoreboardServer$Action");
				ENUM_CHAT_FORMAT = NMSUtils.nmsClass("EnumChatFormat");
				ENUM_CHAT_FORMAT_RESET = NMSUtils.enumValueOf(ENUM_CHAT_FORMAT, "RESET");
			} else {
				ENUM_SB_ACTION = NMSUtils.nmsClass("PacketPlayOutScoreboardScore$EnumScoreboardAction");
				ENUM_CHAT_FORMAT = null;
				ENUM_CHAT_FORMAT_RESET = null;
			}
			ENUM_SB_HEALTH_DISPLAY_INTEGER = NMSUtils.enumValueOf(ENUM_SB_HEALTH_DISPLAY, "INTEGER");
			ENUM_SB_ACTION_CHANGE = NMSUtils.enumValueOf(ENUM_SB_ACTION, "CHANGE");
			ENUM_SB_ACTION_REMOVE = NMSUtils.enumValueOf(ENUM_SB_ACTION, "REMOVE");
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/**
	 * Create a scoreboard sign for a given player and using a specifig objective name
	 * @param player the player viewing the scoreboard sign
	 * @param objectiveName the name of the scoreboard sign (displayed at the top of the scoreboard)
	 */
	public ScoreboardSign(Player player, String objectiveName)
	{
		this.player = player;
		this.objectiveName = objectiveName;
	}

	/**
	 * Send the initial creation packets for this scoreboard sign. Must be called at least once.
	 */
	public void create()
	{
		if(created)
			return;
		try
		{
			Object player = getPlayer();
			PacketUtils.sendPacket(player, createObjectivePacket(0, objectiveName));
			PacketUtils.sendPacket(player, setObjectiveSlot());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		int i = 0;
		while(i < lines.length)
			sendLine(i++);

		created = true;
	}

	/**
	 * Send the packets to remove this scoreboard sign. A destroyed scoreboard sign must be recreated using {@link ScoreboardSign#create()} in order
	 * to be used again
	 */
	public void destroy()
	{
		if(!created)
			return;

		try
		{
			PacketUtils.sendPacket(getPlayer(), createObjectivePacket(1, null));
			for(VirtualTeam team : lines)
				if(team != null)
					PacketUtils.sendPacket(getPlayer(), team.removeTeam());

		}catch(Exception e)
		{
			e.printStackTrace();
		}

		created = false;
	}

	/**
	 * Change the name of the objective. The name is displayed at the top of the scoreboard.
	 * @param name the name of the objective, max 32 char
	 */
	public void setObjectiveName(String name)
	{
		try
		{
			this.objectiveName = name;
			if(created)
				PacketUtils.sendPacket(getPlayer(), createObjectivePacket(2, name));

		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Change a scoreboard line and send the packets to the player. Can be called async.
	 * @param line the number of the line (0 <= line < 15)
	 * @param value the new value for the scoreboard line
	 */
	public void setLine(String value, int line)
	{
		if(!Fk.getInstance().getSaveableManager().getState().equals(State.SLEEP))
			return;
		VirtualTeam team = getOrCreateTeam(line);
		String old = team.getCurrentPlayer();

		if(old != null && created)
			try
			{
				PacketUtils.sendPacket(getPlayer(), removeLine(old));
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		team.setValue(value);
		sendLine(line);
	}

	/**
	 * Remove a given scoreboard line
	 * @param line the line to remove
	 */
	public void removeLine(int line)
	{
		VirtualTeam team = getOrCreateTeam(line);
		String old = team.getCurrentPlayer();

		if(old != null && created)
			try
			{
				PacketUtils.sendPacket(getPlayer(), removeLine(old));
				PacketUtils.sendPacket(getPlayer(), team.removeTeam());
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		lines[line] = null;
	}

	/**
	 * Get the current value for a line
	 * @param line the line
	 * @return the content of the line
	 */
	public String getLine(int line)
	{
		if(line > 14)
			return null;
		if(line < 0)
			return null;
		return getOrCreateTeam(line).getValue();
	}

	/**
	 * Get the team assigned to a line
	 * @return the {@link VirtualTeam} used to display this line
	 */
	public VirtualTeam getTeam(int line)
	{
		if(line > 14)
			return null;
		if(line < 0)
			return null;
		return getOrCreateTeam(line);
	}

	private Object getPlayer() throws ReflectiveOperationException
	{
		return PacketUtils.getPlayerConnection(player);
	}

	private void sendLine(int line)
	{
		if(line > 14)
			return;
		if(line < 0)
			return;
		if(!created)
			return;

		int score = (15 - line);
		VirtualTeam val = getOrCreateTeam(line);

		try
		{

			for(Object packet : val.sendLine())
				PacketUtils.sendPacket(getPlayer(), packet);
			PacketUtils.sendPacket(getPlayer(), sendScore(val.getCurrentPlayer(), score));
		}catch(Exception e)
		{
			e.printStackTrace();
		}

		val.reset();
	}

	private VirtualTeam getOrCreateTeam(int line)
	{
		if(lines[line] == null)
			lines[line] = new VirtualTeam("__fakeScore" + line);

		return lines[line];
	}

	/*
	 * Factories
	 */
	private Object createObjectivePacket(int mode, String displayName)
	{
		try
		{
			Object packet = PACKET_SB_OBJ.newInstance();

			// Nom de l'objectif
			setField(packet, "a", player.getName());

			// Mode
			// 0 : créer
			// 1 : Supprimer
			// 2 : Mettre à jour
			setField(packet, "d", mode);

			if(mode == 0 || mode == 2)
			{
				setComponentField(packet, "b", displayName);
				setField(packet, "c", ENUM_SB_HEALTH_DISPLAY_INTEGER);
			}

			return packet;
		}catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	private Object setObjectiveSlot()
	{
		Object packet = null;

		try
		{
			packet = PACKET_SB_DISPLAY_OBJ.newInstance();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		// Slot
		setField(packet, "a", 1);
		setField(packet, "b", player.getName());

		return packet;
	}

	private Object sendScore(String line, int score)
	{
		Object packet = null;
		try {
			packet = PACKET_SB_SCORE.newInstance();
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		setField(packet, "d", ENUM_SB_ACTION_CHANGE);
		setField(packet, "b", player.getName());
		setField(packet, "c", score);
		setField(packet, "a", line);
		return packet;
	}

	private Object removeLine(String line)
	{
		Object packet = null;
		try {
			packet = PACKET_SB_SCORE.newInstance();
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		setField(packet, "d", ENUM_SB_ACTION_REMOVE);
		setField(packet, "b", player.getName());
		setField(packet, "a", line);
		setField(packet, "c", 0);
		return packet;
	}

	/**
	 * This class is used to manage the content of a line. Advanced users can use it as they want, but they are encouraged to read and understand the
	 * code before doing so. Use these methods at your own risk.
	 */
	public class VirtualTeam
	{
		private final String name;
		private String prefix;
		private String suffix;
		private String currentPlayer;
		private String oldPlayer;

		private boolean prefixChanged, suffixChanged, playerChanged = false;
		private boolean first = true;

		private VirtualTeam(String name, String prefix, String suffix)
		{
			this.name = name;
			this.prefix = prefix;
			this.suffix = suffix;
		}

		private VirtualTeam(String name)
		{
			this(name, "", "");
		}

		public String getName()
		{
			return name;
		}

		public String getPrefix()
		{
			return prefix;
		}

		public void setPrefix(String prefix)
		{
			if(this.prefix == null || !this.prefix.equals(prefix))
				this.prefixChanged = true;
			this.prefix = prefix;
		}

		public String getSuffix()
		{
			return suffix;
		}

		public void setSuffix(String suffix)
		{
			if(this.suffix == null || !this.suffix.equals(prefix))
				this.suffixChanged = true;
			this.suffix = suffix;
		}

		private Object createPacket(int mode)
		{
			Object packet = null;

			try
			{
				packet = PACKET_SB_TEAM.newInstance();

				setField(packet, "a", name);
				setComponentField(packet, "b", "");
				setComponentField(packet, "c", prefix);
				setComponentField(packet, "d", suffix);
				setField(packet, "e", "always");
				//setField(packet, "f", 0);
				if (VERSION1_13)
					setField(packet, "g", ENUM_CHAT_FORMAT_RESET);
				setField(packet, fieldHOrI, mode);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return packet;
		}

		public Object createTeam()
		{
			return createPacket(0);
		}

		public Object updateTeam()
		{
			return createPacket(2);
		}

		public Object removeTeam()
		{
			Object packet = null;

			try
			{
				packet = PACKET_SB_TEAM.newInstance();
			}catch(Exception e)
			{
				e.printStackTrace();
			}

			setField(packet, "a", name);
			setField(packet, fieldHOrI, 1);
			first = true;
			return packet;
		}

		public void setPlayer(String name)
		{
			if(this.currentPlayer == null || !this.currentPlayer.equals(name))
				this.playerChanged = true;
			this.oldPlayer = this.currentPlayer;
			this.currentPlayer = name;
		}

		public Iterable<Object> sendLine()
		{
			List<Object> packets = new ArrayList<Object>();

			if(first)
			{
				packets.add(createTeam());
			}
			else if(prefixChanged || suffixChanged)
			{
				packets.add(updateTeam());
			}

			if(first || playerChanged)
			{
				if(oldPlayer != null) // remove these two lines ?
					packets.add(addOrRemovePlayer(4, oldPlayer)); //
				packets.add(changePlayer());
			}

			if(first)
				first = false;

			return packets;
		}

		public void reset()
		{
			prefixChanged = false;
			suffixChanged = false;
			playerChanged = false;
			oldPlayer = null;
		}

		public Object changePlayer()
		{
			return addOrRemovePlayer(3, currentPlayer);
		}

		@SuppressWarnings("unchecked")
		public Object addOrRemovePlayer(int mode, String playerName)
		{
			Object packet = null;

			try
			{
				packet = PACKET_SB_TEAM.newInstance();
			}catch(Exception e)
			{
				e.printStackTrace();
			}

			setField(packet, "a", name);
			setField(packet, fieldHOrI, mode);

			try
			{
				Field f = packet.getClass().getDeclaredField(fieldGOrH);
				f.setAccessible(true);
				((List<String>) f.get(packet)).add(playerName);
			}catch(NoSuchFieldException | IllegalAccessException e)
			{
				e.printStackTrace();
			}

			return packet;
		}

		public String getCurrentPlayer()
		{
			return currentPlayer;
		}

		public String getValue()
		{
			return getPrefix() + getCurrentPlayer() + getSuffix();
		}

		public void setValue(String value)
		{
			if(value.length() <= 16)
			{
				setPrefix("");
				setSuffix("");
				setPlayer(value);
			}
			else if(value.length() <= 32)
			{
				setPrefix(value.substring(0, 16));
				setPlayer(value.substring(16));
				setSuffix("");
			}
			else if(value.length() <= 48)
			{
				setPrefix(value.substring(0, 16));
				setPlayer(value.substring(16, 32));
				setSuffix(value.substring(32));
			}
			else
			{
				throw new FkLightException("Too long value ! Max 48 characters, value was " + value.length() + " (" + value + ") !");
			}

			for(VirtualTeam team : lines)
				if(team != null && getCurrentPlayer() != null && team.getCurrentPlayer() != null && !team.equals(this) && !getCurrentPlayer().isEmpty() && !team.getCurrentPlayer().isEmpty())
					while(team.getCurrentPlayer().equalsIgnoreCase(getCurrentPlayer()))
					{
						if(getCurrentPlayer().length() > 1)
						{
							setSuffix(getCurrentPlayer().substring(getCurrentPlayer().length() - 1) + getSuffix());
							setPlayer(getCurrentPlayer().substring(0, getCurrentPlayer().length() - 1));

						}

						else
						{
							setPlayer(getPrefix().substring(getPrefix().length() - 1) + getCurrentPlayer());
							setPrefix(getPrefix().substring(0, getPrefix().length() - 1));
						}
					}
		}
	}

	private static void setField(Object edit, String fieldName, Object value)
	{
		try
		{
			Field field = edit.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(edit, value);
		}catch(NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	private void setComponentField(Object object, String fieldName, String value) throws ReflectiveOperationException
	{
		if (VERSION1_13) {
			setField(object, fieldName, CHAT_SERIALIZER.invoke(null, "{\"text\":\"" + value + "\"}"));
		} else {
			setField(object, fieldName, value);
		}
	}
}
