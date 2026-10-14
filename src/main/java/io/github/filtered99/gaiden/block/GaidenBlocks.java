package io.github.filtered99.gaiden.block;

import net.minecraft.block.Block;

public final class GaidenBlocks {
	public static Block SPAWN_ANCHOR;

	private GaidenBlocks() {
	}

	/**
	 * Called from {@code Block}'s static initializer, before vanilla creates the block items,
	 * so each Gaiden block gets its item exactly like a vanilla block does.
	 */
	public static void register() {
		SPAWN_ANCHOR = new SpawnAnchorBlock(92, 37);
	}
}
