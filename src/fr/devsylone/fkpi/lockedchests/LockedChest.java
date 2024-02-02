package fr.devsylone.fkpi.lockedchests;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.display.progress.ProgressBar;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.XAdvancement;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.event.PlayerLockedChestInteractEvent;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fkpi.util.Saveable;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import static fr.devsylone.fallenkingdom.utils.KeyHelper.parseKey;

public class LockedChest implements Saveable {
    public enum ChestState {
        DONE, LOCKED, UNLOCKING, UNLOCKED
    }

    private UUID unlocker;
    private ChatColor chatColor = ChatColor.RESET;
    private Location loc;
    private ChestState state = ChestState.LOCKED;
    private long lastInteract;
    private long startUnlocking;
    private float yFix = -0.5F;

    private int task = -1;

    private ArrayList<LockedChestLoadout> loadouts = new ArrayList<>();

    private LockedChestLoadout activeLoadout;
    private int activeDay;

    private String name;

    public LockedChest(Location loc, String name) {
        this.loc = loc;
        this.lastInteract = System.currentTimeMillis();
        this.name = name;
        this.state = ChestState.DONE;
    }

    public LockedChest(ConfigurationSection config) {
        load(config);
    }

    public Location getLocation() {
        return loc.clone();
    }

    public UUID getUnlocker() {
        return unlocker;
    }

    /**
     * @deprecated {@link #getUnlockingTimeSecs()}
     */
    @Deprecated
    public int getUnlockingTime() {
        return getUnlockingTimeSecs();
    }

    public int getUnlockingTimeSecs() {
        if (state == ChestState.DONE) {
            return 0;
        }
        return activeLoadout.getTime() / 1000;
    }

    public String getName() {
        return name;
    }

    public void changeUnlocker(Player newPlayer) {
        unlocker = newPlayer == null ? null : newPlayer.getUniqueId();
        startUnlocking = System.currentTimeMillis();

        if (newPlayer == null)
            setState(ChestState.LOCKED);
        else
            setState(ChestState.UNLOCKING);

        Team team = FkPI.getInstance().getTeamManager().getPlayerTeam(newPlayer);
        chatColor = team == null ? ChatColor.RESET : team.getChatColor();
    }

    public ChestState getState() {
        return state;
    }

    public void setState(ChestState state) {
        this.state = state;
    }

    public void updateLastInteract() {
        lastInteract = System.currentTimeMillis();
    }

    public void setYFixByBlockFace(BlockFace blockFace) {
        switch (blockFace) {
            case UP:
                yFix = -1.25F;
                break;
            case DOWN:
                yFix = 0;
                break;
            default:
                yFix = -0.5F;
        }
    }

    public void setRequiredAdvancement(String advancement) {
        for (LockedChestLoadout l : loadouts) {
            if (l == null) {
                continue;
            }
            l.setAdvancement(advancement);
        }
    }

    private Runnable unlockPerTickUpdate(Player player) {
        final Location loc = this.loc.clone().add(0.5, this.yFix, 0.5);
        final ProgressBar bar = Fk.getInstance().getDisplayService().initProgressBar(player, loc);
        final LockedChestLoadout loadout = activeLoadout;

        return () -> {
            double progress =
                    (double) (System.currentTimeMillis() - this.startUnlocking) / loadout.getTime();
            loc.setY(this.loc.getY() + this.yFix);
            if (player.isOnline())
                bar.progress(player, loc, progress);

            if (lastInteract + 1000 < System.currentTimeMillis()) {
                if (!getState().equals(ChestState.UNLOCKED))
                    Fk.broadcast(Messages.BROADCAST_LOCKED_CHEST_ABORT.getMessage()
                            .replace("%name%", name)
                            .replace("%player%", chatColor + player.getName()));
                Bukkit.getScheduler().cancelTask(task);
                changeUnlocker(null);
                if (player.isOnline())
                    bar.remove(player);
            }

            if (startUnlocking + loadout.getTime() <= System.currentTimeMillis()) {
                PlayerLockedChestInteractEvent endEvent =
                        new PlayerLockedChestInteractEvent(player, this); // EVENT
                Bukkit.getPluginManager().callEvent(endEvent); // EVENT
                if (!endEvent.isCancelled()) {
                    setState(ChestState.UNLOCKED);
                    Fk.broadcast(Messages.BROADCAST_LOCKED_CHEST_UNLOCKED.getMessage()
                            .replace("%name%", name)
                            .replace("%player%", chatColor + player.getName()));
                }
                Bukkit.getScheduler().cancelTask(task);
                if (player.isOnline())
                    bar.remove(player);
            }
        };
    }

