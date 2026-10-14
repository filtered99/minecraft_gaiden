package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.filtered99.gaiden.skin.GaidenSkins;

import net.minecraft.client.render.texture.TextureManager;

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {
	// While an account skin loads, or if it is unavailable, fall back to the default skin in the 64x64 layout
	@Redirect(
		method = "loadHttpTexture",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/texture/TextureManager;load(Ljava/lang/String;)I")
	)
	private int gaiden$defaultSkin(TextureManager textureManager, String backup, String url, String originalBackup) {
		return GaidenSkins.isKey(url) ? GaidenSkins.defaultTexture(textureManager) : textureManager.load(backup);
	}
}
