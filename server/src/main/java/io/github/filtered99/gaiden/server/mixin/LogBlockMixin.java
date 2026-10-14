package io.github.filtered99.gaiden.server.mixin;

import org.spongepowered.asm.mixin.Mixin;

import io.github.filtered99.gaiden.server.block.LogAxis;

import net.minecraft.block.Block;
import net.minecraft.block.LogBlock;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

/**
 * Logs face the way they are placed, like modern Minecraft. Clients draw the direction from the block metadata.
 */
@Mixin(LogBlock.class)
public abstract class LogBlockMixin extends Block {
	private LogBlockMixin(int id, Material material) {
		super(id, material);
	}

	@Override
	public void updateMetadataOnPlaced(World world, int x, int y, int z, int face) {
		world.setBlockMetadata(x, y, z, LogAxis.forPlacedFace(face));
	}
}
