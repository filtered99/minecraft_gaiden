package io.github.filtered99.gaiden.selftest;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Random;

import io.github.filtered99.gaiden.GaidenVersion;
import io.github.filtered99.gaiden.block.SpawnAnchorBlock;

import net.minecraft.block.Block;
import net.minecraft.block.FireBlock;
import net.minecraft.crafting.CraftingManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Checks the singleplayer Gaiden changes against the values in the original RetroMCP source,
 * using the game's own registries once it has started. Development only.
 */
public final class GaidenSelfTest {
	private static final int E = -1;
	private static int passed;
	private static int failed;

	private GaidenSelfTest() {
	}

	public static boolean run() {
		System.out.println("[Gaiden self test] starting");
		recipes();
		drops();
		stackSizes();
		flammability();
		hardness();
		spawnAnchor();
		branding();
		worldGen();
		System.out.println("[Gaiden self test] " + passed + " passed, " + failed + " failed");
		return failed == 0;
	}

	private static void recipes() {
		int paper = Item.PAPER.id, stick = Item.STICK.id, planks = Block.PLANKS.id, book = Item.BOOK.id;
		int cobble = Block.COBBLESTONE.id, stone = Block.STONE.id, iron = Item.IRON_INGOT.id;
		int dust = Item.GLOWSTONE_DUST.id, string = Item.STRING.id;

		// Increased outputs
		craft("book", grid(E, paper, E, E, paper, E, E, paper, E), book, 2);
		craft("fence", grid(stick, stick, stick, stick, stick, stick, E, E, E), Block.FENCE.id, 3);
		craft("bookshelf", grid(planks, planks, planks, book, book, book, planks, planks, planks), Block.BOOKSHELF.id, 2);
		craft("stone slab", grid(cobble, cobble, cobble, E, E, E, E, E, E), Block.STONE_SLAB.id, 6);
		craft("oak stairs", grid(planks, E, E, planks, planks, E, planks, planks, planks), Block.OAK_STAIRS.id, 6);
		craft("cobblestone stairs", grid(cobble, E, E, cobble, cobble, E, cobble, cobble, cobble), Block.STONE_STAIRS.id, 6);
		craft("ladder", grid(stick, E, stick, stick, stick, stick, stick, E, stick), Block.LADDER.id, 3);
		craft("wooden door", grid(planks, planks, E, planks, planks, E, planks, planks, E), Item.WOODEN_DOOR.id, 3);
		craft("iron door", grid(iron, iron, E, iron, iron, E, iron, iron, E), Item.IRON_DOOR.id, 3);
		craft("sign", grid(planks, planks, planks, planks, planks, planks, E, stick, E), Item.SIGN.id, 3);

		// Cheaper recipes, and the old patterns no longer work
		craft("glowstone from 2x2 dust", grid(dust, dust, E, dust, dust, E, E, E, E), Block.GLOWSTONE.id, 1);
		noRecipe("glowstone from 3x3 dust (old)", grid(dust, dust, dust, dust, dust, dust, dust, dust, dust));
		craft("wool from 2x2 string", grid(string, string, E, string, string, E, E, E, E), Block.WOOL.id, 1);
		noRecipe("wool from 3x3 string (old)", grid(string, string, string, string, string, string, string, string, string));
		craft("button from 1 stone", grid(stone, E, E, E, E, E, E, E, E), Block.STONE_BUTTON.id, 1);
		noRecipe("button from 2 stone (old)", grid(stone, E, E, stone, E, E, E, E, E));
		craft("stone pressure plate from 2 stone", grid(stone, stone, E, E, E, E, E, E, E), Block.STONE_PRESSURE_PLATE.id, 1);
		noRecipe("stone pressure plate from 3 stone (old)", grid(stone, stone, stone, E, E, E, E, E, E));
		craft("wooden pressure plate from 2 planks", grid(planks, planks, E, E, E, E, E, E, E), Block.WOODEN_PRESSURE_PLATE.id, 1);
		noRecipe("wooden pressure plate from 3 planks (old)", grid(planks, planks, planks, E, E, E, E, E, E));

		// Reverse recipes
		craft("snow block -> snowballs", grid(Block.SNOW.id, E, E, E, E, E, E, E, E), Item.SNOWBALL.id, 4);
		craft("clay block -> clay", grid(Block.CLAY.id, E, E, E, E, E, E, E, E), Item.CLAY.id, 4);
		craft("brick block -> bricks", grid(Block.BRICKS.id, E, E, E, E, E, E, E, E), Item.BRICK.id, 4);
		craft("snowballs -> snow block (vanilla, kept)", grid(Item.SNOWBALL.id, Item.SNOWBALL.id, E, Item.SNOWBALL.id, Item.SNOWBALL.id, E, E, E, E), Block.SNOW.id, 1);

		// Spawn anchor
		int obsidian = Block.OBSIDIAN.id;
		craft("spawn anchor", grid(obsidian, obsidian, obsidian, obsidian, Item.COMPASS.id, obsidian, obsidian, obsidian, obsidian), 92, 1);

		// Unchanged vanilla recipe as a control
		craft("planks from log (vanilla)", grid(Block.LOG.id, E, E, E, E, E, E, E, E), planks, 4);
	}

