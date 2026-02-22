# Pillar Peril

**Pillar Peril** in an **open-source Minecraft minigame** where players **spawn on bedrock pillars**, get **random items** every few seconds, and fight to be the last one standing.

**Highly configurable** modes and **lightweight performance** make it perfect for small and medium servers.

## Special about this Version

The special part about this plugin is the high *customizability* and *a bunch of different modes*!    
Here are the different modes that are available, with each of them being fully customizable:

| Name           | Cooldown   | Time | Pillars  | Description                                  |
|----------------|------------|------|----------|----------------------------------------------|
| Blocky         | 10s        | 4m   | Circular | Starts with a weapon; only blocks are given. |
| Chaos          | 3s         | 8m   | Random   | No filters, randomized pillars, fast item cooldown.                            |
| Classic        | 5s         | 5m   | Circular | Classic rules, inspired by CubeCraft.        |
| Item-Only      | 10s        | 10m  | Circular | Only items (no blocks): long & tough.        |
| Item-Shuffle   | 10s        | 5m   | Circular | Items replaced every 10s (9 new items).      |
| Original       | 5s         | 5m   | Circular | Original gameplay as seen in early videos.   |
| Player-Shuffle | 10s        | 5m   | Circular | All players randomly swapped every 10s.      |

## Inspiration

This plugin is inspired by pillar-style minigames featured by creators like [CheapPickle](https://youtube.com/@CheapPickle) and large servers such as [CubeCraft](https://www.cubecraft.net/). This plugin is independent, open-source, and not affiliated with those projects.

## Commands

### Game Management

- **Start** using `/game start <mode> <center> <world> <players>`
- **Stop** using `/game stop <id>`
- Get **info** using `/game info <id>`
- **List** games using `/game list`.

> Use `/game list raw` to get a **raw list** which uses `2yiKLf2h1X6CenH1;h1enf2H12yiX6CKL;I8jos1lsvkh57wjs` format or, just `empty`.

### Queue

- **Join/leave** using `/queue join/leave`
- **Do admin stuff** using `/queue admin <operation>`

### Configuration

- **Get** a value using `/pp-config modify <path> get`
- **Set** a value using `/pp-config modify <path> set <value>`
- **Modify** a list using `/pp-config modify <path> add/remove <value>`
- **Reload** the config using `/pp-config reload`

## Configuration

See **Commands** section above for the `/pp-config` command to modify the configuration in-game.

The configuration is designed to be simple and ships with comments, which explain themselves.

By default, the queue is disabled, so it needs to manually be enabled.  
There are two queue modes: `command` (players use /queue) and `auto` (players are automatically queued).

## Translations

Translations are auto-downloaded from our translation server; if the server is not reachable, the plugin falls back to English. (No sensitive data is sent.)

## Releases & Contact

You can find our official releases on these platforms:

**Releases:** [Modrinth](https://modrinth.com/plugin/pillarperil) — [GitHub](https://github.com/MarcPG1905/PillarPeril/releases) — [Hangar](https://hangar.papermc.io/MarcPG1905/PillarPeril)  
**Legacy (outdated):** [SpigotMC](https://www.spigotmc.org/resources/pillar-peril.119457) — [CurseForge](https://www.curseforge.com/minecraft/bukkit-plugins/pillarperil)

**Contact:** Join our community on [Discord](https://discord.gg/HvWhqY3kRG), message me on Discord (`@marcpg1905`) or email me at [marcpg@proton.me](mailto:marcpg@proton.me).
