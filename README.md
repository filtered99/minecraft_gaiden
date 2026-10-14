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

## Server mod

The Alpha multiplayer server (`a0.2.8`, which pairs with client `a1.2.6`) has its own mod in `server/`:

```sh
./gradlew :server:build      # writes server/build/libs/gaiden-server-<version>+mca0.2.8.jar
./gradlew :server:runServer  # development server
```

Players joining a Gaiden server need the Gaiden client mod as well.

### Set up a server

1. Download the [Ornithe installer](https://maven.ornithemc.net/releases/net/ornithemc/ornithe-installer/)
   (tested with 0.16.0) and install a Fabric server for `a0.2.8`:

   ```sh
   java -jar ornithe-installer-0.16.0.jar install server a0.2.8 Fabric 0.19.5 --install-dir=<server folder> --download-server
   ```

   `--download-server` saves the vanilla server as `server.jar`. The installer gets it from Omniarchive, a
   community archive; it is the same file Loom uses for development.
2. Build the server mod and copy `server/build/libs/gaiden-server-<version>+mca0.2.8.jar` into
   `<server folder>/mods` (create the folder if needed).
3. Start the server from the server folder:

   ```sh
   java -Xms1024M -Xmx1024M -jar fabric-server-launch.jar nogui
   ```

   Java 8 or newer works. Type `stop` in the console to shut down and save. The installer's
   `--create-scripts` option did not create any scripts in 0.16.0, so make your own start script if you want one.

## Distribution

The jar contains only Gaiden's own classes and its icon. It does not include Minecraft code or assets;
players get the game from their own launcher. Distribute the mod jar only, never the game jar or
decompiled Minecraft source.

## License

The template's own license is in [LICENSE-TEMPLATE.md](LICENSE-TEMPLATE.md) and does not cover this mod.
No license has been chosen for Gaiden yet.
