package fr.devsylone.fallenkingdom.scoreboard;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.utils.PlaceHolderUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum PlaceHolder
{

	DAY(Fk.getInstance().getGame(), Game::getDays, "Jour", "DAY", "DAYS", "JOUR", "JOURS", "D", "J"),
	HOUR(Fk.getInstance().getGame(), Game::getHour, "Heure", "Heure", "HOUR", "HOURS", "HEURE", "HEURES", "H"),
	MINUTE(Fk.getInstance().getGame(), Game::getMinute, "Minutes", "Minute", "MINUTE", "MINUTES", "M"),
	TEAM(PlaceHolderUtils::getTeamOf, "Équipe du joueur", "PLAYER_TEAM", "TEAM", "EQUIPE"),
	DEATHS(PlaceHolderUtils::getDeaths, "Nombre de morts", "PLAYER_DEATHS", "DEATHS", "MORTS"),
	KILLS(PlaceHolderUtils::getKills,"Nombre de kills", "PLAYER_KILLS", "KILLS"),

	BASE_DISTANCE(PlaceHolderUtils::getBaseDistance,"Distance de la base", "PLAYER_DISTANCE_TO_BASE", "BASE_DISTANCE", "DISTANCE", "DIST"),
	BASE_DIRECTION(PlaceHolderUtils::getBaseDirection,"Direction de la base", "PLAYER_BASE_DIRECTION", "BASE_DIRECTION", "DIRECTION", "ARROW", "ARROWS"),
	BASE_OR_PORTAL(PlaceHolderUtils::getBaseOrPortal, "Base/portail selon la dimension", "BASE_OR_PORTAL", "BASE_PORTAL"),
	NEAREST_TEAM_BASE(PlaceHolderUtils::getNearestTeamBase, "Base ennemie la plus proche", "NEAREST_TEAM_BASE", "ENEMY_TEAM_BASE", "ENEMY_BASE"),
	NEAREST_BASE_DIRECTION(PlaceHolderUtils::getNearestBaseDirection, "Direction de la base ennemie la plus proche", "NEAREST_BASE_DIRECTION", "ENEMY_BASE_DIRECTION", "ENEMY_BASE_DIR", "ENEMY_DIR"),

	PVPCAP(Fk.getInstance().getGame(), Game::isPvpEnabled, "Pvp actif ?", "PVP?"),
	TNTCAP(Fk.getInstance().getGame(), Game::isAssaultsEnabled, "Assauts actifs ?", "TNT?"),
	NETHERCAP(Fk.getInstance().getGame(), Game::isNetherEnabled, "Nether ouvert ?", "NETHER?"),
	ENDCAP(Fk.getInstance().getGame(), Game::isEndEnabled, "End ouvert ?", "END?");

	private final BiFunction<Player, Integer, ?> callable;
	private final String description;
	private final List<String> keys;

	<T> PlaceHolder(T game, Function<T, ?> callable, String description, String... rawKeys)
	{
		this((BiFunction<Player, Integer, ?>) (Player p, Integer i) -> callable.apply(game), description, rawKeys);
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
			replaceValue = String.valueOf((boolean) replaceValue ? Fk.getInstance().getScoreboardManager().getCustomStrings().get("stringTrue") : Fk.getInstance().getScoreboardManager().getCustomStrings().get("stringFalse"));
		for (String key : keys)
			chainToProcess = chainToProcess.replace(key, String.valueOf(replaceValue));
		return chainToProcess;
	}

	public String getShortestKey()
	{
		Comparator<String> byLength = (e1, e2) -> e1.length() > e2.length() ? -1 : 1;
		return keys.stream().max(byLength).get();
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
