package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.texture.TextureManager;
import net.minecraft.client.resource.pack.TexturePacks;

@Mixin(TextureManager.class)
public interface TextureManagerAccessor {
	@Accessor("texturePacks")
	TexturePacks gaiden$getTexturePacks();
}
