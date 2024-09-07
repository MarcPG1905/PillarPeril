# Pillar Peril

Pillar Peril in a open-source game mode where you spawn on bedrock pillars and have to win using random items that you get every few seconds. Some items may be good for building, some may be good for killing other players, and some may just be complete trash.  
This game originated from a popular YouTube channel called [CheapPickle](https://youtube.com/@CheapPickle) and was later adapted by the popular Minecraft server, [CubeCraft](https://www.cubecraft.net/), which both were the main inspiration for this plugin.

## Special about this Version

The special part about this plugin is the high *customizability* and *huge amount of different modes*!    
Here are *some* of the many different modes that are available. Each of them being fully customizable:
Name | Cooldown | Time Limit | Pillars | Description
---|---|---|---|---
Blocky | 10 Seconds | 4 Minutes | Circular | Gives you a random weapon at the start, but you only get blocks randomly.
Chaos | 3 Seconds | 8 Minutes | Random | No item filters, pillars are randomized and the item cooldown is very low.
CubeCraft | 5 Seconds | 5 Minutes | Circular | Game mode with the same rules and properties as CubeCraft's "Pillars of Fortune".
Item-Only | 10 Seconds | 10 Minutes | Circular | You only get items and no blocks, which makes the game very long and hard.
Original | 5 Seconds | 5 Minutes | Circular | The original mode as seen in CheapPickle's videos.
Soon... | ... | ... | ... | More modes are coming soon...!

## Usage

### Configuration

The configuration is designed to be simple and descriptive. You can find everything you need inside the configuration file, which ships with useful descriptions. The functionality to customize the different game modes will be extended in the next few updates.

### Translations

Translations are automatically downloaded over [a simple database](https://marcpg.com/pillarperil/lang/), which means that you don't have to do anything except have a stable internet connection. The download itself will only take a few kilobytes on each startup.

## Game Management

Everything you want to do will be available in the `/games` command. You probably won't even need the following guides, because everything will explain itself when typing out the command.

### Start a Game

To start a game, you just have to use `/games start`, followed by the game info. These are some examples:

```
/games start force cubecraft ~ ~ ~ minecraft:overworld @a
/games start force original 0 -100 20 minecraft:the_nether @a[name=!MarcPG1905]
/games start force item-only ~100 ~ 0 minecraft:the_end @a
```

There is currently no queue system, so you can only *force* a game.

### Stop a Game

To stop a game, you just have to use `/games stop`, followed by the game's ID. This is an example usage:

```
/games stop 2yh1X6CenI
```

### Get Game Info

To get info about a game, just use `/games info`, followed by the game's ID. This is an example usage:

```
/games info 2yh1X6CenI
```

It will return a bunch of technical details and other info about the game, like the current item cooldown and players.

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
