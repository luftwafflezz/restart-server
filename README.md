![Restart Server icon](docs/media/icon_128x128.png)

# Restart Server

Adds a restart command to dedicated servers.

> **About this project:** Restart Server was created by [Steveplays28](https://github.com/Steveplays28). The [original repository](https://github.com/steves-underwater-paradise/restart-server) hasn't been updated since 2023, so development continues here, independently of it, starting with the port to Minecraft 26.3. All credit for the original mod goes to Steveplays28.

![Restart command screenshot](docs/media/command_preview.png)

This mod is for when you don't want to run `/stop` and then run the start script again.  
Supports scheduling restarts at intervals, or when no players have been online for some time.

Everything is fully configurable in the `restart-server.json5` config file (in the `config` folder). You can reload the mod's config using `/reload`.

By default `/restart` runs `./start.sh` in the server's root directory and then stops the server. The script has to be executable (`chmod +x start.sh`) and start with a shebang, for example:

```sh
#!/bin/sh
# Wait for the old server to release its port, then start the new one detached from the old server's process
( sleep 10; exec setsid java -Xmx4G -jar fabric-server-launch.jar nogui > server.log 2>&1 < /dev/null ) &
```

The script is started by the server and doesn't have a terminal (unless `openInTerminal` is enabled), so it has to start the server detached with its output redirected, as in the example. The old server keeps running for a moment after the script has started, so wait for it to stop before binding the port again.

## Dependencies

- Minecraft 26.3, running on Java 25
- [Fabric Loader](https://fabricmc.net/use/server/) 0.19.3 or newer
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Cloth Config API](https://modrinth.com/mod/cloth-config)

## Download

[![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2/assets/cozy/available/github_vector.svg)](https://github.com/luftwafflezz/restart-server/releases)

![fabric](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2/assets/compact/supported/fabric_vector.svg)

Builds are published on the GitHub releases page, or you can build the mod from source with `./gradlew build` (JDK 25). The Modrinth and CurseForge pages of the original project only carry its builds for older Minecraft versions.

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

This project is licensed under LGPLv2.1, see [LICENSE](LICENSE).
