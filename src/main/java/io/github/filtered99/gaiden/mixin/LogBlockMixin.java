package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;

import io.github.filtered99.gaiden.block.LogAxis;

import net.minecraft.block.Block;
import net.minecraft.block.LogBlock;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

/**
 * Logs face the way they are placed, like modern Minecraft.
 */
@Mixin(LogBlock.class)
public abstract class LogBlockMixin extends Block {
	private LogBlockMixin(int id, Material material) {
		super(id, material);
	}

	// Rings on the two end faces, bark on the others
	@Override
	public int getSprite(int face, int metadata) {
		return LogAxis.isEndFace(LogAxis.of(metadata), face) ? 21 : 20;
	}

	@Override
	public void updateMetadataOnPlaced(World world, int x, int y, int z, int face) {
		world.setBlockMetadata(x, y, z, LogAxis.forPlacedFace(face));
	}
}
