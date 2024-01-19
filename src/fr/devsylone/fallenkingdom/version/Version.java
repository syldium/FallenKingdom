package fr.devsylone.fallenkingdom.version;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import org.bukkit.Bukkit;

public class Version {

    public static final VersionType VERSION_TYPE;

    static {
        switch (getVersionShortstring(Bukkit.getBukkitVersion())) {
            case "1.9": 
            case "1.10":
            case "1.11":
            case "1.12":
                VERSION_TYPE = VersionType.V1_9_V1_12;
                break;
            case "1.13":
                VERSION_TYPE = VersionType.V1_13;
                break;
            case "1.14":
            case "1.15": 
                VERSION_TYPE = VersionType.V1_14_V1_15;
                break;
            case "1.16":
                VERSION_TYPE = VersionType.V1_16;
                break;
            case "1.17":
            case "1.18":
                VERSION_TYPE = VersionType.V1_17;
            break;
            case "1.19":
                VERSION_TYPE = VersionType.V1_19;
                break;
            case "1.20":
                VERSION_TYPE = VersionType.V1_20;
                break;
            default:
            case "1.8":
                VERSION_TYPE = VersionType.V1_8;
        };
    }

    private static String getVersionShortstring(String bukkitVersion) {
        String res = bukkitVersion.split("-")[0].split("\\.[0-9]$")[0];
        return res;
    }

    public static boolean hasSpigotApi() {
        return classExists("org.spigotmc.SpigotConfig");
    }

    public static boolean hasPaperApi() {
        return classExists("com.destroystokyo.paper.PaperConfig");
    }

    public static boolean isTooOldApi() {
        return !NMSUtils.nmsOptionalClass("IScoreboardCriteria$EnumScoreboardHealthDisplay").isPresent() && !VersionType.V1_9_V1_12.isHigherOrEqual();
    }

    public static boolean isBrigadierSupported() {
        return classExists("com.mojang.brigadier.CommandDispatcher");
    }

    public static boolean isAsyncTabCompleteSupported() {
        return classExists("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent");
    }

    public static boolean isAsyncPlayerSendCommandsEventSupported() {
        return classExists("com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent");
    }

    public static boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException notFound) {
            return false;
        }
    }

    public enum VersionType {
        V1_8,
        V1_9_V1_12,
        V1_13,
        V1_14_V1_15,
        V1_16,
        V1_17,
        V1_19,
        V1_20;

        public boolean isHigherOrEqual() {
            return VERSION_TYPE.ordinal() >= ordinal();
        }

        @Override
        public String toString() {
            switch (this) {
                case V1_8:
                    return "1.8.3";
                case V1_9_V1_12:
                    return "1.9.4";
                case V1_13:
                    return "1.13.2";
                case V1_14_V1_15:
                    return "1.14.4";
                case V1_16:
                    return "1.16.5";
                case V1_17:
                    return "1.17.1";
                case V1_19:
                    return "1.19.4";
                case V1_20:
                    return "1.20.1";
                default:
                    return "Unknown";
            }
        }
    }
}
