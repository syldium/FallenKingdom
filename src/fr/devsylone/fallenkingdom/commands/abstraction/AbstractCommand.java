package fr.devsylone.fallenkingdom.commands.abstraction;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.FkSound;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Décrit une commande, peu importe son niveau
 */
public abstract class AbstractCommand
{
    protected final String name;
    protected final Messages description;
    protected final CommandRole role;
    protected String permission;

    protected @Nullable FkParentCommand parent;

    AbstractCommand(String name, Messages description, CommandRole role) {
        this.name = name;
        this.description = description;
        this.role = role;
        this.permission = "fallenkingdom.commands." + name;
    }

    public abstract CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) throws FkLightException, IllegalArgumentException;

    public abstract List<String> tabComplete(Fk plugin, CommandSender sender, List<String> args);

    public String getName() {
        return name;
    }

    public String getUsage() {
        return getName();
    }

    public String getFullUsage() {
        if (parent != null) {
            return parent.getFullUsage() + " " + getUsage();
        }
        return getUsage();
    }

    public String getDescription() {
        if (description == null) {
            return "";
        }
        return description.getMessage();
    }

    /**
     * Teste les permissions de l'exécuteur pour la commande actuelle
     * @param sender Envoyeur
     * @return Si l'exécution peut continuer
     */
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(permission);
    }

    /**
     * Vérifie si la commande peut être exécutée avec l'envoyeur donné
     * @param sender Envoyeur
     * @return Si la commande peut utiliser cet envoyeur
     */
    public boolean isValidExecutor(CommandSender sender) {
        return true;
    }

    abstract public int getMinArgumentCount();

    /**
     * Cherche la commande appropriée pour l'exécution
     * @param args Liste des arguments connus
     * @return Commande à exécuter
     */
    abstract public AbstractCommand get(List<String> args);

    /**
     * Cherche l'instance de la commande parmi celles enregistrées
     * @param cmd Liste des arguments connus
     * @return Commande trouvée
     */
    abstract public AbstractCommand get(Class<? extends AbstractCommand> cmd);

    /**
     * Récupère la commande parente. Peut être null
     * @return Commande parente ou null
     */
    public @Nullable FkParentCommand getParent() {
        return parent;
    }

    public @NotNull CommandRole getRole() {
        return role;
    }

    public @NotNull String getPermission() {
        return permission;
    }

    public @NotNull Map<String, CommandRole> getPermissions() {
        return Collections.singletonMap(this.permission, this.role);
    }

    public void setParent(FkParentCommand parent) {
        this.parent = parent;
        this.permission = parent.permission + '.' + name;
    }

    protected void broadcast(String message) {
        if (getParent() == null) {
            Fk.broadcast(message);
            return;
        }
        getParent().broadcast(message);
        Bukkit.getConsoleSender().sendMessage(Messages.PREFIX_FK.getMessage() + message);
    }

    protected void broadcast(String message, int noBroadcastPos, List<String> args) {
        if (args.size() > noBroadcastPos && args.get(noBroadcastPos).equalsIgnoreCase("nobroadcast")) {
            return;
        }
        broadcast(message);
    }

    protected void broadcast(String message, FkSound sound) {
        broadcast(message);
        for (Player player : Fk.getInstance().getPlayerManager().getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound.key(), 1.0f, 1.0f);
        }
    }

    protected void broadcastState(boolean state, Messages base, Messages trueMsg, Messages falseMsg) {
        Messages value = state ? trueMsg : falseMsg;
        broadcast(base.getMessage().replace("%state%", value.getMessage()));
    }

    public boolean shouldDisplay() {
        return true;
    }
}
