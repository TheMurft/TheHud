package dev.thehud.service;

import dev.thehud.model.GroupStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class GroupService {
    private final JavaPlugin plugin; private final ConfigurationFiles files;
    private volatile List<GroupStyle> groups = Collections.emptyList();
    public GroupService(JavaPlugin plugin, ConfigurationFiles files) { this.plugin = plugin; this.files = files; }
    public void reload() {
        FileConfiguration config = files.groups(); ConfigurationSection section = config.getConfigurationSection("groups");
        List<GroupStyle> loaded = new ArrayList<GroupStyle>();
        if (section != null) for (String id : section.getKeys(false)) {
            String root = "groups." + id;
            loaded.add(new GroupStyle(id, config.getString(root + ".permission", ""), config.getInt(root + ".weight", 999), config.getString(root + ".prefix", ""), config.getString(root + ".suffix", "")));
        }
        Collections.sort(loaded, new Comparator<GroupStyle>() { public int compare(GroupStyle a, GroupStyle b) { return Integer.compare(a.weight(), b.weight()); } });
        groups = Collections.unmodifiableList(loaded);
    }
    public GroupStyle resolve(Player player) {
        for (GroupStyle group : groups) if (group.permission().isEmpty() || player.hasPermission(group.permission())) return group;
        return new GroupStyle("default", "", 999, "", "");
    }
    public int size() { return groups.size(); }
}
