package fr.devsylone.fkpi.rules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Rule<T>
{
	protected final String name;
	protected final T defaultValue;

	private static final Map<String, Rule<?>> RULES = new HashMap<>();

	Rule(String name, T defaultValue)
	{
		this.name = name;
		this.defaultValue = defaultValue;
		RULES.put(name, this);
	}

	// Caps
	public static final Rule<Integer> PVP_CAP = new Rule<>("PvpCap", 3);
	public static final Rule<Integer> TNT_CAP = new Rule<>("TntCap", 6);
	public static final Rule<Integer> NETHER_CAP = new Rule<>("NetherCap", 1);
	public static final Rule<Integer> END_CAP = new Rule<>("EndCap", 1);

	// Limites
	public static final Rule<Integer> DEATH_LIMIT = new Rule<>("DeathLimit", 0);
	public static final Rule<Integer> CHEST_LIMIT = new Rule<>("ChestLimit", 20);

	// Entiers
	public static final Rule<Integer> DAY_DURATION = new Rule<>("DayDuration", 24000);

	// Booléens
	public static final Rule<Boolean> FRIENDLY_FIRE = new Rule<>("FriendlyFire", true);
	public static final Rule<Boolean> ETERNAL_DAY = new Rule<>("EternalDay", false);
	public static final Rule<Boolean> DO_PAUSE_AFTER_DAY = new Rule<>("DoPauseAfterDay", false);
	public static final Rule<Boolean> DEEP_PAUSE = new Rule<>("DeepPause", true);
	public static final Rule<Boolean> TNT_JUMP = new Rule<>("TntJump", true);
	public static final Rule<Boolean> RESPAWN_AT_HOME = new Rule<>("RespawnAtHome", false);
	public static final Rule<Boolean> HEALTH_BELOW_NAME = new Rule<>("HealthBelowName", true);

	// Valeurs complexes
	public static final Rule<AllowedBlocks> ALLOWED_BLOCKS = new Rule<>("AllowedBlocks", new AllowedBlocks());
	public static final Rule<ChargedCreepers> CHARGED_CREEPERS = new Rule<>("ChargedCreepers", new ChargedCreepers());
	public static final Rule<DisabledPotions> DISABLED_POTIONS = new Rule<>("DisabledPotions", new DisabledPotions());
	public static final Rule<PlaceBlockInCave> PLACE_BLOCK_IN_CAVE = new Rule<>("PlaceBlockInCave", new PlaceBlockInCave());

	public String getName()
	{
		return name;
	}

	public T getDefaultValue()
	{
		return defaultValue;
	}

	@SuppressWarnings("unchecked")
	public static <T> Rule<T> getByName(String name)
	{
		return (Rule<T>) RULES.get(name);
	}

	public static Set<Rule<?>> values()
	{
		return new HashSet<>(RULES.values());
	}
}
