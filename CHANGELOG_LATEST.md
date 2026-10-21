### Changed

- Updated to Minecraft 26.3
- Updated to Fabric Loader 0.19.5
- Updated to Fabric API 0.164.0+26.3
- Updated to Java 25
- Updated to Gradle 9.8.2 and Fabric Loom 1.19.2
- Migrated to Mojang mappings (unobfuscated Minecraft)
- Scheduled restarts and the restart when no players have been online now also run while Minecraft pauses an empty server (`pause-when-empty-seconds` in `server.properties`, 60 seconds by default)
- Restart messages in the server console are now prefixed with `System chat:` by Minecraft
- The default config values are now meant for Linux: `restartScriptPath` is `./start.sh`, `openInTerminal` is `false` and `terminalStartCommand` is `x-terminal-emulator -e` (existing config files keep their values)

### Fixed

- Scheduled restarts repeated every tick when the restart didn't stop the server (`stopServer` disabled, or the restart script couldn't be started)
- An invalid `restartScriptPath` (for example an empty one) crashed the server instead of failing the restart
- Players are now disconnected from a copy of the player list

### Removed

- Support for Minecraft 1.19.x-1.20.x, use older releases for these versions
- Support for the Quilt modloader
