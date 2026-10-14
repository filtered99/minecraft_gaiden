package io.github.filtered99.gaiden.server.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.GlassBlock;

@Mixin(GlassBlock.class)
public abstract class GlassBlockMixin {
	// Drops itself
	@Inject(method = "getBaseDropCount", at = @At("HEAD"), cancellable = true)
	private void gaiden$dropSelf(Random random, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(1);
	}
}
