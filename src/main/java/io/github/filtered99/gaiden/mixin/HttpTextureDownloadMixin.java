package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.filtered99.gaiden.skin.GaidenSkins;

import net.minecraft.client.render.texture.HttpTexture;

/**
 * Alpha's skin download thread. Account skin keys are loaded by Gaiden on this same background thread.
 */
@Mixin(targets = "net.minecraft.client.render.texture.HttpTexture__34366662")
public abstract class HttpTextureDownloadMixin {
	@Shadow
	@Final
	String f_17044395;
	@Shadow
	@Final
	HttpTexture f_67917914;

	@Inject(method = "run", at = @At("HEAD"), cancellable = true)
	private void gaiden$loadAccountSkin(CallbackInfo ci) {
		if (GaidenSkins.isKey(this.f_17044395)) {
			this.f_67917914.image = GaidenSkins.load(this.f_17044395);
			ci.cancel();
		}
	}
}
