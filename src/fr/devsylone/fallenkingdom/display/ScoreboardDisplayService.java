package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class ScoreboardDisplayService implements DisplayService {

    private final String title;
    private final List<String> lines;
    private final List<Set<PlaceHolder>> indexes;
    private final Map<PlaceHolder, List<Integer>> placeHolders;

    public ScoreboardDisplayService() {
        this("", Collections.emptyList());
    }

    public ScoreboardDisplayService(@NotNull String title, @NotNull List<@NotNull String> lines) {
        this.title = requireNonNull(title,"scoreboard title");
        requireNonNull(lines,"scoreboard lines");
        if (lines.isEmpty()) {
            this.lines = Collections.emptyList();
            this.indexes = Collections.emptyList();
            this.placeHolders = Collections.emptyMap();
            return;
        }

        this.lines = new ArrayList<>(lines);
        this.indexes = new ArrayList<>(lines.size());
        this.placeHolders = new EnumMap<>(PlaceHolder.class);
        for (int i = 0; i < lines.size(); i++) {
            final String value = requireNonNull(lines.get(i), "scoreboard line");
            final Set<PlaceHolder> set = EnumSet.noneOf(PlaceHolder.class);
            for (PlaceHolder placeholder : PlaceHolder.values()) {
                if (value.contains(placeholder.getKey())) {
                    set.add(placeholder);
                    this.placeHolders.computeIfAbsent(placeholder, s -> new ArrayList<>(2)).add(i);
                }
            }
            this.indexes.add(set);
        }
    }

    public static @NotNull ScoreboardDisplayService createDefault() {
        final List<String> lines = new ArrayList<>(Arrays.asList(Messages.SCOREBOARD_DEFAULT.getMessage().split("\n")));
        lines.add(ChatUtils.DEVSYLONE);
        return new ScoreboardDisplayService(Messages.PREFIX_FK.getMessage(), lines);
    }

    @Override
    public boolean contains(@NotNull PlaceHolder placeHolder) {
        return this.placeHolders.containsKey(placeHolder);
    }

    @Override
    public boolean containsAny(@NotNull PlaceHolder... placeHolders) {
        for (PlaceHolder placeHolder : placeHolders) {
            if (this.placeHolders.containsKey(placeHolder)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void update(@NotNull Player player, @NotNull FkPlayer fkPlayer, @NotNull PlaceHolder... placeHolders) {
        if (placeHolders.length == 0) {
            fkPlayer.getScoreboard().updateLines(this.renderLines(player, fkPlayer));
            return;
        }

        final Set<Integer> visitedLines = new HashSet<>();
        for (PlaceHolder placeHolderToRerender : placeHolders) {
            final List<Integer> lines = this.placeHolders.get(placeHolderToRerender);
            if (lines == null) {
                continue;
            }

            for (Integer line : lines) {
                if (visitedLines.contains(line)) {
                    continue;
                }
                fkPlayer.getScoreboard().updateLine(
                        line,
                        this.renderLine(player, fkPlayer, line)
                );
                visitedLines.add(line);
            }
        }
    }

    @Override
    public void hide(@NotNull Player player, @NotNull FkPlayer fkPlayer) {
        fkPlayer.removeScoreboard();
    }

    public void updateLine(@NotNull Player player, @NotNull FkPlayer fkPlayer, int line) {
        fkPlayer.getScoreboard().updateLine(line, this.renderLine(player, fkPlayer, line));
    }

    public int reverseIndex(int index) {
        return this.size() - index - 1;
    }

    public @NotNull String renderLine(@NotNull Player player, @NotNull FkPlayer fkPlayer, int line) {
        String replaced = this.lines.get(line);
        if (!fkPlayer.useFormattedText()) {
            return ChatUtils.translateColorCodeToAmpersand(replaced);
        }
        for (PlaceHolder placeHolder : this.indexes.get(line)) {
            final int usageIndex = this.placeHolders.get(placeHolder).indexOf(line);
            replaced = placeHolder.replace(replaced, player, usageIndex);
        }
        return replaced;
    }

    public @NotNull String title() {
        return this.title;
    }

    public @NotNull @UnmodifiableView List<@NotNull String> lines() {
        return Collections.unmodifiableList(this.lines);
    }

    public @NotNull List<String> renderLines(@NotNull Player player, @NotNull FkPlayer fkPlayer) {
        final List<String> rendered = new ArrayList<>(this.size());
        for (int i = 0; i < this.size(); i++) {
            rendered.add(this.renderLine(player, fkPlayer, i));
        }
        return rendered;
    }

    public @NotNull String line(int index) {
        return this.lines.get(index);
    }

    public int size() {
        return this.lines.size();
    }

    @Contract("_, _ -> new")
    public @NotNull ScoreboardDisplayService withLine(int line, @Nullable String value) {
        final List<String> lines;
        if (value == null) {
            // Supprimer une ligne
            if (0 > line || line >= this.size()) {
                throw new IllegalArgumentException("Index " + line + " of the line to be removed is invalid for length " + this.size() + ".");
            }
            lines = new ArrayList<>(this.lines);
            lines.remove(line);
        } else {
            // Éditer/Ajouter une ligne
            if (line < 0) {
                // Rajouter au début
                lines = new ArrayList<>(this.size() - line);
                lines.add(value);
                fillWith(lines, line + 1, 0);
                lines.addAll(this.lines);
            } else {
                // Rajouter à la fin/Éditer
                lines = new ArrayList<>(Math.max(this.size(), line + 1));
                lines.addAll(this.lines);
                fillWith(lines, this.size(), line + 1);
                lines.set(line, value);
            }
        }
        return new ScoreboardDisplayService(this.title, lines);
    }

    @Contract("_ -> new")
    public @NotNull ScoreboardDisplayService withLines(@NotNull List<@NotNull String> lines) {
        return new ScoreboardDisplayService(this.title, lines);
    }

    @Contract("_ -> new")
    public ScoreboardDisplayService withTitle(@NotNull String title) {
        return new ScoreboardDisplayService(title, this.lines);
    }

    public boolean isDefaultSidebar() {
        String[] def = Messages.SCOREBOARD_DEFAULT.getMessage().split("\n");
        if (def.length != this.lines.size() && def.length != this.lines.size() - 1) {
            return false;
        }

        for (int i = 0; i < def.length; i++) {
            if (!def[i].equals(this.lines.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static void fillWith(@NotNull List<@NotNull String> lines, int from, int to) {
        for (int i = from; i < to; i++) {
            lines.add("");
        }
    }
}
