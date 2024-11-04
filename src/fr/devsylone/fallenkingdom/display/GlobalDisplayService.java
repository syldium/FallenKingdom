package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.display.change.DisplayChange;
import fr.devsylone.fallenkingdom.display.change.SetScoreboardLineChange;
import fr.devsylone.fallenkingdom.display.change.SetScoreboardTitleChange;
import fr.devsylone.fallenkingdom.display.notification.ChatChannel;
import fr.devsylone.fallenkingdom.display.notification.GameNotification;
import fr.devsylone.fallenkingdom.display.notification.NotificationChannel;
import fr.devsylone.fallenkingdom.display.progress.ProgressBar;
import fr.devsylone.fallenkingdom.display.sound.SoundPlayer;
import fr.devsylone.fallenkingdom.display.tick.CycleTickFormatter;
import fr.devsylone.fallenkingdom.display.tick.TickFormatter;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fkpi.util.Saveable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Deque;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static fr.devsylone.fallenkingdom.display.DisplayType.ACTIONBAR;
import static fr.devsylone.fallenkingdom.display.DisplayType.BOSSBAR;
import static fr.devsylone.fallenkingdom.display.DisplayType.SCOREBOARD;
import static java.util.Objects.requireNonNull;

/**
 * Point d'entrée de tous les supports d'affichage.
 * <p>
 * Ce service n'étant pas forcément hydraté de la configuration tout de suite,
 * chaque propriété doit être initialisée à une valeur vide, mais pas {@code
 * null} (no-op).
 */
public class GlobalDisplayService implements DisplayService, Saveable {

    private static final int STACK_MAX_SIZE = 10;

    private final DisplayText text = new DisplayText();
    private final Deque<DisplayChange<?>> revisions = new LinkedList<>();
    private Map<DisplayType, DisplayService> services;
    private ScoreboardDisplayService scoreboard;

    private ProgressBar.Provider barProvider = ProgressBar.Provider.EMPTY;
    private TickFormatter tickFormatter = new CycleTickFormatter();

    private SoundPlayer deathSound = SoundPlayer.EMPTY;
    private SoundPlayer gameStartSound = SoundPlayer.EMPTY;
    private SoundPlayer eliminationSound = SoundPlayer.EMPTY;
    private SoundPlayer eventSound = SoundPlayer.EMPTY;

    private NotificationChannel regionChangeDispatcher = new ChatChannel();

    public GlobalDisplayService() {
        this.services = Collections.emptyMap();
        this.scoreboard = new ScoreboardDisplayService();
    }

