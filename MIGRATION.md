# Gaiden: RetroMCP → Ornithe migration

Source of truth: the RetroMCP workspace (`filtered99/gaiden-edition`, branch `gaiden-alpha-1.2.7`,
`setup=a1.2.6`). The feature list below comes from `git diff master..gaiden-alpha-1.2.7` of that
workspace, plus the uncommitted spawn anchor particle change that existed on 2026-10-14.
There are no added textures, sounds or other assets; the spawn anchor uses the obsidian sprite (37).

## Toolchain compatibility (Alpha 1.2.6)

| Item | Value | Evidence |
|---|---|---|
| Game version id | `a1.2.6` (client only; matching server is `a0.2.8`) | Ornithe meta `/v3/versions/gen2/intermediary` |
| Intermediary | `net.ornithemc:calamus-intermediary-gen2:a1.2.6` | Ornithe maven; also used by the Prism instance |
| Mappings | Feather gen2 `a1.2.6+build.2` | Ornithe meta `/v3/versions/gen2/feather/a1.2.6` |
| Raven / Sparrow / Nests | not published for a1.2.6 | Ornithe meta returns nothing, so removed from `build.gradle` |
| OSL | not used (the mod needs no entrypoint, only mixins) | removed from `build.gradle` |
| Loader | Fabric Loader 0.19.5 | template default, same as the Prism instance |
| Loom / Ploceus | 1.18.4 / 1.18.1 | `genSources`, `build`, `runClient` all succeed |
| Gradle JDK | 17+ required by Gradle 9.7.1 | verified with Prism's bundled Java 25 |
| `fabric.mod.json` version | `1.0.0-alpha.2.6` | Loader normalises `a1.2.6` to this; `"a1.2.6"` fails resolution |

Changes from the template: `minecraft_version = a1.2.6`, `feather_build = 2`, raven/sparrow/nests/OSL lines removed,
example mod replaced with the `gaiden` mod (`environment: client`).

## Feature checklist

Status key:
- **Verified (runtime)**: observed in the running game.
- **Applied (runtime)**: the dev client loaded the target class and the mixin transformed it without errors, and the
  transformed bytecode was inspected. The behaviour has not been played yet.
- **Compiled only**: builds and remaps, but the target class was not loaded during the test launch.

