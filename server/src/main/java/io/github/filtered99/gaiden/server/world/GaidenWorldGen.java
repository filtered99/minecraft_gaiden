package io.github.filtered99.gaiden.server.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.VeinFeature;

/**
 * Gaiden's extra world features: obsidian pockets, boulders and fallen logs.
 * <p>
 * Runs after Alpha's own decoration, with its own random seeded from the world seed and chunk position, so vanilla
 * terrain, ores and trees are unchanged and the same seed always gives the same features. Like vanilla decoration,
 * everything is placed in the 16x16 area offset by 8 blocks, which is already generated.
 * <p>
 * Keep this class identical in the client and server mods so singleplayer and multiplayer worlds match.
 */
public final class GaidenWorldGen {
	private GaidenWorldGen() {
	}

	public static void populate(World world, int chunkX, int chunkZ) {
		Random random = new Random(world.seed ^ (chunkX * 341873128712L + chunkZ * 132897987541L) ^ 0x6A1DE4L);
		int x0 = chunkX * 16;
		int z0 = chunkZ * 16;
		Biome biome = world.getBiomeSource().getBiome(x0 + 16, z0 + 16);
		boolean forest = biome == Biome.FOREST || biome == Biome.RAINFOREST || biome == Biome.SEASONAL_FOREST || biome == Biome.TAIGA;

		obsidianPocket(world, random, x0, z0);
		boulder(world, random, x0, z0, forest);
		fallenLog(world, random, x0, z0, forest);
	}

	// About one small obsidian vein every three chunks, deep underground (y 4-31), replacing stone like an ore
	private static void obsidianPocket(World world, Random random, int x0, int z0) {
		if (random.nextInt(3) != 0) {
			return;
		}

		int x = x0 + random.nextInt(16);
		int y = 4 + random.nextInt(28);
		int z = z0 + random.nextInt(16);
		new VeinFeature(Block.OBSIDIAN.id, 8).place(world, random, x, y, z);
	}

	// A mossy cobblestone boulder, partly sunk into the ground: one in three forest chunks, one in five chunks on hills
	private static void boulder(World world, Random random, int x0, int z0, boolean forest) {
		int roll = random.nextInt(15);
		int cx = x0 + 11 + random.nextInt(10);
		int cz = z0 + 11 + random.nextInt(10);
		int radius = 1 + random.nextInt(2);
		int y = world.getHeight(cx, cz);
		boolean hill = y > 80;
		if (!(forest && roll < 5 || hill && roll < 3)) {
			return;
		}
		if (!isGround(world.getBlock(cx, y - 1, cz))) {
			return;
		}

		int cy = y + radius - 2;
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					if (dx * dx + dy * dy + dz * dz > radius * radius + random.nextInt(2)) {
						continue;
					}

					int bx = cx + dx;
					int by = cy + dy;
					int bz = cz + dz;
					int existing = world.getBlock(bx, by, bz);
					if (existing == 0 || isGround(existing) || isPlant(existing)) {
						world.setBlockQuietly(bx, by, bz, random.nextInt(3) == 0 ? Block.COBBLESTONE.id : Block.MOSSY_COBBLESTONE.id);
					}
				}
			}
		}
	}

	// A log lying on flat ground (grass or dirt under every block), 3-5 blocks long, in one of three forest chunks
	private static void fallenLog(World world, Random random, int x0, int z0, boolean forest) {
		int roll = random.nextInt(3);
		int length = 3 + random.nextInt(3);
		boolean alongX = random.nextBoolean();
		int sx = x0 + 9 + random.nextInt(14 - length);
		int sz = z0 + 9 + random.nextInt(14 - length);
		if (!forest || roll != 0) {
			return;
		}

		int y = world.getHeight(sx, sz);
		for (int i = 0; i < length; i++) {
			int x = alongX ? sx + i : sx;
			int z = alongX ? sz : sz + i;
			int below = world.getBlock(x, y - 1, z);
			int at = world.getBlock(x, y, z);
			if (!(below == Block.GRASS.id || below == Block.DIRT.id) || !(at == 0 || isPlant(at))) {
				return;
			}
		}

		for (int i = 0; i < length; i++) {
			world.setBlockQuietly(alongX ? sx + i : sx, y, alongX ? sz : sz + i, Block.LOG.id);
		}
	}

	private static boolean isGround(int block) {
		return block == Block.GRASS.id || block == Block.DIRT.id || block == Block.STONE.id || block == Block.GRAVEL.id;
	}

	private static boolean isPlant(int block) {
		return block == Block.YELLOW_FLOWER.id || block == Block.RED_FLOWER.id || block == Block.BROWN_MUSHROOM.id
			|| block == Block.RED_MUSHROOM.id || block == Block.SNOW_LAYER.id;
	}
}
