package fr.devsylone.fallenkingdom.scoreboard;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.PlaceHolderUtils;
import org.jetbrains.annotations.NotNull;

import static fr.devsylone.fallenkingdom.display.DisplayService.PLACEHOLDER_END;
import static fr.devsylone.fallenkingdom.display.DisplayService.PLACEHOLDER_START;

public enum PlaceHolder
{
    DAY(PlaceHolderUtils.GAME_SUPPLIER, Game::getDay, Messages.PLACE_HOLDER_DAY, "D", "DAY", "DAYS", "JOUR", "JOURS", "J"),
    HOUR(PlaceHolderUtils.GAME_SUPPLIER, Game::getHour, Messages.PLACE_HOLDER_HOUR, "H", "Heure", "HOUR", "HOURS", "HEURE", "HEURES"),
    MINUTE(PlaceHolderUtils.GAME_SUPPLIER, Game::getMinute, Messages.PLACE_HOLDER_MINUTES, "M", "Minute", "MINUTE", "MINUTES"),
    TEAM(PlaceHolderUtils::getTeamOf, Messages.PLACE_HOLDER_PLAYER_TEAM, "TEAM", "PLAYER_TEAM", "EQUIPE"),
    DEATHS(PlaceHolderUtils::getDeaths, Messages.PLACE_HOLDER_DEATHS_COUNT, "DEATHS", "PLAYER_DEATHS", "MORTS"),
	DEATHS_TEAM(PlaceHolderUtils::getTeamDeaths, Messages.PLACE_HOLDER_DEATHS_TEAM_COUNT, "DEATHS_TEAM"),
	KILLS(PlaceHolderUtils::getKills, Messages.PLACE_HOLDER_KILLS_COUNT, "KILLS"),
	KILLS_TEAM(PlaceHolderUtils::getTeamKills, Messages.PLACE_HOLDER_KILLS_TEAM_COUNT, "KILLS_TEAM"),

    BASE_DISTANCE(PlaceHolderUtils::getBaseDistance, Messages.PLACE_HOLDER_BASE_DISTANCE, "DIST", "PLAYER_DISTANCE_TO_BASE", "BASE_DISTANCE", "DISTANCE"),
    BASE_DIRECTION(PlaceHolderUtils::getBaseDirection, Messages.PLACE_HOLDER_BASE_DIRECTION, "ARROW", "PLAYER_BASE_DIRECTION", "BASE_DIRECTION", "DIRECTION", "ARROWS"),
    BASE_OR_PORTAL(PlaceHolderUtils::getBaseOrPortal, Messages.PLACE_HOLDER_BASE_OR_PORTAL, "BASE_PORTAL", "BASE_OR_PORTAL"),
	REGION(PlaceHolderUtils::getRegion, Messages.PLACE_HOLDER_REGION, "REGION"),
	REGION_CHANGE(PlaceHolderUtils::getRegionChange, Messages.PLACE_HOLDER_REGION_CHANGE, "REGION_CHANGE"),

	NEAREST_ALLY(PlaceHolderUtils::getNearestAllyName, Messages.PLACE_HOLDER_ALLY_NAME, "ALLY"),
	NEAREST_ALLY_DIR(PlaceHolderUtils::getNearestAllyDir, Messages.PLACE_HOLDER_ALLY_DIR, "ALLY_DIR"),
	NEAREST_ALLY_DIST(PlaceHolderUtils::getNearestAllyDist, Messages.PLACE_HOLDER_ALLY_DIST, "ALLY_DIST"),
	NEAREST_TEAM_BASE(PlaceHolderUtils::getNearestTeamBase, Messages.PLACE_HOLDER_ENEMY_TEAM_BASE, "ENEMY_BASE", "NEAREST_TEAM_BASE", "ENEMY_TEAM_BASE"),
    NEAREST_BASE_DIRECTION(PlaceHolderUtils::getNearestBaseDirection, Messages.PLACE_HOLDER_ENEMY_TEAM_BASE_DIRECTION, "ENEMY_DIR", "NEAREST_BASE_DIRECTION", "ENEMY_BASE_DIRECTION", "ENEMY_BASE_DIR"),