    @Override
    public boolean contains(@NotNull PlaceHolder placeHolder) {
        for (DisplayService service : this.services.values()) {
            if (service.contains(placeHolder)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAny(@NotNull PlaceHolder... placeHolders) {
        for (DisplayService service : this.services.values()) {
            if (service.containsAny(placeHolders)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void update(@NotNull Player player, @NotNull FkPlayer fkPlayer, @NotNull PlaceHolder... placeHolders) {
        if (this.isPreStart() && fkPlayer.getState() == FkPlayer.PlayerState.INGAME) {
            if (placeHolders.length == 0) {
                fkPlayer.refreshScoreboard();
            }
        } else {
            for (DisplayService service : this.services.values()) {
                service.update(player, fkPlayer, placeHolders);
            }
        }
    }

    @Override
    public void hide(@NotNull Player player, @NotNull FkPlayer fkPlayer) {
        for (DisplayService service : this.services.values()) {
            service.hide(player, fkPlayer);
        }
    }

    public void updateAll(PlaceHolder... placeHolders) {
        final boolean preStart = this.isPreStart();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!Fk.getInstance().getWorldManager().isAffected(player.getWorld())) {
                continue;
            }
            final FkPlayer fkPlayer = Fk.getInstance().getPlayerManager().getPlayer(player);
            if (preStart && fkPlayer.getState() == FkPlayer.PlayerState.INGAME) {
                if (placeHolders.length == 0) {
                    fkPlayer.refreshScoreboard();
                }
            } else {
                for (DisplayService service : this.services.values()) {
                    service.update(player, fkPlayer, placeHolders);
                }
            }
        }
    }

    public void updateAllScoreboards(int line) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Fk.getInstance().getWorldManager().isAffected(player.getWorld())) {
                final FkPlayer fkPlayer = Fk.getInstance().getPlayerManager().getPlayer(player);
                this.scoreboard.updateLine(player, fkPlayer, line);
            }
        }
    }

    public void hideAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Fk.getInstance().getWorldManager().isAffected(player.getWorld())) {
                final FkPlayer fkPlayer = Fk.getInstance().getPlayerManager().getPlayer(player);
                for (DisplayService service : this.services.values()) {
                    service.hide(player, fkPlayer);
                }
            }
        }
    }

    public boolean isPreStart() {
        return Fk.getInstance().getGame().isPreStart();
    }

    public @NotNull ScoreboardDisplayService scoreboard() {
        return this.scoreboard;
    }

    public void setScoreboard(@NotNull ScoreboardDisplayService scoreboard) {
        this.scoreboard = scoreboard;
        this.services.put(SCOREBOARD, scoreboard);
    }

    public boolean setScoreboardLine(int line, @Nullable String value) {
        final SetScoreboardLineChange change = new SetScoreboardLineChange(this.scoreboard, line, value);
        this.pushChange(change);
        this.setScoreboard(change.apply(this.scoreboard));
        return true;
    }

    public void setScoreboardLines(@NotNull List<@NotNull String> lines) {
        this.setScoreboard(this.scoreboard.withLines(lines));
    }

    public void setScoreboardTitle(@NotNull String title) {
        final SetScoreboardTitleChange change = new SetScoreboardTitleChange(this.scoreboard, title);
        this.pushChange(change);
        this.setScoreboard(change.apply(this.scoreboard));
    }

    private void pushChange(@NotNull DisplayChange<?> change) {
        this.revisions.push(change);
        if (this.revisions.size() > STACK_MAX_SIZE) {
            this.revisions.removeFirst();
        }
    }

    public @NotNull DisplayText text() {
        return this.text;
    }

    public static final String FILENAME = "display.yml";
    private static final String SIDEBAR = "sidebar";
    private static final String TITLE = "title";
    private static final String DEATH_SOUND = "death-sound";
    private static final String GAME_START_SOUND = "game-start-sound";
    private static final String ELIMINATION_SOUND = "elimination-sound";
    private static final String EVENT_SOUND = "event-sound";
    private static final String PROGRESSBAR = "progressbar";
    private static final String TICK_FORMAT = "tick-format";
    private static final String NOTIFICATION = "notification";
    private static final String REGION_CHANGE = "region-change";

    @Override
    public void load(ConfigurationSection config) {
        this.hideAll();
        final Map<DisplayType, DisplayService> services = new EnumMap<>(DisplayType.class);
        if (config.contains(ACTIONBAR.asString())) {
            services.put(ACTIONBAR, new ActionBarDisplayService(config.getString(ACTIONBAR.asString(), "")));
        }
        if (config.contains(BOSSBAR.asString())) {
            final ConfigurationSection section = requireNonNull(config.getConfigurationSection(BOSSBAR.asString()), "bossbar config has no section");
            services.put(BOSSBAR, new MultipleBossBarDisplayService(section));
        }
        if (config.contains(SCOREBOARD.asString())) {
            final ConfigurationSection section = requireNonNull(config.getConfigurationSection(SCOREBOARD.asString()), "scoreboard config has no section");
            this.scoreboard = new ScoreboardDisplayService(section.getString(TITLE, ""), section.getStringList(SIDEBAR));
        } else {
            this.scoreboard = ScoreboardDisplayService.createDefault();
        }
        services.put(SCOREBOARD, this.scoreboard);

        this.barProvider = ProgressBar.Provider.fromConfig(config.getConfigurationSection(PROGRESSBAR));
        if (config.contains(TICK_FORMAT)) {
            this.tickFormatter = TickFormatter.fromConfig(requireNonNull(config.getConfigurationSection(TICK_FORMAT), "tick formatter config has no section"));
        }

        this.deathSound = SoundPlayer.fromConfig(config.getConfigurationSection(DEATH_SOUND), SoundPlayer.deathSound());
        this.gameStartSound = SoundPlayer.fromConfig(config.getConfigurationSection(GAME_START_SOUND), SoundPlayer.gameStartSound());
        this.eliminationSound = SoundPlayer.fromConfig(config.getConfigurationSection(ELIMINATION_SOUND), SoundPlayer.eliminationSound());
        this.eventSound = SoundPlayer.fromConfig(config.getConfigurationSection(EVENT_SOUND), SoundPlayer.eventSound());

        this.services = services;
        this.text.load(config);

        final ConfigurationSection notification = config.getConfigurationSection(NOTIFICATION);
        if (notification != null) {
            this.regionChangeDispatcher = NotificationChannel.fromConfig(notification.getString(REGION_CHANGE));
        }
    }

    @Override
    public void loadNullable(ConfigurationSection config) {
        this.load(config == null ? new MemoryConfiguration() : config);
    }

    @Override
    public void save(ConfigurationSection config) {
        for (Map.Entry<DisplayType, DisplayService> entry : this.services.entrySet()) {
            final String key = entry.getKey().asString();
            final DisplayService service = entry.getValue();
            if (service instanceof ScoreboardDisplayService) {
                final ConfigurationSection section = config.createSection(key);
                section.set(TITLE, ((ScoreboardDisplayService) service).title());
                section.set(SIDEBAR, ((ScoreboardDisplayService) service).lines());
            } else {
                final String value = ((SimpleDisplayService) service).value();
                if (service instanceof BossBarDisplayService) {
                    ((MultipleBossBarDisplayService) service).save(config.createSection(key));
                } else {
                    config.set(key, value);
                }
            }
        }

        this.barProvider.save(config.createSection(PROGRESSBAR));
        this.tickFormatter.save(config.createSection(TICK_FORMAT));
        this.deathSound.save(config.createSection(DEATH_SOUND));
        this.gameStartSound.save(config.createSection(GAME_START_SOUND));
        this.eliminationSound.save(config.createSection(ELIMINATION_SOUND));
        this.eventSound.save(config.createSection(EVENT_SOUND));
        this.text.save(config);

        final ConfigurationSection notification = config.createSection(NOTIFICATION);
        notification.set(REGION_CHANGE, NotificationChannel.name(this.regionChangeDispatcher));
    }

    public @NotNull ProgressBar initProgressBar(@NotNull Player player, @NotNull Location location) {
        return this.barProvider.init(player, location);
    }

    @Contract("_ -> new")
    public @NotNull TickFormatter configureTickFormatter(int dayDuration) {
        return this.tickFormatter.withDayDuration(dayDuration);
    }

    public @NotNull TickFormatter baseTickFormatter() {
        return this.tickFormatter;
    }

    public void playDeathSound(@NotNull Player player) {
        this.deathSound.play(player);
    }

    public void playGameStartSound(@NotNull Player player) {
        this.gameStartSound.play(player);
    }

    public void playEliminationSound(@NotNull Player player) {
        this.eliminationSound.play(player);
    }

    public void playEventSound(@NotNull Player player) {
        this.eventSound.play(player);
    }

    public void dispatch(@NotNull GameNotification notification, @NotNull Player player) {
        this.regionChangeDispatcher.send(player, notification);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean undo() {
        if (this.revisions.isEmpty()) {
            return false;
        }
        final DisplayChange change = this.revisions.pop();
        final DisplayType type = change.type();
        final DisplayService service = this.services.get(type);
        if (service == null) {
            throw new IllegalStateException("Unable to recreate the display service.");
        }
        final DisplayService changed = change.revert(service);
        this.services.put(type, changed);
        if (type == SCOREBOARD) {
            this.scoreboard = (ScoreboardDisplayService) changed;
        }
        return true;
    }
}
