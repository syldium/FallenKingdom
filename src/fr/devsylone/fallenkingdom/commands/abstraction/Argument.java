package fr.devsylone.fallenkingdom.commands.abstraction;

import com.google.common.collect.ImmutableList;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.XAdvancement;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fkpi.util.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Représente un argument de commande du plugin.
 *
 * Le nom de l'argument permet la complétion avec la touche tabulation de Bukkit.
 * Les informations à propos du type d'argument ne serviront qu'à la traduction en nœuds Brigadier.
 */
public class Argument<T>
{
    private final String name;
    private final boolean required;
    private final String description;
    private final Class<T> typeClazz;

    public static Argument<String> create(String name, boolean required) {
        return Argument.create(name, required, "");
    }

    public static Argument<String> create(String name, boolean required, String description) {
        return new Argument<>(name, required, description, String.class);
    }

    /**
     * Compile les arguments selon l'usage.
     *
     * Les arguments sont séparés par des espaces, et sont entourés de chevrons ou crochets
     * selon s'ils sont obligatoires ou non. Le nom de l'argument peut être précédé de son type
     * en suivant le format t:nom où t correspond au nom du type représenté par #getClassByToken().
     *
     * @param usage Usage des arguments
     * @return Arguments trouvés
     */
    public static ImmutableList<Argument<?>> create(String usage) {
        String[] args = usage.split("([>\\]]) ?");
        List<Argument<?>> created = new ArrayList<>();
        for (String arg : args) {
            String name = arg.substring(1);
            String type = "s";
            Class<?> clazz = String.class;
            if (name.contains(":")) { // Un type d'argument est précisé
                type = name.substring(0, name.indexOf(":"));
                clazz = getClassByToken(type);
                name = name.substring(name.indexOf(":") + 1);
            }
            boolean required = arg.startsWith("<");
            if (clazz.equals(int.class)) { // Si on attend un entier, il y a peut être d'autres informations à récupérer
                int sep = type.indexOf(";") > 0 ? type.indexOf(";") : type.length();
                int min = type.length() > 1 ? Integer.parseInt(type.substring(1, sep)) : 0;
                int max = type.length() > 1 && type.indexOf(";") > 0 ? Integer.parseInt(type.substring(sep + 1)) : Integer.MAX_VALUE;
                created.add(new IntegerArgument(name, required,"", min, max));
            } else {
                created.add(new Argument<>(name, required,"", clazz));
            }
        }
        return ImmutableList.copyOf(created);
    }

    public static ImmutableList<Argument<?>> list(Argument<?>... args) {
        return ImmutableList.copyOf(args);
    }

    public Argument(String name, boolean required, String description, Class<T> typeClazz) {
        this.name = name;
        this.required = required;
        this.description = description;
        this.typeClazz = typeClazz;
    }

    public String getName() {
        return this.name;
    }

    public String getForUsage() {
        if (isRequired()) {
            return "<" + name + ">";
        }
        return "[" + name + "]";
    }

    public boolean isRequired() {
        return this.required;
    }

    public String getDescription() {
        return this.description;
    }

    private static Class<?> getClassByToken(String token) {
        switch (token.charAt(0)) {
            case 'b':
                return boolean.class;
            case 'i':
                return int.class;
            default:
                return String.class;
        }
    }

    public boolean shouldBrigadierAskServer() {
        return typeClazz == String.class && !name.equals("name") && !name.equals("block") && !name.equals("player") && !name.equals("entity");
    }

    public List<String> provideTabComplete(Fk plugin, String typed) {
        switch (name) {
            case "team":
                return plugin.getFkPI().getTeamManager().getTeams().stream()
                        .map(Team::getName)
                        .filter(s -> startsWith(s, typed))
                        .collect(Collectors.toList());
            case "player":
                return plugin.getServer().getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(s -> startsWith(s, typed))
                        .collect(Collectors.toList());
            case "block":
                return Arrays.stream(Material.values())
                        .filter(Material::isBlock)
                        .filter(team -> startsWith(team.name(), typed))
                        .map(m -> m.name().toLowerCase())
                        .collect(Collectors.toList());
            case "color":
            case "newteam":
                return Arrays.stream(Color.values())
                        .filter(color -> startsWith(color.getGenredName(1), typed))
                        .filter(color -> !color.getGenredName(1).contains(" "))
                        .map(color -> color.getGenredName(1))
                        .collect(Collectors.toList());
            case "advancement":
                List<String> suggestions = new ArrayList<>();
                Iterator<String> iterator = XAdvancement.iterator();
                String key = typed;
                if (XAdvancement.isAdvancement()) {
                    key = typed.contains(":") ? typed : "minecraft:" + typed;
                }
                while (iterator.hasNext()) {
                    String advancement = iterator.next();
                    if (startsWith(advancement, key)) {
                        suggestions.add(advancement);
                    }
                }
                return suggestions;
            case "chest":
                return plugin.getFkPI().getLockedChestsManager().getChestList().stream()
                        .map(LockedChest::getName)
                        .filter(s -> startsWith(s, typed))
                        .collect(Collectors.toList());
            default:
                if (name.contains("|")) {
                    return Arrays.stream(name.split("\\|"))
                            .filter(n -> startsWith(n, typed))
                            .collect(Collectors.toList());
                }
                return Collections.emptyList();
        }
    }

    private boolean startsWith(String word, String prefix) {
        return word.toLowerCase().startsWith(prefix.toLowerCase());
    }

    public Class<T> getType() {
        return typeClazz;
    }
}
