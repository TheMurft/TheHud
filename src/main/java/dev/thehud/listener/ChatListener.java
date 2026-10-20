package dev.thehud.listener;

import dev.thehud.model.GroupStyle;
import dev.thehud.service.DisplayService;
import dev.thehud.service.TextService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ChatListener implements Listener {
    private final JavaPlugin plugin; private final DisplayService displays; private final TextService text;
    public ChatListener(JavaPlugin plugin, DisplayService displays, TextService text) { this.plugin = plugin; this.displays = displays; this.text = text; }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!plugin.getConfig().getBoolean("chat.enabled", true)) return;
        GroupStyle group = displays.cachedGroup(event.getPlayer()); if (group == null) return;
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put("prefix", text.render(group.prefix(), new LinkedHashMap<String, String>())); values.put("suffix", text.render(group.suffix(), new LinkedHashMap<String, String>()));
        values.put("name", event.getPlayer().getName()); values.put("message", "%2$s");
        String format = text.render(plugin.getConfig().getString("chat.format", "<prefix><name><suffix> » <message>"), values);
        event.setFormat(format.replace("%", "%%").replace("%%2$s", "%2$s"));
    }
}
