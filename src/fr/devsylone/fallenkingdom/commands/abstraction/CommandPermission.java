package fr.devsylone.fallenkingdom.commands.abstraction;

public enum CommandPermission
{
    PLAYER("fallenkingdom.player"),
    ADMIN("fallenkingdom.admin");

    private final String permission;

    CommandPermission(String permission) {
        this.permission = permission;
    }

    public String get() {
        return permission;
    }
}
