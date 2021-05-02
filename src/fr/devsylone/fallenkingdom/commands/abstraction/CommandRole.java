package fr.devsylone.fallenkingdom.commands.abstraction;

public enum CommandRole
{
    PLAYER("fallenkingdom.player"),
    ADMIN("fallenkingdom.admin");

    private final String permission;

    CommandRole(String permission) {
        this.permission = permission;
    }

    public String get() {
        return permission;
    }
}
