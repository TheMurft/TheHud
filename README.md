# TheHud

TheHud is a single cross-version Spigot/Paper plugin for Minecraft 1.8 through 1.21.x. It keeps the tab list, nametags, player sorting, chat, MOTD and boss bar visually consistent.

## Features

- Polished MiniMessage header and footer.
- Live ping and TPS with automatic status colors.
- Configurable player-list format.
- Permission-based groups, prefixes, suffixes and sorting.
- Matching nametags and chat prefixes.
- Coordinates, health, world, time, date and server-stat placeholders.
- Two-line MiniMessage server MOTD with live player count and performance placeholders.
- Built-in 64x64 custom server icon, automatically extracted to the plugin folder.
- Configurable live boss bar with seven colors, five styles and dynamic progress modes.
- Configurable refresh interval.
- `/thehud reload`, `/thehud refresh`, `/thehud status`, and `/thehud placeholders`.

## Installation

1. Use the Java version required by your Minecraft server (Java 8 on legacy releases, Java 21 on current releases).
2. Place `TheHud-1.2.0.jar` in the server's `plugins` folder.
3. Restart the server.
4. Edit `config.yml`, `motd.yml`, `groups.yml`, and `bossbar.yml` inside `plugins/TheHud`, then run `/thehud reload`.

Do not enable another plugin's scoreboard-team or chat formatter at the same time unless the
overlapping feature is disabled, as both plugins would compete for the same display.

Native boss bars are available on Minecraft 1.9 and newer. On 1.8, only that feature is disabled; the rest of TheHud continues to work. Legacy servers use classic colors, while modern servers retain the same configuration syntax.
