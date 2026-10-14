package io.github.filtered99.gaiden.server.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

/**
 * Stairs drop themselves instead of delegating drops to their base block.
 */
@Mixin(StairsBlock.class)
public abstract class StairsBlockMixin extends Block {
	protected StairsBlockMixin(int id, Material material) {
		super(id, material);
	}

	@Inject(method = "getDropItem", at = @At("HEAD"), cancellable = true)
	private void gaiden$dropSelf(int metadata, Random random, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(this.id);
	}

	@Inject(method = "dropItems(Lnet/minecraft/world/World;IIIIF)V", at = @At("HEAD"), cancellable = true)
	private void gaiden$dropItems(World world, int x, int y, int z, int metadata, float luck, CallbackInfo ci) {
		super.dropItems(world, x, y, z, metadata, luck);
		ci.cancel();
	}

	@Inject(method = "dropItems(Lnet/minecraft/world/World;IIII)V", at = @At("HEAD"), cancellable = true)
	private void gaiden$dropItems(World world, int x, int y, int z, int metadata, CallbackInfo ci) {
		super.dropItems(world, x, y, z, metadata);
		ci.cancel();
	}
}
