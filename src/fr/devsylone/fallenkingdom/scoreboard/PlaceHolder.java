package fr.devsylone.fallenkingdom.scoreboard;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.PlaceHolderUtils;

public enum PlaceHolder
{
    DAY(PlaceHolderUtils.GAME_SUPPLIER, Game::getDay, Messages.PLACE_HOLDER_DAY.getMessage(), "DAY", "DAYS", "JOUR", "JOURS", "D", "J"),
    HOUR(PlaceHolderUtils.GAME_SUPPLIER, Game::getHour, Messages.PLACE_HOLDER_HOUR.getMessage(), "Heure", "HOUR", "HOURS", "HEURE", "HEURES", "H"),
    MINUTE(PlaceHolderUtils.GAME_SUPPLIER, Game::getMinute, Messages.PLACE_HOLDER_MINUTES.getMessage(), "Minute", "MINUTE", "MINUTES", "M"),
    TEAM(PlaceHolderUtils::getTeamOf, Messages.PLACE_HOLDER_PLAYER_TEAM.getMessage(), "PLAYER_TEAM", "TEAM", "EQUIPE"),
    DEATHS(PlaceHolderUtils::getDeaths, Messages.PLACE_HOLDER_DEATHS_COUNT.getMessage(), "PLAYER_DEATHS", "DEATHS", "MORTS"),
    KILLS(PlaceHolderUtils::getKills, Messages.PLACE_HOLDER_KILLS_COUNT.getMessage(), "KILLS"),

    BASE_DISTANCE(PlaceHolderUtils::getBaseDistance, Messages.PLACE_HOLDER_BASE_DISTANCE.getMessage(), "PLAYER_DISTANCE_TO_BASE", "BASE_DISTANCE", "DISTANCE", "DIST"),
    BASE_DIRECTION(PlaceHolderUtils::getBaseDirection, Messages.PLACE_HOLDER_BASE_DIRECTION.getMessage(), "PLAYER_BASE_DIRECTION", "BASE_DIRECTION", "DIRECTION", "ARROW", "ARROWS"),
    BASE_OR_PORTAL(PlaceHolderUtils::getBaseOrPortal, Messages.PLACE_HOLDER_BASE_OR_PORTAL.getMessage(), "BASE_OR_PORTAL", "BASE_PORTAL"),
    NEAREST_TEAM_BASE(PlaceHolderUtils::getNearestTeamBase, Messages.PLACE_HOLDER_ENEMY_TEAM_BASE.getMessage(), "NEAREST_TEAM_BASE", "ENEMY_TEAM_BASE", "ENEMY_BASE"),
    NEAREST_BASE_DIRECTION(PlaceHolderUtils::getNearestBaseDirection, Messages.PLACE_HOLDER_ENEMY_TEAM_BASE_DIRECTION.getMessage(), "NEAREST_BASE_DIRECTION", "ENEMY_BASE_DIRECTION", "ENEMY_BASE_DIR", "ENEMY_DIR"),

    PVPCAP(PlaceHolderUtils.GAME_SUPPLIER, Game::isPvpEnabled, Messages.PLACE_HOLDER_ACTIVE_PVP.getMessage(), "PVP?"),
    TNTCAP(PlaceHolderUtils.GAME_SUPPLIER, Game::isAssaultsEnabled, Messages.PLACE_HOLDER_ACTIVE_ASSAULTS.getMessage(), "TNT?"),
    NETHERCAP(PlaceHolderUtils.GAME_SUPPLIER, Game::isNetherEnabled, Messages.PLACE_HOLDER_OPEN_NETHER.getMessage(), "NETHER?"),
    ENDCAP(PlaceHolderUtils.GAME_SUPPLIER, Game::isEndEnabled, Messages.PLACE_HOLDER_OPEN_END.getMessage(), "END?");

	private final BiFunction<Player, Integer, ?> callable;
	private final String description;
	private final List<String> keys;
	
    <T> PlaceHolder(Supplier<T> supplier, Function<T, ?> callable, String description, String... rawKeys)
    {
        this((BiFunction<Player, Integer, ?>) (Player p, Integer i) -> callable.apply(supplier.get()), description, rawKeys);
    }
    
    PlaceHolder(Function<Player, ?> callable, String description, String... rawKeys)
    {
        this((BiFunction<Player, Integer, ?>) (Player p, Integer i) -> callable.apply(p), description, rawKeys);
    }
    
	PlaceHolder(BiFunction<Player, Integer, ?> callable, String description, String... rawKeys)
	{
		this.callable = callable;
		this.description = description;
		this.keys = Arrays.stream(rawKeys).map(key -> "{" + key + "}").collect(Collectors.toList());
	}

	public String replace(String chainToProcess, Player player, int iteration)
	{
		Object replaceValue = callable.apply(player, iteration);
		if(replaceValue instanceof Boolean)
			replaceValue = Fk.getInstance().getScoreboardManager().format((boolean) replaceValue);
		for (String key : keys)
			chainToProcess = chainToProcess.replace(key, String.valueOf(replaceValue));
		return chainToProcess;
	}

	public String getShortestKey()
	{
		Comparator<String> byLength = Comparator.comparingInt(String::length);
		return keys.stream().min(byLength).orElseThrow(RuntimeException::new);
	}
	
	public String getDescription()
	{
		return description;
	}

	public BiFunction<Player, Integer, ?> getFunction()
	{
		return callable;
	}

	public boolean isInLine(String s)
	{
		return keys.parallelStream().anyMatch(s::contains);
	}
}
