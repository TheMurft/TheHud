package dev.thehud.service;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ConfigurationFiles {
    private final JavaPlugin plugin;
    private FileConfiguration motd;
    private FileConfiguration groups;
    private FileConfiguration bossbar;

    public ConfigurationFiles(JavaPlugin plugin) {
        this.plugin = plugin;
        ensureExists("motd.yml");
        ensureExists("groups.yml");
        ensureExists("bossbar.yml");
        reload();
    }

    public void reload() {
        motd = load("motd.yml");
        groups = load("groups.yml");
        bossbar = load("bossbar.yml");
    }

    public FileConfiguration motd() {
        return motd;
    }

    public FileConfiguration groups() {
        return groups;
    }

    public FileConfiguration bossbar() {
        return bossbar;
    }

    private void ensureExists(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) plugin.saveResource(name, false);
    }

    private FileConfiguration load(String name) {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), name));
    }
}