| # | Feature (original behaviour) | RetroMCP source | Ornithe implementation | Status |
|---|---|---|---|---|
| 1a | Title screen: "Gaiden Edition: Alpha v1.2.7" and footer "Gaiden Edition - Unofficial Minecraft project." | `GuiMainMenu.drawScreen` | `TitleScreenMixin` | **Verified (runtime)** (screenshot) |
| 1b | HUD / F3 text "Gaiden Edition Alpha v1.2.7" | `GuiIngame` | `GameGuiMixin` | Applied (runtime) |
| 1c | Crash screen text | `PanelCrashReport` | `CrashReportPanelMixin` | Applied (runtime) |
| 1d | Save-conflict and multiplayer screen text | `GuiConflictWarning`, `GuiMultiplayer` | `WorldSaveConflictScreenMixin`, `JoinMultiplayerScreenMixin` | Applied (runtime) |
| 1e | Window title "Gaiden Edition: Alpha v1.2.7"; standalone frame "Gaiden Edition" | `Minecraft.startGame`, `Minecraft.func_6253_a` | `MinecraftMixin` | **Partial**: mixin applies, but the dev launch window still shows "Minecraft" (the game is embedded in the launcher's frame, so `Display.setTitle` is not visible). Behaviour in Prism is untested. |
| 2 | Recipe outputs: book 2, fence 3, bookshelf 2, slab 6, wood/cobble stairs 6, ladder 3, wood/iron door 3, sign 3 | `CraftingManager` | `CraftingManagerMixin` | Compiled only |
| 3 | Cheaper recipes: glowstone 2×2, wool 2×2, button = 1 stone, pressure plates = 2 | `CraftingManager` | `CraftingManagerMixin` | Compiled only |
| 4 | Reverse recipes: snow block → 4 snowballs, clay block → 4 clay, brick block → 4 bricks | `CraftingManager` | `CraftingManagerMixin` (same pre-sort position as the original) | Compiled only |
| 5 | Glass and bookshelves drop themselves | `BlockGlass`, `BlockBookshelf` | `GlassBlockMixin`, `BookshelfBlockMixin` | Applied (runtime) |
| 6 | Glowstone drops 4 dust | `BlockLightStone` | `GlowstoneBlockMixin` | Applied (runtime) |
| 7 | Stairs drop themselves | `BlockStairs` | `StairsBlockMixin` | Applied (runtime) |
| 8 | A boat broken by hand drops a boat | `EntityBoat.attackEntityFrom` | `BoatEntityMixin` | Applied (runtime) |
| 9 | Doors and signs stack to 16 | `ItemDoor`, `ItemSign` | `DoorItemMixin`, `SignItemMixin` | Applied (runtime) |
| 10 | Wooden stairs, fence, wooden door, workbench, chest, signs, ladder and wooden pressure plate burn (5, 20) | `BlockFire` constructor (raw ids, see note) | `FireBlockMixin` | Applied (runtime) |
| 11 | Fences can stack vertically | `BlockFence.canPlaceBlockAt` | `FenceBlockMixin` | Applied (runtime) |
| 12 | Obsidian hardness 10 → 5 | `Block` static init | `BlockMixin` (`@ModifyConstant` sliced after `new ObsidianBlock`) | Applied (runtime) |
| 13a | Spawn anchor block, id 92, obsidian sprite, hardness 10, resistance 2000 | `BlockSpawnAnchor`, `Block` | `SpawnAnchorBlock`, `GaidenBlocks`, `BlockMixin` | Applied (runtime): registered before vanilla's block-item loop, and the game started without an id clash |
| 13b | Spawn anchor recipe: 8 obsidian around a compass | `CraftingManager` | `CraftingManagerMixin` | Compiled only |
| 13c | Singleplayer use: sets the **world** spawn and shows "Spawn point set." in chat | `BlockSpawnAnchor.blockActivated`, `EntityPlayer`/`EntityPlayerSP.addChatMessage` | `SpawnAnchorBlock.use` + `ClientPlayerEntityAccessor` (chat goes to the same `GameGui.addChatMessage`) | Compiled only |
| 13d | Portal particles above the anchor (was uncommitted in RetroMCP) | `BlockSpawnAnchor.randomDisplayTick` | `SpawnAnchorBlock.randomDisplayTick` | Compiled only |
| S1 | **Server:** per-player spawn anchor, saved to player NBT (`HasSpawnAnchor`, `SpawnAnchorX/Y/Z`) and used on respawn if the block still exists; "Spawn point set." is sent as a chat packet | `minecraft_server`: `BlockSpawnAnchor`, `EntityPlayer`, `ServerConfigurationManager.recreatePlayerEntity` | `server/`: `SpawnAnchorBlock`, `SpawnAnchorHolder`, `PlayerEntityMixin` (fields + NBT), `PlayerManagerMixin` (respawn, via MixinExtras `@ModifyExpressionValue`) | Applied (runtime): the dev server reached "Done!"; the respawn hook runs right after the new player is constructed, as in the original |
| S2 | **Server:** copies of features 5–12 and the spawn anchor block (id 92) | `minecraft_server/src` | `server/` (same Feather names as the client, so these mirror the client mixins) | Applied (runtime) |
| S3 | **Server:** recipe changes | — | — | Not applicable: the RetroMCP server never had them, so the server port doesn't add them either |

Not features: `.idea`/`.iml` changes, `minecraft_server/game/ops.txt` (runtime file), and the removed world save files.

Note on #10: `FireBlock` is built partway through `Block`'s static initializer, before fences, doors and the other
blocks exist. That is why the original used raw ids (see the RetroMCP commit "Fix fire block initialization crash").
The port keeps the raw ids for the same reason.

## Server mod (`server/` subproject)

The Alpha multiplayer server is a separate game jar, `a0.2.8`, which pairs with client `a1.2.6`. The `server`
Gradle subproject builds `gaiden-server-<version>+mca0.2.8.jar` against it:

- Mappings are Feather gen2 `a0.2.8+build.2` with intermediary `calamus-intermediary-gen2:a0.2.8`.
- `fabric.mod.json` must depend on `1.0.0-alpha.2.8`, which is how Loader normalises `a0.2.8`.
- `:server:genSources` fails because Feather's unpick data references LWJGL constants the server doesn't ship.
  This only affects the readable-source step; `build` and `runServer` work. To read the server code, decompile the
  mapped jar under `.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-server-*` with Vineflower.
- The client and server mods are separate source trees, like the two RetroMCP projects. A fix to a shared
  block or item change has to be made in both.

Players joining a Gaiden server need the Gaiden client mod as well.

Respawn height fix: the RetroMCP server placed a respawning player flush on top of the anchor
(`anchorY + 1`). In multiplayer this left the client stuck. After a respawn the server sends terrain only
for movement it has accepted, so a player standing exactly on the block's top surface collided before the
terrain arrived. The port now places the player 1.62 above the anchor, the same height the vanilla server
uses for a normal respawn. Singleplayer is unchanged. There the client pushes the player out of the anchor
block onto its top, as vanilla does for any spawn, with no server round trip involved. The server sends block id 92, which a
vanilla client doesn't have.

## In-game test plan (not yet run)

Use a fresh singleplayer world in the Prism instance (or `runClient`):

1. Craft each recipe in rows 2–4 and 13b and check the output count and pattern. The game log prints `N recipes`
   when crafting first loads; it should be the vanilla count + 4.
2. Break glass, a bookshelf, glowstone (expect 4 dust), wood and cobble stairs, and a boat (hit it by hand).
3. Stack doors and signs in the inventory (expect max 16).
4. Light fire next to each block in row 10 and confirm it burns.
5. Place a fence on top of a fence.
6. Mine obsidian with a diamond pickaxe; it should take about half the vanilla time.
7. Place a spawn anchor: check the portal particles, right-click it ("Spawn point set."), die and respawn at it.
   Save and quit, reload the world, die again and confirm the spawn is still at the anchor (world spawn is saved in `level.dat`).
8. Open F3 and check the HUD version string.

Server (`./gradlew :server:runServer`, then connect from a Gaiden client to `localhost`):

9. Two players each right-click a different anchor. Each should get "Spawn point set." and respawn at their own anchor.
10. Restart the server and die again; each player's anchor should be kept (stored in `world/players/<name>.dat`).
11. Break an anchor, then die; that player should respawn at world spawn and the anchor should be forgotten.
12. Repeat checks 2, 3, 5 and 6 on the server.
