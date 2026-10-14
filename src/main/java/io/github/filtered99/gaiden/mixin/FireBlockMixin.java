package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.FireBlock;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin {
	@Shadow
	private void setFlammable(int block, int flammability, int burnChance) {
	}

	// Raw ids: fire is built before these blocks exist in Block's static init
	@Inject(method = "<init>", at = @At("TAIL"))
	private void gaiden$addFlammableBlocks(CallbackInfo ci) {
		this.setFlammable(53, 5, 20); // Wooden stairs
		this.setFlammable(85, 5, 20); // Fence
		this.setFlammable(64, 5, 20); // Wooden door
		this.setFlammable(58, 5, 20); // Workbench
		this.setFlammable(54, 5, 20); // Chest
		this.setFlammable(63, 5, 20); // Standing sign
		this.setFlammable(68, 5, 20); // Wall sign
		this.setFlammable(65, 5, 20); // Ladder
		this.setFlammable(72, 5, 20); // Wooden pressure plate
	}
}
