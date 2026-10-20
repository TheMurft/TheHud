package dev.thehud.service;

import dev.thehud.model.GroupStyle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DisplayService {
    private static final String TEAM_PREFIX = "th_";
    private final JavaPlugin plugin; private final GroupService groups; private final PlaceholderService placeholders; private final TextService text;
    private final Map<UUID, GroupStyle> cache = new ConcurrentHashMap<UUID, GroupStyle>();
    public DisplayService(JavaPlugin plugin, GroupService groups, PlaceholderService placeholders, TextService text) { this.plugin = plugin; this.groups = groups; this.placeholders = placeholders; this.text = text; }
    public void refreshAll() { for (Player player : Bukkit.getOnlinePlayers()) refresh(player); }
    public void refresh(Player player) {
        GroupStyle group = groups.resolve(player); cache.put(player.getUniqueId(), group);
        Map<String, String> values = new HashMap<String, String>(placeholders.forPlayer(player));
        values.put("prefix", text.render(group.prefix(), values)); values.put("suffix", text.render(group.suffix(), values));
        if (plugin.getConfig().getBoolean("tablist.enabled", true)) {
            String name = text.render(plugin.getConfig().getString("tablist.player-format", "<prefix><name><suffix>"), values);
            player.setPlayerListName(limit(name, 64));
            setHeaderFooter(player, text.join(plugin.getConfig().getStringList("tablist.header"), values), text.join(plugin.getConfig().getStringList("tablist.footer"), values));
        } else player.setPlayerListName(null);
        updateTeam(player, group, values);
    }
    public GroupStyle cachedGroup(Player player) { return cache.get(player.getUniqueId()); }
    public void remove(Player player) { cache.remove(player.getUniqueId()); }
    public void clearTeams() { Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard(); for (Team team : board.getTeams()) if (team.getName().startsWith(TEAM_PREFIX)) team.unregister(); }
    private void updateTeam(Player player, GroupStyle group, Map<String, String> values) {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Team team : board.getTeams()) if (team.getName().startsWith(TEAM_PREFIX) && team.hasEntry(player.getName())) team.removeEntry(player.getName());
        if (!plugin.getConfig().getBoolean("nametag.enabled", true)) return;
        String hash = Integer.toString(group.id().hashCode(), 36).replace("-", "0");
        String teamName = limit(TEAM_PREFIX + String.format("%04d", Math.max(0, Math.min(9999, group.weight()))) + hash, 16);
        Team team = board.getTeam(teamName); if (team == null) team = board.registerNewTeam(teamName);
        team.setPrefix(limit(text.render(group.prefix(), values), 16)); team.setSuffix(limit(text.render(group.suffix(), values), 16));
        team.setCanSeeFriendlyInvisibles(plugin.getConfig().getBoolean("nametag.show-friendly-invisibles", false)); team.addEntry(player.getName());
    }
    private void setHeaderFooter(Player player, String header, String footer) {
        try { Method method = player.getClass().getMethod("setPlayerListHeaderFooter", String.class, String.class); method.invoke(player, header, footer); }
        catch (Exception legacyMissing) {
            try {
                Class<?> component = Class.forName("net.kyori.adventure.text.Component");
                Method textMethod = component.getMethod("text", String.class);
                Method sendMethod = player.getClass().getMethod("sendPlayerListHeaderAndFooter", component, component);
                sendMethod.invoke(player, textMethod.invoke(null, header), textMethod.invoke(null, footer));
            } catch (Exception ignored) { }
        }
    }
    private String limit(String value, int max) { return value.length() <= max ? value : value.substring(0, max); }
}
