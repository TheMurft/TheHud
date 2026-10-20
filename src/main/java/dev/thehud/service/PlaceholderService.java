package dev.thehud.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class PlaceholderService {
    public static final String AVAILABLE = "<player>, <name>, <display_name>, <uuid>, <ping>, <ping_color>, <online>, <max_players>, <world>, <x>, <y>, <z>, <health>, <max_health>, <level>, <xp>, <gamemode>, <tps>, <tps_color>, <mspt>, <server_name>, <time>, <date>";
    private final JavaPlugin plugin;

    public PlaceholderService(JavaPlugin plugin) { this.plugin = plugin; }

    public Map<String, String> forPlayer(Player player) {
        double tps = tps();
        int ping = ping(player);
        Map<String, String> values = common();
        values.put("player", player.getName()); values.put("name", player.getName());
        values.put("display_name", player.getDisplayName()); values.put("uuid", player.getUniqueId().toString());
        values.put("ping", Integer.toString(ping)); values.put("ping_color", ping <= 75 ? "§a" : ping <= 150 ? "§e" : "§c");
        values.put("world", player.getWorld().getName()); values.put("x", Integer.toString(player.getLocation().getBlockX()));
        values.put("y", Integer.toString(player.getLocation().getBlockY())); values.put("z", Integer.toString(player.getLocation().getBlockZ()));
        values.put("health", decimal(player.getHealth())); values.put("max_health", decimal(player.getMaxHealth()));
        values.put("level", Integer.toString(player.getLevel())); values.put("xp", Integer.toString(player.getTotalExperience()));
        values.put("gamemode", player.getGameMode().name().toLowerCase(Locale.ROOT));
        values.put("tps", decimal(tps)); values.put("tps_color", tps >= 18 ? "§a" : tps >= 15 ? "§e" : "§c");
        return values;
    }

    public Map<String, String> common() {
        Map<String, String> values = new LinkedHashMap<String, String>();
        double tps = tps(); Date now = new Date();
        values.put("online", Integer.toString(Bukkit.getOnlinePlayers().size())); values.put("max_players", Integer.toString(Bukkit.getMaxPlayers()));
        values.put("tps", decimal(tps)); values.put("tps_color", tps >= 18 ? "§a" : tps >= 15 ? "§e" : "§c");
        values.put("mspt", decimal(mspt())); values.put("server_name", plugin.getConfig().getString("settings.server-name", "Minecraft Server"));
        values.put("time", new SimpleDateFormat("HH:mm").format(now)); values.put("date", new SimpleDateFormat("yyyy-MM-dd").format(now));
        return values;
    }

    public int ping(Player player) {
        try { Method method = player.getClass().getMethod("getPing"); return ((Number) method.invoke(player)).intValue(); }
        catch (Exception ignored) {
            try { Method handle = player.getClass().getMethod("getHandle"); Object entity = handle.invoke(player); Field field = entity.getClass().getField("ping"); return field.getInt(entity); }
            catch (Exception ignoredAgain) { return 0; }
        }
    }

    public double tps() {
        try { Method method = Bukkit.class.getMethod("getTPS"); return Math.min(20.0, ((double[]) method.invoke(null))[0]); }
        catch (Exception ignored) { return 20.0; }
    }

    private double mspt() {
        try { Method method = Bukkit.class.getMethod("getAverageTickTime"); return ((Number) method.invoke(null)).doubleValue(); }
        catch (Exception ignored) { return 50.0; }
    }

    private String decimal(double value) { return String.format(Locale.US, "%.1f", value); }
}
