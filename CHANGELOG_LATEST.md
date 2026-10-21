### Changed

- Updated to Minecraft 26.3
- Updated to Fabric Loader 0.19.5
- Updated to Fabric API 0.164.0+26.3
- Updated to Java 25
- Updated to Gradle 9.8.2 and Fabric Loom 1.19.2
- Migrated to Mojang mappings (unobfuscated Minecraft)
- Scheduled restarts and the restart when no players have been online now also run while Minecraft pauses an empty server (`pause-when-empty-seconds` in `server.properties`, 60 seconds by default)
- Restart messages in the server console are now prefixed with `System chat:` by Minecraft

### Removed

- Support for Minecraft 1.19.x-1.20.x, use older releases for these versions
- Quilt is no longer listed as a supported modloader, it has not been tested with Minecraft 26.x
