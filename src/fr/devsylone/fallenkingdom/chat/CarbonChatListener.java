package fr.devsylone.fallenkingdom.chat;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.ITeam;
import fr.devsylone.fkpi.api.event.PlayerTeamChangeEvent;
import fr.devsylone.fkpi.api.event.TeamUpdateEvent;
import fr.devsylone.fkpi.teams.Team;
import net.draycia.carbon.api.CarbonChatProvider;
import net.draycia.carbon.api.users.Party;
import net.draycia.carbon.api.users.UserManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

/**
 * Listener for chat-handling with Carbon chat
 */
public class CarbonChatListener implements Listener {
    UserManager<?> um = CarbonChatProvider.carbonChat().userManager();
    ConcurrentHashMap<ITeam, UUID> parties = new ConcurrentHashMap<>();

    @EventHandler
    public void onTeamChange(TeamUpdateEvent event) {
        ITeam team = event.getTeam();
        switch (event.getUpdateType()) {
            case CREATION:
            case DELETION:
                if (!parties.containsKey(team)) {
                    break;
                }
                um.party(parties.get(team)).thenAcceptAsync(party -> {
                    party.disband();
                });
                parties.remove(team);
        }

    }

    @EventHandler
    public void onPlayerTeamChange(PlayerTeamChangeEvent event) {
        final ITeam from = event.getFrom();
        final ITeam who = event.getTeam();
        final Player player = Fk.getInstance().getServer().getPlayer(event.getPlayerName());

        if (from != null) {
            um.party(parties.get(from)).thenAcceptAsync(party -> {
                party.removeMember(player.getUniqueId());
                if (party.members().isEmpty()) {
                    parties.remove(from);
                }
            });
        }
        if (who != null) {
            if (!parties.containsKey(who)) {
                Party p = um.createParty(Component.text(who.getName())
                        .color(TextColor.color(who.getChatColor().getColor().getRGB())));
                p.addMember(player.getUniqueId());
                parties.put(who, p.id());
            } else {
                um.party(parties.get(who)).thenAcceptAsync(party -> {
                    party.addMember(player.getUniqueId());
                });
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        Team team = FkPI.getInstance().getTeamManager().getPlayerTeam(p);
        if (team != null) {
            if (parties.containsKey(team)) {
                um.party(parties.get(team)).thenAcceptAsync(party -> {
                    party.addMember(p.getUniqueId());
                });
            } else {
                parties.put(team, createPartyWith(p, team));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        Team team = FkPI.getInstance().getTeamManager().getPlayerTeam(p);
        if (team != null && parties.containsKey(team)) {
            leaveParty(team, p);
        }
    }

    private void leaveParty(Team team, Player player) {
        um.party(parties.get(team)).thenAcceptAsync(party -> {
            party.removeMember(player.getUniqueId());
            if (party.members().isEmpty()) {
                parties.remove(team);
            }
        });
    }

    private UUID createPartyWith(Player player, Team team) {
        Party ret = um.createParty(Component.text(team.getName())
                .color(TextColor.color(team.getChatColor().getColor().getRGB())));
        ret.addMember(player.getUniqueId());
        return ret.id();
    }
}
