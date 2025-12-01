# Pillar Peril

Pillar Peril in a open-source game mode where you spawn on bedrock pillars and have to win using random items that you get every few seconds. Some items may be good for building, some may be good for killing other players, and some may just be complete trash.  
This game originated from a popular YouTube channel called [CheapPickle](https://youtube.com/@CheapPickle) and was later adapted by the popular Minecraft server, [CubeCraft](https://www.cubecraft.net/), which both were the main inspiration for this plugin.

## Special about this Version

The special part about this plugin is the high *customizability* and *a bunch of different modes*!    
Here are the different modes that are available, with each of them being fully customizable:

| Name           | Cooldown   | Time Limit | Pillars  | Description                                                                       |
|----------------|------------|------------|----------|-----------------------------------------------------------------------------------|
| Blocky         | 10 Seconds | 4 Minutes  | Circular | Gives you a random weapon at the start, but you only get blocks randomly.         |
| Chaos          | 3 Seconds  | 8 Minutes  | Random   | No item filters, pillars are randomized and the item cooldown is very low.        |
| CubeCraft      | 5 Seconds  | 5 Minutes  | Circular | Game mode with the same rules and properties as CubeCraft's "Pillars of Fortune". |
| Item-Only      | 10 Seconds | 10 Minutes | Circular | You only get items and no blocks, which makes the game very long and hard.        |
| Item-Shuffle   | 10 Seconds | 5 Minutes  | Circular | Every 10 seconds, you lose your items and get 10 random new ones.                 |
| Original       | 5 Seconds  | 5 Minutes  | Circular | The original mode as seen in CheapPickle's videos.                                |
| Player-Shuffle | 10 Seconds | 5 Minutes  | Circular | Every 10 seconds, all player's positions are randomly swapped.                    |
| Soon...        | ...        | ...        | ...      | More modes are coming soon...!                                                    |

## Usage

### Configuration

The configuration is designed to be simple and descriptive.  
You can find everything you need inside the configuration file, which ships with useful descriptions.

There are special options in the configuration, which have **placeholders**. Configuration supporting placeholders will say so in their comment above.  
All possible placeholders will be listed above and you just have to have { and } surrounding the placeholder's name, like `{name}`.

> [!NOTE]
> Values with placeholders should be surrounded by quotes ("text"), as they contain special characters.

### Translations

Translations are automatically downloaded over [a simple database](https://marcpg.com/pillar-peril/lang/all), which means that you don't have to do anything to have them.

The download itself will only take a few kilobytes on each startup, and if there is no internet connection, it will just use the default English translations.

### Game Management

Everything you want to do will be available in the `/game` command.  
You probably won't even need the following guides, because everything will explain itself when typing out the command.

- **Start** a game using `/game start <mode> <center> <world> <players>`, e.g.:
```
/game start cubecraft ~ ~ ~ minecraft:overworld @a
/game start original 0 -100 20 minecraft:the_nether @a[name=!MarcPG1905]
```

- **Stop** a game using `/game stop <id>`, e.g.:
```
/game stop 2yiKLf2h1X6CenH1
```

- Get **info** about a game using `/game info <id>`, e.g.:
```
/game info 2yiKLf2h1X6CenH1
```

- **List** all running games using `/game list`. This will also show some info about each of the games (for maximum info, please use `/game info <id>` instead).
  - Use `/game list raw` to get a **raw list** which uses `2yiKLf2h1X6CenH1;h1enf2H12yiX6CKL;I8jos1lsvkh57wjs` format or, just `empty`.

### Queue System

The queue system is very flexible and has two methods, which can be changed in the configuration:

- **Command**: Players join the queue using the `/queue` command.
- **Auto**: Anyone who is not currently playing a game is automatically inside the queue.

All settings, like minimum/maximum players to start games, the game mode to start, the world and location to use, etc. can all be changed inside the configuration.

## Releases

You can find our official releases on these platforms:
- Modrinth (Recommended): [modrinth.com/plugin/pillarperil](https://modrinth.com/plugin/pillarperil)
- GitHub: [github.com/MarcPG1905/PillarPeril/releases](https://github.com/MarcPG1905/PillarPeril/releases)
- Hangar: [hangar.papermc.io/MarcPG1905/PillarPeril](https://hangar.papermc.io/MarcPG1905/PillarPeril)
- SpigotMC: [spigotmc.org/resources/pillar-peril...](https://www.spigotmc.org/resources/pillar-peril.119457)
- Planet Minecraft: [planetminecraft.com/mod/pillar-peril](https://www.planetminecraft.com/mod/pillar-peril/)
- CurseForge: [curseforge.com/minecraft/bukkit-plugins/pillarperil](https://www.curseforge.com/minecraft/bukkit-plugins/pillarperil)

## Contact

### Discord Server

You can join my Discord community and just ping me for a quick response: [MarcPG Dev Discord](https://discord.gg/HvWhqY3kRG)

### Direct Contact

If you don't use Discord or would like to contact me otherwise, please rely on one of these methods:
- E-Mail: [me@marcpg.com](mailto:me@marcpg.com)
- Discord: `@marcpg1905`
