# Minecraft Gaiden

Gaiden Edition gameplay changes for Minecraft **Alpha 1.2.6**, packaged as an Ornithe (Gen2, Fabric Loader) mod.
It is a port of the original RetroMCP implementation. See [MIGRATION.md](MIGRATION.md) for the feature list
and the status of each feature.

## Requirements

- JDK 17 or newer to run Gradle (the mod itself compiles to Java 8 bytecode).
  Prism's bundled runtime works, for example:
  `JAVA_HOME=%APPDATA%\PrismLauncher\java\java-runtime-epsilon`
- An Ornithe Gen2 Fabric `a1.2.6` instance in Prism Launcher (Fabric Loader 0.19.5,
  intermediary `calamus-intermediary-gen2:a1.2.6`).

## Build

```sh
./gradlew build
```

The mod jar is written to `build/libs/gaiden-<version>+mca1.2.6.jar` (ignore the `-sources` jar).

## Run in development

```sh
./gradlew runClient
```

## Install in Prism

1. Build the mod.
2. Copy `build/libs/gaiden-<version>+mca1.2.6.jar` into the instance's `minecraft/mods` folder
   (Prism: right-click the instance > Folder > `minecraft/mods`, creating `mods` if needed).
3. Launch the instance. The title screen should read "Gaiden Edition: Alpha v1.2.7".

## Distribution

The jar contains only Gaiden's own classes and its icon. It does not include Minecraft code or assets;
players get the game from their own launcher. Distribute the mod jar only, never the game jar or
decompiled Minecraft source.

## License

The template's own license is in [LICENSE-TEMPLATE.md](LICENSE-TEMPLATE.md) and does not cover this mod.
No license has been chosen for Gaiden yet.
