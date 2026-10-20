package dev.thehud.model;

public final class GroupStyle {
    private final String id;
    private final String permission;
    private final int weight;
    private final String prefix;
    private final String suffix;

    public GroupStyle(String id, String permission, int weight, String prefix, String suffix) {
        this.id = id;
        this.permission = permission;
        this.weight = weight;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String id() { return id; }
    public String permission() { return permission; }
    public int weight() { return weight; }
    public String prefix() { return prefix; }
    public String suffix() { return suffix; }
}
