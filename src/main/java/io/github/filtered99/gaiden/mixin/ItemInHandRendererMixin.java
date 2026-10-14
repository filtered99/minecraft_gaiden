package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.filtered99.gaiden.skin.SkinModelSelector;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.ItemInHandRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
	@Shadow
	private Minecraft minecraft;

	// Use the classic or slim arm matching the local player's skin
	@Inject(
		method = "renderHand",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerRenderer;renderRightArm()V")
	)
	private void gaiden$selectArmModel(float tickDelta, CallbackInfo ci) {
		((SkinModelSelector) EntityRenderDispatcher.INSTANCE.getRenderer(this.minecraft.player)).gaiden$selectSkinModel(this.minecraft.player.httpTexture1);
	}
}
