![Restart Server icon](docs/media/icon_128x128.png)

# Restart Server

Adds a restart command to dedicated servers.

> **Fork notice:** this is an unofficial port of [Steveplays28's restart-server](https://github.com/steves-underwater-paradise/restart-server) to Minecraft 26.3, maintained at [luftwafflezz/restart-server](https://github.com/luftwafflezz/restart-server).

![Restart command screenshot](docs/media/command_preview.png)

I made this so you don't have to run `/stop` and then run the start script again.  
Supports scheduling restarts at intervals, or when no players have been online for some time.

Everything is fully configurable in the `restart-server.json5` config file (in the `config` folder). You can reload the mod's config using `/reload`.

By default `/restart` runs `./start.sh` in the server's root directory and then stops the server. The script has to be executable (`chmod +x start.sh`) and start with a shebang, for example:

```sh
#!/bin/sh
java -Xmx4G -jar fabric-server-launch.jar nogui
```

## Dependencies

- Minecraft 26.3, running on Java 25
- [Fabric Loader](https://fabricmc.net/use/server/) 0.19.3 or newer
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Cloth Config API](https://modrinth.com/mod/cloth-config)

## Download

[![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2/assets/cozy/available/github_vector.svg)](https://github.com/luftwafflezz/restart-server/releases)
[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/restart-server)
[![curseforge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/restart-server)

![fabric](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2/assets/compact/supported/fabric_vector.svg)

The Modrinth and CurseForge pages above only carry the original author's builds for older Minecraft versions; Minecraft 26.3 builds of this fork are published on its GitHub releases page once released, or can be built from source with `./gradlew build` (JDK 25).

See the version info in the filename for the supported Minecraft versions.  
Made for the Fabric modloader.  
Server side only.

## FAQ

![forge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2/assets/cozy/unsupported/forge_vector.svg)

- Q: Will you be backporting this to lower Minecraft versions?  
A: No.

- Q: Forge pls?  
A: Also no.

- Q: Does only the server need this mod or does the client need it too?  
A: Only the server needs this mod. Singleplayer/LAN is not supported.

## License

This project is licensed under LGPLv2.1, see [LICENSE](https://github.com/Steveplays28/restart-server/blob/main/LICENSE).