	private static void drops() {
		Random random = new Random(0);
		drop("glass drops itself", Block.GLASS, Block.GLASS.id, 1, random);
		drop("bookshelf drops itself", Block.BOOKSHELF, Block.BOOKSHELF.id, 1, random);
		drop("glowstone drops 4 dust", Block.GLOWSTONE, Item.GLOWSTONE_DUST.id, 4, random);
		drop("oak stairs drop themselves", Block.OAK_STAIRS, Block.OAK_STAIRS.id, 1, random);
		drop("cobblestone stairs drop themselves", Block.STONE_STAIRS, Block.STONE_STAIRS.id, 1, random);
		drop("spawn anchor drops itself", Block.BY_ID[92], 92, 1, random);
	}

	private static void stackSizes() {
		check("wooden doors stack to 16", Item.WOODEN_DOOR.getMaxStackSize() == 16, "" + Item.WOODEN_DOOR.getMaxStackSize());
		check("iron doors stack to 16", Item.IRON_DOOR.getMaxStackSize() == 16, "" + Item.IRON_DOOR.getMaxStackSize());
		check("signs stack to 16", Item.SIGN.getMaxStackSize() == 16, "" + Item.SIGN.getMaxStackSize());
		check("sticks still stack to 64 (control)", Item.STICK.getMaxStackSize() == 64, "" + Item.STICK.getMaxStackSize());
	}

	private static void flammability() {
		int[] flammability = intArrayField(FireBlock.class, Block.FIRE, "flammability");
		int[] burnChance = intArrayField(FireBlock.class, Block.FIRE, "burnChance");
		int[][] gaiden = {{53, 5, 20}, {85, 5, 20}, {64, 5, 20}, {58, 5, 20}, {54, 5, 20}, {63, 5, 20}, {68, 5, 20}, {65, 5, 20}, {72, 5, 20}};
		String[] names = {"wooden stairs", "fence", "wooden door", "workbench", "chest", "standing sign", "wall sign", "ladder", "wooden pressure plate"};
		for (int i = 0; i < gaiden.length; i++) {
			int id = gaiden[i][0];
			check(names[i] + " is flammable (5, 20)", flammability[id] == gaiden[i][1] && burnChance[id] == gaiden[i][2],
				flammability[id] + ", " + burnChance[id]);
		}
		check("leaves keep vanilla flammability (30, 60)", flammability[18] == 30 && burnChance[18] == 60, flammability[18] + ", " + burnChance[18]);
		check("stone is not flammable (control)", flammability[1] == 0 && burnChance[1] == 0, flammability[1] + ", " + burnChance[1]);
	}

