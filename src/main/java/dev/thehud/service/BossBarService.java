package dev.thehud.service;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BossBarService {
    private final JavaPlugin plugin; private final PlaceholderService placeholders; private final TextService text; private final ConfigurationFiles files;
    private final Map<UUID, Object> bars = new ConcurrentHashMap<UUID, Object>(); private boolean warnedLegacy;
    public BossBarService(JavaPlugin plugin, PlaceholderService placeholders, TextService text, ConfigurationFiles files) { this.plugin = plugin; this.placeholders = placeholders; this.text = text; this.files = files; }
    public void refreshAll() { if (!files.bossbar().getBoolean("enabled", true)) { hideAll(); return; } for (Player player : Bukkit.getOnlinePlayers()) refresh(player); }
    @SuppressWarnings({"unchecked", "rawtypes"}) public void refresh(Player player) {
        try {
            FileConfiguration config = files.bossbar(); String title = text.render(config.getString("title", "<blue><bold><server_name></bold></blue>"), placeholders.forPlayer(player));
            Class<?> colorType = Class.forName("org.bukkit.boss.BarColor"); Class<?> styleType = Class.forName("org.bukkit.boss.BarStyle");
            Object color = Enum.valueOf((Class<Enum>) colorType, safeEnum(config.getString("color", "BLUE"), "BLUE"));
            Object style = Enum.valueOf((Class<Enum>) styleType, bukkitStyle(config.getString("style", "NOTCHED_10")));
            Object bar = bars.get(player.getUniqueId());
            if (bar == null) { Class<?> flagType = Class.forName("org.bukkit.boss.BarFlag"); Object flags = java.lang.reflect.Array.newInstance(flagType, 0); Method create = Bukkit.class.getMethod("createBossBar", String.class, colorType, styleType, flags.getClass()); bar = create.invoke(null, title, color, style, flags); bars.put(player.getUniqueId(), bar); bar.getClass().getMethod("addPlayer", Player.class).invoke(bar, player); }
            bar.getClass().getMethod("setTitle", String.class).invoke(bar, title); bar.getClass().getMethod("setProgress", double.class).invoke(bar, progress(player, config));
            bar.getClass().getMethod("setColor", colorType).invoke(bar, color); bar.getClass().getMethod("setStyle", styleType).invoke(bar, style);
        } catch (ClassNotFoundException legacy) { if (!warnedLegacy) { warnedLegacy = true; plugin.getLogger().warning("Native boss bars require Minecraft 1.9+. Boss bar disabled on this legacy server."); } }
        catch (Exception exception) { plugin.getLogger().warning("Could not update boss bar: " + exception.getMessage()); }
    }
    public void remove(Player player) { Object bar = bars.remove(player.getUniqueId()); if (bar != null) try { bar.getClass().getMethod("removePlayer", Player.class).invoke(bar, player); } catch (Exception ignored) { } }
    public void hideAll() { for (Object bar : bars.values()) try { bar.getClass().getMethod("removeAll").invoke(bar); } catch (Exception ignored) { } bars.clear(); }
    private double progress(Player player, FileConfiguration config) { String mode = config.getString("progress.mode", "STATIC").toUpperCase(Locale.ROOT); double value; if ("HEALTH".equals(mode)) value = player.getMaxHealth() <= 0 ? 0 : player.getHealth() / player.getMaxHealth(); else if ("TPS".equals(mode)) value = placeholders.tps() / 20.0; else if ("PING".equals(mode)) value = 1.0 - placeholders.ping(player) / Math.max(1.0, config.getDouble("progress.max-ping", 300)); else value = config.getDouble("progress.value", 1); return Math.max(0, Math.min(1, value)); }
    private String safeEnum(String value, String fallback) { String result = value == null ? fallback : value.toUpperCase(Locale.ROOT); return result.matches("[A-Z0-9_]+") ? result : fallback; }
    private String bukkitStyle(String value) {
        String style = safeEnum(value, "NOTCHED_10");
        if (style.startsWith("NOTCHED_")) return "SEGMENTED_" + style.substring("NOTCHED_".length());
        return style;
    }
}
