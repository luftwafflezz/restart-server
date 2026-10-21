# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## `v1.3.0` - 21/10/2026

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

## `v1.2.0` - 28/06/2023

### Added

- Compatibility for the Fabric Permissions API

## `v1.1.1` - 16/06/2023

### Changed

- Updated to Minecraft 1.20

### Fixed

- Player join causes server to instantly restart
