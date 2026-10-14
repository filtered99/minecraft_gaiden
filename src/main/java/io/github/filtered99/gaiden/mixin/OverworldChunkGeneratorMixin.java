package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.filtered99.gaiden.world.GaidenWorldGen;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;

@Mixin(OverworldChunkGenerator.class)
public abstract class OverworldChunkGeneratorMixin {
	@Shadow
	private World world;

	// After vanilla decoration, before snow is laid, so snow settles on the new features
	@Inject(
		method = "populateChunk",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/source/BiomeSource;getTemperatures([DIIII)[D")
	)
	private void gaiden$addFeatures(ChunkSource source, int chunkX, int chunkZ, CallbackInfo ci) {
		GaidenWorldGen.populate(this.world, chunkX, chunkZ);
	}
}