    public void startUnlocking(Player player) {
        if (state == ChestState.DONE || state == ChestState.UNLOCKED) {
            return;
        }
        PlayerLockedChestInteractEvent event = new PlayerLockedChestInteractEvent(player, this); // EVENT
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;

        if (unlocker != null)
            Fk.broadcast(Messages.BROADCAST_LOCKED_CHEST_ABORT.getMessage().replace("%name%", name)
                    .replace("%player%",
                            chatColor.toString() + Bukkit.getOfflinePlayer(unlocker).getName()));

        changeUnlocker(player);
        Fk.broadcast(Messages.BROADCAST_LOCKED_CHEST_START.getMessage().replace("%name%", name)
                .replace("%player%", chatColor + player.getName()));

        if (task > 0)
            Bukkit.getScheduler().cancelTask(task);

        lastInteract = System.currentTimeMillis();
        task = Bukkit.getScheduler()
                .runTaskTimer(Fk.getInstance(), unlockPerTickUpdate(player), 1L, 1L).getTaskId();
    }

    /**
     * Add a chest loadout to the chest. Overwrites the loadout set on that day if there is one.
     * 
     * @param day The day for which the loadout is active.
     * @param unlockTime The time taken to unlock the chest with this loadout active.
     * @param expiry How long it takes before the loadout expires
     * @param advancements Required advancements for the chest
     * @param items The inventory with which to fill the chest.
     */
    public void addChestLoadout(int day, int unlockTime, int expiry, @Nullable String advancements,
            ItemStack[] items) {
        while (loadouts.size() <= day) {
            loadouts.add(null);
        }
        loadouts.set(day, new LockedChestLoadout(unlockTime, expiry, advancements, items));
        if (activeLoadout == null || day <= activeDay) {
            activeLoadout = loadouts.get(day);
            activeDay = day;
            setState(ChestState.LOCKED);
        }
    }

    /**
     * Removes the chest loadout set at the given day
     * @param day The day
     * @return Whether there was a loadout on that day.
     */
    public boolean removeLoadout(int day) {
        LockedChestLoadout rmLoadout = getLoadout(day);
        if (rmLoadout == null) {
            return false;
        }
        loadouts.set(day, null);
        while (!loadouts.isEmpty() && loadouts.get(loadouts.size() - 1) == null) {
            loadouts.remove(loadouts.size() - 1);
        }
        if (!activeLoadout.equals(rmLoadout)) {
            return true;
        }
        rmLoadout = getLoadout(++day);
        while (day < loadouts.size() && getLoadout(day) == null) {
            day += 1;
        }
        if (rmLoadout != null) {
            activeLoadout = rmLoadout;
            activeDay = day;
            setState(ChestState.LOCKED);
        } else {
            activeLoadout = null;
            activeDay = -1;
            setState(ChestState.DONE);
        }
        return true;
    }

    /**
     * Get the chest loadout on a given day.
     *
     * @param day Day index.
     * @return {@link LockedChestLoadout} for the day. Null if no loadout on that day.
     */
    @Nullable
    public LockedChestLoadout getLoadout(int day) {
        return day < loadouts.size() && day >= 0 ? loadouts.get(day) : null;
    }

    /**
     * Expire the current loadout, and set next active loadout, or set chest to done if none left.
     */
    public void expireLoadout() {
        int newDay = Fk.getInstance().getGame().getDay() + 1;
        LockedChestLoadout newLoadout = getLoadout(newDay);
        while (newLoadout == null && newDay + 1 < loadouts.size())
            newLoadout = getLoadout(++newDay);
        if (newLoadout == null) {
            setState(ChestState.DONE);
            return;
        }
        activeLoadout = newLoadout;
        activeDay = newDay;
        setState(ChestState.LOCKED);
    }

