# TheHud 🎨

<div align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.8--1.21.x-62B47A?style=for-the-badge&logo=minecraft&logoColor=white" alt="Minecraft 1.8 to 1.21.x">
  <img src="https://img.shields.io/badge/Java-8%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 8 and newer">
  <img src="https://img.shields.io/badge/Platform-Spigot%20%7C%20Paper-5865F2?style=for-the-badge" alt="Spigot and Paper">
  <img src="https://img.shields.io/badge/Developer-TheMurft-F1C40F?style=for-the-badge" alt="Developer: TheMurft">
</div>

---

TheHud is a single-JAR server presentation plugin for Spigot and Paper. It keeps the tab list, nametags, chat, MOTD, and boss bar consistent across legacy and modern Minecraft versions.

## ✨ Features

- 📋 **Tab list:** Custom header, footer, player format, sorting, and live ping/TPS placeholders.
- 🏷️ **Groups and nametags:** Permission-based prefixes and suffixes shared across the player list, nametags, and chat.
- 💬 **Chat formatting:** Apply the same rank identity to player messages.
- 🌈 **Legacy-aware colors:** RGB and gradients on supported versions, with classic-color fallback on older servers.
- 📢 **Server MOTD:** Two configurable lines with live player count and performance placeholders.
- 🎨 **Boss bar:** Configurable colors, styles, progress modes, and refresh interval.
- 🖼️ **Server icon:** Bundled 64×64 icon, extracted to the plugin folder for customization.
- 🧩 **Built-in placeholders:** Player, world, location, health, time, date, ping, TPS, and other server details.

## 🚀 Quick start

1. Build with `mvn clean package`, or use the released `TheHud-1.2.0.jar`.
2. Put the JAR in the server's `plugins/` folder.
3. Restart the server, edit the generated files in `plugins/TheHud/`, and run `/thehud reload`.

### Requirements

- Spigot or Paper, Minecraft 1.8–1.21.x
- Java 8 or newer; use the Java version required by your server (Java 21 for current server versions)
- Maven to build from source

## 🎮 Commands

| Command | Description |
| :--- | :--- |
| `/thehud reload` | Reload configuration files. |
| `/thehud refresh` | Refresh displayed information. |
| `/thehud status` | Show plugin and compatibility status. |
| `/thehud placeholders` | List built-in placeholders. |

These commands require `thehud.admin` by default.

## ⚙️ Configuration

- `config.yml` — General settings, tab list, chat, nametags, and refresh interval.
- `groups.yml` — Permission groups, prefixes, suffixes, and sorting weights.
- `motd.yml` — Server-list MOTD and displayed capacity.
- `bossbar.yml` — Boss bar title, color, style, and progress mode.

Avoid enabling another plugin's scoreboard-team or chat formatter for the same feature; they may compete for the same display.

## 🧰 Build from source

```bash
mvn clean package
```

The plugin JAR is created in `target/`.

## 🧩 Compatibility notes

The source targets Java 8 bytecode. Boss bars are native on Minecraft 1.9 and newer; only that feature is disabled on 1.8. Legacy servers use classic colors, while modern servers keep the same configuration syntax.

## 💬 Support

For questions or help, join the [Discord support server](https://discord.gg/6C5t995jC6).

---

Made with ❤️ by [TheMurft](https://github.com/TheMurft).