	private static void hardness() {
		float obsidian = floatField(Block.OBSIDIAN, "miningTime");
		check("obsidian hardness is 5", obsidian == 5.0F, "" + obsidian);
		float resistance = floatField(Block.OBSIDIAN, "blastResistance");
		check("obsidian blast resistance unchanged (2000 * 3)", resistance == 6000.0F, "" + resistance);
	}

	private static void spawnAnchor() {
		Block anchor = Block.BY_ID[92];
		check("block 92 is the spawn anchor", anchor instanceof SpawnAnchorBlock, String.valueOf(anchor));
		if (anchor == null) {
			return;
		}
		check("spawn anchor has an item", Item.BY_ID[92] != null, String.valueOf(Item.BY_ID[92]));
		check("spawn anchor uses the obsidian sprite (37)", anchor.sprite == 37, "" + anchor.sprite);
		check("spawn anchor hardness is 10", floatField(anchor, "miningTime") == 10.0F, "" + floatField(anchor, "miningTime"));
		check("spawn anchor blast resistance is 2000 * 3", floatField(anchor, "blastResistance") == 6000.0F, "" + floatField(anchor, "blastResistance"));
		check("spawn anchor uses stone sounds", anchor.sounds == Block.STONE_SOUNDS, String.valueOf(anchor.sounds));
	}

	private static void branding() {
		check("version is read from the mod metadata", GaidenVersion.VERSION.matches("[0-9]+(\\.[0-9]+)+"), GaidenVersion.VERSION);
		check("title reads \"Gaiden Edition: Alpha v<version>\"", GaidenVersion.TITLE.equals("Gaiden Edition: Alpha v" + GaidenVersion.VERSION), GaidenVersion.TITLE);
		System.out.println("[Gaiden self test] version " + GaidenVersion.VERSION);
	}

