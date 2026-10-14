package io.github.filtered99.gaiden.server.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

@Mixin(FenceBlock.class)
public abstract class FenceBlockMixin extends Block {
	protected FenceBlockMixin(int id, Material material) {
		super(id, material);
	}

	// Fences may be placed on top of other fences
	@Inject(method = "canBePlaced", at = @At("HEAD"), cancellable = true)
	private void gaiden$allowStacking(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		boolean supported = world.getBlock(x, y - 1, z) == this.id || world.getMaterial(x, y - 1, z).isSolid();
		cir.setReturnValue(supported && super.canBePlaced(world, x, y, z));
	}
}
