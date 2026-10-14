package io.github.filtered99.gaiden.server.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.Block;
import net.minecraft.block.GlowstoneBlock;
import net.minecraft.block.material.Material;

@Mixin(GlowstoneBlock.class)
public abstract class GlowstoneBlockMixin extends Block {
	protected GlowstoneBlockMixin(int id, Material material) {
		super(id, material);
	}

	// Drops four dust
	@Override
	public int getBaseDropCount(Random random) {
		return 4;
	}
}
