package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.Item;
import net.minecraft.item.SignItem;

@Mixin(SignItem.class)
public abstract class SignItemMixin extends Item {
	protected SignItemMixin(int id) {
		super(id);
	}

	// Stacks to 16
	@Inject(method = "<init>", at = @At("TAIL"))
	private void gaiden$stackTo16(CallbackInfo ci) {
		this.maxStackSize = 16;
	}
}
