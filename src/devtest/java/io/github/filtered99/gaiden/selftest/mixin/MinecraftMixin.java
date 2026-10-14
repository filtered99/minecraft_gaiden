package io.github.filtered99.gaiden.selftest.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.filtered99.gaiden.selftest.GaidenSelfTest;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	// Run once the game has started, then exit with the result. halt() rather than exit(): a shutdown
	// hook joins this thread, so exit() from here deadlocks.
	@Inject(method = "init", at = @At("TAIL"))
	private void gaidenSelfTest$run(CallbackInfo ci) {
		boolean ok = GaidenSelfTest.run();
		System.out.flush();
		Runtime.getRuntime().halt(ok ? 0 : 1);
	}
}