    /**
     * Update the chest's active loadout for the next days.
     */
    public void updateActiveLoadout() {
        int newDay = Fk.getInstance().getGame().getDay();
        LockedChestLoadout newLoadout = getLoadout(newDay);
        if (newLoadout != null) {
            activeLoadout = newLoadout;
            activeDay = newDay;
            setState(ChestState.LOCKED);
        }
    }

    public int getUnlockDay() {
        return state == ChestState.DONE ? -1 : activeDay;
    }

    public LockedChestLoadout getUnlockLoadout() {
        return activeLoadout;
    }

    public Advancement getRequiredAdvancement() {
        if (state == ChestState.DONE || activeLoadout.getAdvancement() == null
                || activeLoadout.getAdvancement().isEmpty()) {
            return null;
        }
        return Bukkit.getAdvancement(parseKey(activeLoadout.getAdvancement()));
    }

    public boolean hasAccess(Player player) {
        if (state == ChestState.DONE) {
            return false;
        }
        if (activeLoadout.getAdvancement() == null || activeLoadout.getAdvancement().isEmpty()) {
            return true;
        }
        return XAdvancement.hasAdvancement(player, activeLoadout.getAdvancement());
    }

    private void reset() {
        for (int i = 0; i < loadouts.size(); i++) {
            if (loadouts.get(i) != null) {
                activeLoadout = loadouts.get(i);
                activeDay = i;
                return;
            }
        }
        setState(ChestState.LOCKED);
    }

    @Override
    public void save(ConfigurationSection config) {
        config.set("Name", name);
        config.set("Loc.World", loc.getWorld().getName());
        config.set("Loc.x", loc.getBlockX());
        config.set("Loc.y", loc.getBlockY());
        config.set("Loc.z", loc.getBlockZ());

        config.set("State", state.name());
        ConfigurationSection loadoutsCfg = config.createSection("Loadouts");
        for (Integer i = 0; i < loadouts.size(); i++) {
            if (loadouts.get(i) == null) {
                continue;
            }
            loadouts.get(i).save(loadoutsCfg.createSection(i.toString()));
        }
    }

    @Override
    public void load(ConfigurationSection config) {
        name = config.getString("Name");
        loc = new Location(Bukkit.getWorld(config.getString("Loc.World")), config.getInt("Loc.x"),
                config.getInt("Loc.y"), config.getInt("Loc.z"));
        state = ChestState.valueOf(config.getString("State"));

        // Get loadouts
        if (!config.isConfigurationSection("Loadouts")) {
            return;
        }
        ConfigurationSection loadoutCfg = config.getConfigurationSection("Loadouts");
        List<Integer> days =
                loadoutCfg.getKeys(false).stream().filter(key -> StringUtils.isNumeric(key))
                        .map(key -> Integer.parseInt(key)).collect(Collectors.toList());
        loadouts = new ArrayList<LockedChestLoadout>();
        for (Integer day : days) {
            while (loadouts.size() <= day) {
                loadouts.add(null);
            }
            loadouts.set(day,
                    LockedChestLoadout.from(loadoutCfg.getConfigurationSection(day.toString())));
        }
        reset();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: " + name);
        sb.append(",State: " + state);
        if (state == ChestState.UNLOCKING) {
            sb.append(",Unlocker: " + unlocker);
            sb.append(",Unlocking start time: " + startUnlocking);
        }
        sb.append(",Location: (" + loc.getWorld() + "," + loc.getBlockX() + "," + loc.getBlockY()
                + "," + loc.getBlockZ() + ")");
        sb.append(",Loadouts: [");
        for (Integer i = 0; i < loadouts.size(); i++) {
            if (loadouts.get(i) == null) {
                continue;
            }
            sb.append("Day " + (i) + ":{" + loadouts.get(i).toString() + "}");
        }
        sb.append("],Last interaction: " + lastInteract);
        return sb.toString();
    }
}
