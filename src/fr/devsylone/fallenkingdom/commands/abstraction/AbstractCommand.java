package fr.devsylone.fallenkingdom.commands.abstraction;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.FkSound;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Décrit une commande, peu importe son niveau
 */
public abstract class AbstractCommand
{
    protected final String name;
    protected final Messages description;
    protected final CommandPermission permission;

    protected FkParentCommand parent;

    AbstractCommand(String name, Messages description, CommandPermission permission) {
        this.name = name;
        this.description = description;
        this.permission = permission;
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
            return parent.getUsage() + " " + getUsage();
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
        return sender.hasPermission(permission.get());
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
    public FkParentCommand getParent() {
        return parent;
    }

    public void setParent(FkParentCommand parent) {
        this.parent = parent;
    }

    protected void broadcast(String message) {
        if (getParent() == null) {
            Fk.broadcast(message);
            return;
        }
        getParent().broadcast(message);
        Bukkit.getConsoleSender().sendMessage(ChatUtils.PREFIX + message);
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
            player.playSound(player.getLocation(), sound.bukkitSound(),1.0f,1.0f);
        }
    }

    public boolean shouldDisplay() {
        return true;
    }
}
