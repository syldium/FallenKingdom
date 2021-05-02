package fr.devsylone.fallenkingdom.version;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fkpi.teams.Team;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

public class LuckPermsContext implements ContextCalculator<Player> {

    private static final String[] GAME_STATES = Arrays.stream(Game.GameState.values()).map(Game.GameState::asString).toArray(String[]::new);
    private static final String GAME_KEY = "fk:game";
    private static final String TEAM_KEY = "fk:team";

    private final Fk plugin;

    public LuckPermsContext(@NotNull Fk plugin) {
        this.plugin = plugin;
        requireNonNull(plugin.getServer().getServicesManager().load(LuckPerms.class), "LuckPerms service").getContextManager().registerCalculator(this);
    }

    @Override
    public void calculate(@NotNull Player player, @NotNull ContextConsumer contextConsumer) {
        final Game game = this.plugin.getGame();
        final Team team = this.plugin.getFkPI().getTeamManager().getPlayerTeam(player);
        contextConsumer.accept(GAME_KEY, game.getState().asString());
        if (team != null) {
            contextConsumer.accept(TEAM_KEY, team.getName());
        }
    }

    @Override
    public ContextSet estimatePotentialContexts() {
        ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
        for (String state : GAME_STATES) {
            builder.add(GAME_KEY, state);
        }
        for (Team team : this.plugin.getFkPI().getTeamManager().getTeams()) {
            builder.add(TEAM_KEY, team.getName());
        }
        return builder.build();
    }
}
