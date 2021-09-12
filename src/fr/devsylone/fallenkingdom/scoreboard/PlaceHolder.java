package fr.devsylone.fallenkingdom.scoreboard;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.PlaceHolderUtils;

import static fr.devsylone.fallenkingdom.display.DisplayService.PLACEHOLDER_END;
import static fr.devsylone.fallenkingdom.display.DisplayService.PLACEHOLDER_START;

public enum PlaceHolder
{
    DAY(PlaceHolderUtils.GAME_SUPPLIER, Game::getDay, Messages.PLACE_HOLDER_DAY, "D", "DAY", "DAYS", "JOUR", "JOURS", "J"),
    HOUR(PlaceHolderUtils.GAME_SUPPLIER, Game::getHour, Messages.PLACE_HOLDER_HOUR, "H", "Heure", "HOUR", "HOURS", "HEURE", "HEURES"),
    MINUTE(PlaceHolderUtils.GAME_SUPPLIER, Game::getMinute, Messages.PLACE_HOLDER_MINUTES, "M", "Minute", "MINUTE", "MINUTES"),
    TEAM(PlaceHolderUtils::getTeamOf, Messages.PLACE_HOLDER_PLAYER_TEAM, "TEAM", "PLAYER_TEAM", "EQUIPE"),
    DEATHS(PlaceHolderUtils::getDeaths, Messages.PLACE_HOLDER_DEATHS_COUNT, "DEATHS", "PLAYER_DEATHS", "MORTS"),
    KILLS(PlaceHolderUtils::getKills, Messages.PLACE_HOLDER_KILLS_COUNT, "KILLS"),

    BASE_DISTANCE(PlaceHolderUtils::getBaseDistance, Messages.PLACE_HOLDER_BASE_DISTANCE, "DIST", "PLAYER_DISTANCE_TO_BASE", "BASE_DISTANCE", "DISTANCE"),
    BASE_DIRECTION(PlaceHolderUtils::getBaseDirection, Messages.PLACE_HOLDER_BASE_DIRECTION, "ARROW", "PLAYER_BASE_DIRECTION", "BASE_DIRECTION", "DIRECTION", "ARROWS"),
    BASE_OR_PORTAL(PlaceHolderUtils::getBaseOrPortal, Messages.PLACE_HOLDER_BASE_OR_PORTAL, "BASE_PORTAL", "BASE_OR_PORTAL"),
    NEAREST_TEAM_BASE(PlaceHolderUtils::getNearestTeamBase, Messages.PLACE_HOLDER_ENEMY_TEAM_BASE, "ENEMY_BASE", "NEAREST_TEAM_BASE", "ENEMY_TEAM_BASE"),
    NEAREST_BASE_DIRECTION(PlaceHolderUtils::getNearestBaseDirection, Messages.PLACE_HOLDER_ENEMY_TEAM_BASE_DIRECTION, "ENEMY_DIR", "NEAREST_BASE_DIRECTION", "ENEMY_BASE_DIRECTION", "ENEMY_BASE_DIR"),

    PVPCAP(PlaceHolderUtils.GAME_SUPPLIER, Game::isPvpEnabled, Messages.PLACE_HOLDER_ACTIVE_PVP, "PVP?"),
    TNTCAP(PlaceHolderUtils.GAME_SUPPLIER, Game::isAssaultsEnabled, Messages.PLACE_HOLDER_ACTIVE_ASSAULTS, "TNT?"),
    NETHERCAP(PlaceHolderUtils.GAME_SUPPLIER, Game::isNetherEnabled, Messages.PLACE_HOLDER_OPEN_NETHER, "NETHER?"),
    ENDCAP(PlaceHolderUtils.GAME_SUPPLIER, Game::isEndEnabled, Messages.PLACE_HOLDER_OPEN_END, "END?");

	private final BiFunction<Player, Integer, ?> callable;
	private final Messages description;
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
		this.key = PLACEHOLDER_START + key + PLACEHOLDER_END;
		this.legacyKeys = legacyKeys;
	}

	public String replace(String chainToProcess, Player player, int iteration)
	{
		Object replaceValue = callable.apply(player, iteration);
		if(replaceValue instanceof Boolean)
			replaceValue = Fk.getInstance().getDisplayService().text().format((boolean) replaceValue);
		return chainToProcess.replace(key, String.valueOf(replaceValue));
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

    public String getKey()
	{
    	return key;
    }

	public String[] getLegacyKeys()
	{
		return legacyKeys;
	}
}
