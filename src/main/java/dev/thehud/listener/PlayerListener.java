package dev.thehud.listener;

import dev.thehud.service.BossBarService;
import dev.thehud.service.DisplayService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerListener implements Listener {
    private final JavaPlugin plugin; private final DisplayService displays; private final BossBarService bossBars;
    public PlayerListener(JavaPlugin plugin, DisplayService displays, BossBarService bossBars) { this.plugin = plugin; this.displays = displays; this.bossBars = bossBars; }
    @EventHandler public void onJoin(final PlayerJoinEvent event) { plugin.getServer().getScheduler().runTask(plugin, new Runnable() { public void run() { displays.refreshAll(); bossBars.refresh(event.getPlayer()); } }); }
    @EventHandler public void onQuit(final PlayerQuitEvent event) { displays.remove(event.getPlayer()); bossBars.remove(event.getPlayer()); plugin.getServer().getScheduler().runTask(plugin, new Runnable() { public void run() { displays.refreshAll(); } }); }
}