	// Generates a fresh world from a fixed seed and counts Gaiden's world features in a 128x128 area
	private static void worldGen() {
		File dir = new File("selftest-worlds");
		deleteRecursively(new File(dir, "worldgen"));
		World world = new World(dir, "worldgen", 20261014L);

		// Pick a forested area that is mostly land: rank 8x8-chunk areas by forest biome (cheap, biome map only),
		// then generate a few sample columns in the best candidates and keep the one with the most grass
		java.util.List<int[]> candidates = new java.util.ArrayList<>();
		for (int ax = -1500; ax <= 1500; ax += 128) {
			for (int az = -1500; az <= 1500; az += 128) {
				int forest = 0;
				for (int cx = 0; cx < 8; cx++) {
					for (int cz = 0; cz < 8; cz++) {
						net.minecraft.world.biome.Biome b = world.getBiomeSource().getBiome(ax + cx * 16 + 16, az + cz * 16 + 16);
						if (b == net.minecraft.world.biome.Biome.FOREST || b == net.minecraft.world.biome.Biome.SEASONAL_FOREST
							|| b == net.minecraft.world.biome.Biome.RAINFOREST || b == net.minecraft.world.biome.Biome.TAIGA) {
							forest++;
						}
					}
				}
				candidates.add(new int[]{forest, ax, az});
			}
		}
		candidates.sort((p, q) -> q[0] - p[0]);
		int bestX = 0, bestZ = 0, bestForest = 0, bestLand = -1;
		for (int[] c : candidates.subList(0, Math.min(6, candidates.size()))) {
			int land = 0;
			for (int sx = 16; sx < 128; sx += 40) {
				for (int sz = 16; sz < 128; sz += 40) {
					int h = world.getHeight(c[1] + sx, c[2] + sz);
					int top = world.getBlock(c[1] + sx, h - 1, c[2] + sz);
					if (top == Block.GRASS.id || top == Block.LEAVES.id || top == Block.LOG.id) {
						land++;
					}
				}
			}
			if (land > bestLand) {
				bestLand = land;
				bestForest = c[0];
				bestX = c[1];
				bestZ = c[2];
			}
		}

		// Load every chunk in and around the area first: a chunk is only decorated once its +x/+z neighbours exist
		for (int x = bestX - 32; x < bestX + 160; x += 16) {
			for (int z = bestZ - 32; z < bestZ + 160; z += 16) {
				world.getBlock(x, 64, z);
			}
		}

		int obsidian = 0, boulderBlocks = 0, fallenLogs = 0, floatingLogs = 0, buriedBoulders = 0;
		for (int x = bestX; x < bestX + 128; x++) {
			for (int z = bestZ; z < bestZ + 128; z++) {
				int top = world.getHeight(x, z);
				for (int y = 1; y < 127; y++) {
					int block = world.getBlock(x, y, z);
					if (block == Block.OBSIDIAN.id) {
						obsidian++;
					} else if (block == Block.MOSSY_COBBLESTONE.id && y >= 56 && y >= top - 4) {
						boulderBlocks++;
					} else if (block == Block.MOSSY_COBBLESTONE.id && y >= 56 && world.getBlock(x, y + 1, z) == Block.STONE.id) {
						buriedBoulders++;
					} else if (block == Block.LOG.id) {
						int above = world.getBlock(x, y + 1, z);
						int below = world.getBlock(x, y - 1, z);
						if (above != Block.LOG.id && above != Block.LEAVES.id && (below == Block.GRASS.id || below == Block.DIRT.id)) {
							fallenLogs++;
						} else if (below == 0) {
							floatingLogs++;
						}
					}
				}
			}
		}

		System.out.println("[Gaiden self test] world gen (seed 20261014, 128x128 at " + bestX + "," + bestZ + ", " + bestForest + "/64 forest chunks): obsidian " + obsidian + ", surface boulder blocks "
			+ boulderBlocks + ", fallen log blocks " + fallenLogs);
		check("world gen: obsidian pockets generate underground", obsidian > 0, "" + obsidian);
		check("world gen: boulders generate on the surface", boulderBlocks > 0, "" + boulderBlocks);
		check("world gen: fallen logs generate on grass or dirt", fallenLogs > 0, "" + fallenLogs);
		check("world gen: no floating logs", floatingLogs == 0, "" + floatingLogs);
		deleteRecursively(new File(dir, "worldgen"));
	}

	private static void deleteRecursively(File file) {
		File[] children = file.listFiles();
		if (children != null) {
			for (File child : children) {
				deleteRecursively(child);
			}
		}
		file.delete();
	}

	private static int[] grid(int... ids) {
		return ids;
	}

	private static void craft(String what, int[] grid, int expectedId, int expectedCount) {
		ItemStack result = CraftingManager.getInstance().getResult(grid);
		check("recipe: " + what + " -> " + expectedCount,
			result != null && result.id == expectedId && result.size == expectedCount,
			result == null ? "no result" : result.id + " x" + result.size);
	}

	private static void noRecipe(String what, int[] grid) {
		ItemStack result = CraftingManager.getInstance().getResult(grid);
		check("no recipe: " + what, result == null, result == null ? "" : result.id + " x" + result.size);
	}

	private static void drop(String what, Block block, int expectedId, int expectedCount, Random random) {
		int id = block.getDropItem(0, random);
		int count = block.getBaseDropCount(random);
		check(what, id == expectedId && count == expectedCount, id + " x" + count);
	}

	private static int[] intArrayField(Class<?> owner, Object instance, String name) {
		try {
			Field field = owner.getDeclaredField(name);
			field.setAccessible(true);
			return (int[]) field.get(instance);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}

	private static float floatField(Block block, String name) {
		try {
			Field field = Block.class.getDeclaredField(name);
			field.setAccessible(true);
			return field.getFloat(block);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}

	private static void check(String what, boolean ok, String detail) {
		if (ok) {
			passed++;
		} else {
			failed++;
		}
		System.out.println("[Gaiden self test] " + (ok ? "PASS " : "FAIL ") + what + (ok ? "" : "  (got " + detail + ")"));
	}
}
