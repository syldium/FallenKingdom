package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.display.change.DisplayChange;
import fr.devsylone.fallenkingdom.display.change.SetScoreboardLineChange;
import fr.devsylone.fallenkingdom.display.change.SetScoreboardTitleChange;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.ConfigHelper;
import fr.devsylone.fkpi.util.Saveable;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static fr.devsylone.fallenkingdom.display.DisplayType.ACTIONBAR;
import static fr.devsylone.fallenkingdom.display.DisplayType.BOSSBAR;
import static fr.devsylone.fallenkingdom.display.DisplayType.SCOREBOARD;
import static java.util.Objects.requireNonNull;

public class GlobalDisplayService implements DisplayService, Saveable {

    private final DisplayText text = new DisplayText();
    private final Stack<DisplayChange<?>> revisions = new Stack<>();
    private Map<DisplayType, DisplayService> services;
    private ScoreboardDisplayService scoreboard;

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
        for (DisplayService service : this.services.values()) {
            service.update(player, fkPlayer, placeHolders);
        }
    }

    @Override
    public void hide(@NotNull Player player, @NotNull FkPlayer fkPlayer) {
        for (DisplayService service : this.services.values()) {
            service.hide(player, fkPlayer);
        }
    }

    public void updateAll(PlaceHolder... placeHolders) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!Fk.getInstance().getWorldManager().isAffected(player.getWorld())) {
                continue;
            }
            final FkPlayer fkPlayer = Fk.getInstance().getPlayerManager().getPlayer(player);
            for (DisplayService service : this.services.values()) {
                service.update(player, fkPlayer, placeHolders);
            }
        }
    }

    public @NotNull ScoreboardDisplayService scoreboard() {
        return this.scoreboard;
    }

    private void setScoreboard(@NotNull ScoreboardDisplayService scoreboard) {
        this.scoreboard = scoreboard;
        this.services.put(SCOREBOARD, scoreboard);
    }

    public boolean setScoreboardLine(int line, @Nullable String value) {
        final SetScoreboardLineChange change = new SetScoreboardLineChange(this.scoreboard, line, value);
        this.revisions.push(change);
        this.setScoreboard(change.apply(this.scoreboard));
        return true;
    }

    public void setScoreboardLines(@NotNull List<@NotNull String> lines) {
        this.setScoreboard(this.scoreboard.withLines(lines));
    }

    public void setScoreboardTitle(@NotNull String title) {
        final SetScoreboardTitleChange change = new SetScoreboardTitleChange(this.scoreboard, title);
        this.revisions.push(change);
        this.setScoreboard(change.apply(this.scoreboard));
    }

    public @NotNull DisplayText text() {
        return this.text;
    }

    public static final String FILENAME = "display.yml";
    private static final String COLOR = "color";
    private static final String SIDEBAR = "sidebar";
    private static final String STYLE = "style";
    private static final String TITLE = "title";

    @Override
    public void load(ConfigurationSection config) {
        final Map<DisplayType, DisplayService> services = new EnumMap<>(DisplayType.class);
        if (config.contains(ACTIONBAR.asString())) {
            services.put(ACTIONBAR, new ActionBarDisplayService(config.getString(ACTIONBAR.asString(), "")));
        }
        if (config.contains(BOSSBAR.asString())) {
            final ConfigurationSection section = requireNonNull(config.getConfigurationSection(BOSSBAR.asString()), "bossbar config has no section");
            services.put(BOSSBAR, new MultipleBossBarDisplayService(
                    section.getString(TITLE, ""),
                    ConfigHelper.enumValueOf(BarColor.class, section.getString(COLOR), BarColor.WHITE),
                    ConfigHelper.enumValueOf(BarStyle.class, section.getString(STYLE), BarStyle.SOLID)
            ));
        }
        if (config.contains(SCOREBOARD.asString())) {
            final ConfigurationSection section = requireNonNull(config.getConfigurationSection(SCOREBOARD.asString()), "scoreboard config has no section");
            this.scoreboard = new ScoreboardDisplayService(section.getString(TITLE, ""), section.getStringList(SIDEBAR));
            services.put(SCOREBOARD, this.scoreboard);
        } else {
            this.scoreboard = new ScoreboardDisplayService();
        }
        this.services = services;
        this.text.load(config);
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
                    final ConfigurationSection section = config.createSection(key);
                    section.set(COLOR, ((BossBarDisplayService) service).color().name());
                    section.set(STYLE, ((BossBarDisplayService) service).style().name());
                    section.set(TITLE, value);
                } else {
                    config.set(key, value);
                }
            }
        }
        this.text.save(config);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean undo() {
        if (this.revisions.empty()) {
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
