package dev.thehud.service;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public final class MotdService implements Listener {
    private final JavaPlugin plugin; private final TextService text; private final PlaceholderService placeholders; private final ConfigurationFiles files; private volatile CachedServerIcon icon;
    public MotdService(JavaPlugin plugin, TextService text, PlaceholderService placeholders, ConfigurationFiles files) { this.plugin = plugin; this.text = text; this.placeholders = placeholders; this.files = files; reload(); }
    public void reload() { icon = null; if (!files.motd().getBoolean("use-custom-icon", true)) return; File file = new File(plugin.getDataFolder(), "server-icon.png"); if (!file.exists()) plugin.saveResource("server-icon.png", false); installRootIcon(file); try { icon = Bukkit.loadServerIcon(file); } catch (Exception e) { plugin.getLogger().warning("Could not load server icon: " + e.getMessage()); } }
    @EventHandler public void onPing(ServerListPingEvent event) { FileConfiguration config = files.motd(); if (!config.getBoolean("enabled", true)) return; Map<String, String> values = placeholders.common(); values.put("online", Integer.toString(event.getNumPlayers())); values.put("max_players", Integer.toString(event.getMaxPlayers())); event.setMotd(text.join(config.getStringList("lines"), values)); int max = config.getInt("max-players", -1); if (max >= 0) event.setMaxPlayers(max); if (icon != null) event.setServerIcon(icon); }
    private void installRootIcon(File source) {
        File destination = new File(plugin.getServer().getWorldContainer(), "server-icon.png"); if (destination.exists()) return;
        try { FileInputStream in = new FileInputStream(source); FileOutputStream out = new FileOutputStream(destination); try { byte[] buffer = new byte[8192]; int read; while ((read = in.read(buffer)) >= 0) out.write(buffer, 0, read); } finally { in.close(); out.close(); } plugin.getLogger().info("Installed server icon at " + destination.getAbsolutePath()); }
        catch (IOException exception) { plugin.getLogger().warning("Could not install root server icon: " + exception.getMessage()); }
    }
}