    PVPCAP(PlaceHolderUtils.GAME_SUPPLIER, Game::isPvpEnabled, Messages.PLACE_HOLDER_ACTIVE_PVP, "PVP?"),
    TNTCAP(PlaceHolderUtils.GAME_SUPPLIER, Game::isAssaultsEnabled, Messages.PLACE_HOLDER_ACTIVE_ASSAULTS, "TNT?"),
    NETHERCAP(PlaceHolderUtils.GAME_SUPPLIER, Game::isNetherEnabled, Messages.PLACE_HOLDER_OPEN_NETHER, "NETHER?"),
    ENDCAP(PlaceHolderUtils.GAME_SUPPLIER, Game::isEndEnabled, Messages.PLACE_HOLDER_OPEN_END, "END?");

	public static final PlaceHolder[] KILLS_RELATIVE = new PlaceHolder[]{DEATHS, DEATHS_TEAM, KILLS, KILLS_TEAM};
	public static final PlaceHolder[] LOCATION_RELATIVE = new PlaceHolder[]{BASE_DIRECTION, BASE_DISTANCE, NEAREST_TEAM_BASE, NEAREST_BASE_DIRECTION, NEAREST_ALLY,NEAREST_ALLY_DIR, NEAREST_ALLY_DIST};

	private final BiFunction<Player, Integer, ?> callable;
	private final Messages description;
	private final String rawKey;
	private final String key;
	private final String[] legacyKeys;

    <T> PlaceHolder(Supplier<T> supplier, Function<T, ?> callable, Messages description, String key, String... legacyKeys)
    {
        this((BiFunction<Player, Integer, ?>) (Player p, Integer i) -> callable.apply(supplier.get()), description, key, legacyKeys);
    }
    
    PlaceHolder(Function<Player, ?> callable, Messages description, String key,  String... legacyKeys)
    {
        this((BiFunction<Player, Integer, ?>) (Player p, Integer i) -> callable.apply(p), description, key, legacyKeys);
    }
    
	PlaceHolder(BiFunction<Player, Integer, ?> callable, Messages description, String key,  String... legacyKeys)
	{
		this.callable = callable;
		this.description = description;
		this.rawKey = key;
		this.key = PLACEHOLDER_START + key + PLACEHOLDER_END;
		this.legacyKeys = legacyKeys;
	}

	public String replace(String chainToProcess, Player player, int iteration)
	{
		return chainToProcess.replace(this.key, this.resolve(player, iteration));
	}

	public @NotNull String replaceMultiple(@NotNull String chainToProcess, @NotNull Player player)
	{
		final StringBuilder builder = new StringBuilder();
		int startIndex = 0;
		int count = 0;
		int keyIndex;
		while ((keyIndex = chainToProcess.indexOf(this.key, startIndex)) != -1) {
			if (startIndex != keyIndex) {
				builder.append(chainToProcess, startIndex, keyIndex);
			}
			startIndex = keyIndex + this.key.length();
			builder.append(this.resolve(player, count++));
		}
		if (startIndex != chainToProcess.length()) {
			builder.append(chainToProcess, startIndex, chainToProcess.length());
		}
		return builder.toString();
	}

	private @NotNull String resolve(@NotNull Player player, int iteration)
	{
		Object value = this.callable.apply(player, iteration);
		if (value instanceof Boolean)
			return Fk.getInstance().getDisplayService().text().format((boolean) value);
		return value.toString();
	}
	
	public String getDescription()
	{
		return description.getMessage();
	}

	public BiFunction<Player, Integer, ?> getFunction()
	{
		return callable;
	}

	public boolean isInLine(String s)
	{
		return s.contains(key);
	}

	public @NotNull String getKey()
	{
		return this.key;
	}

	public String getRawKey() {
		return this.rawKey;
	}

	public static @NotNull String removeLegacyKeys(@NotNull String message)
	{
		for (PlaceHolder placeHolder : PlaceHolder.values()) {
			for (String legacyKey : placeHolder.legacyKeys) {
				message = message.replace(PLACEHOLDER_START + legacyKey + PLACEHOLDER_END, placeHolder.getKey());
			}
		}
		return message;
	}
}
