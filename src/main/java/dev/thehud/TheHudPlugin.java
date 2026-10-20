package dev.thehud;

import dev.thehud.listener.ChatListener;
import dev.thehud.listener.PlayerListener;
import dev.thehud.service.BossBarService;
import dev.thehud.service.ConfigurationFiles;
import dev.thehud.service.DisplayService;
import dev.thehud.service.GroupService;
import dev.thehud.service.MotdService;
import dev.thehud.service.PlaceholderService;
import dev.thehud.service.TextService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TheHudPlugin extends JavaPlugin {
    private GroupService groups; private DisplayService displays; private MotdService motd; private ConfigurationFiles files; private BossBarService bossBars; private BukkitTask refreshTask; private TextService text;
    public void onEnable() {
        saveDefaultConfig(); getConfig().options().copyDefaults(true); migrateLegacyConfiguration(); saveConfig();
        text = new TextService(this); files = new ConfigurationFiles(this); groups = new GroupService(this, files); PlaceholderService placeholders = new PlaceholderService(this);
        displays = new DisplayService(this, groups, placeholders, text); bossBars = new BossBarService(this, placeholders, text, files); motd = new MotdService(this, text, placeholders, files); groups.reload();
        getServer().getPluginManager().registerEvents(new PlayerListener(this, displays, bossBars), this); getServer().getPluginManager().registerEvents(new ChatListener(this, displays, text), this); getServer().getPluginManager().registerEvents(motd, this);
        startRefreshTask(); displays.refreshAll(); bossBars.refreshAll(); getLogger().info("TheHud multi-version mode enabled with " + groups.size() + " groups.");
    }
    public void onDisable() { if (displays != null) displays.clearTeams(); if (bossBars != null) bossBars.hideAll(); }
    private void reloadAll() { reloadConfig(); files.reload(); groups.reload(); motd.reload(); startRefreshTask(); displays.refreshAll(); bossBars.refreshAll(); }
    private void migrateLegacyConfiguration() { if (getConfig().isConfigurationSection("motd")) getConfig().set("motd", null); if (getConfig().isConfigurationSection("groups")) getConfig().set("groups", null); if ("The SMP".equals(getConfig().getString("settings.server-name"))) getConfig().set("settings.server-name", "Your Server"); }
    private void startRefreshTask() { if (refreshTask != null) refreshTask.cancel(); long interval = Math.max(5L, getConfig().getLong("settings.refresh-interval-ticks", 20L)); refreshTask = getServer().getScheduler().runTaskTimer(this, new Runnable() { public void run() { displays.refreshAll(); bossBars.refreshAll(); } }, interval, interval); }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String action = args.length == 0 ? "status" : args[0].toLowerCase();
        if ("reload".equals(action)) { reloadAll(); message(sender, "&9&lTheHud&9 reloaded successfully."); }
        else if ("refresh".equals(action)) { displays.refreshAll(); bossBars.refreshAll(); message(sender, "&9&lTheHud&9 refreshed all player displays."); }
        else if ("status".equals(action)) message(sender, "&9&lTheHud " + getDescription().getVersion() + "&9 &8• &f" + getServer().getOnlinePlayers().size() + " players &8• &f" + groups.size() + " groups &8• &a running");
        else if ("placeholders".equals(action)) { message(sender, "&9&lTheHud placeholders"); sender.sendMessage(PlaceholderService.AVAILABLE); }
        else message(sender, "&eUsage: /thehud <reload|refresh|status|placeholders>"); return true;
    }
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { if (args.length != 1) return Collections.emptyList(); List<String> result = new ArrayList<String>(); for (String value : Arrays.asList("reload", "refresh", "status", "placeholders")) if (value.startsWith(args[0].toLowerCase())) result.add(value); return result; }
    private void message(CommandSender sender, String value) { sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', value)); }
}
