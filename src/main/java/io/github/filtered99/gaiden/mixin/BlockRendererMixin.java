package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.filtered99.gaiden.block.LogAxis;

import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.world.WorldView;

/**
 * Turns the bark texture on horizontal logs so it runs along the log. Alpha cannot rotate face textures, so while a
 * rotated log face is drawn its texture coordinates are turned 90 degrees inside the texture's 16x16 cell.
 */
@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {
	@Shadow
	private WorldView world;
	@Shadow
	private int forcedSprite;

	@Unique
	private int logAxis = -1;
	@Unique
	private boolean rotateFace;
	@Unique
	private int faceSprite;

	@Inject(method = "tesselateWithoutAmbientOcclusion", at = @At("HEAD"))
	private void gaiden$beginLog(Block block, int x, int y, int z, float r, float g, float b, CallbackInfoReturnable<Boolean> cir) {
		this.logAxis = block == Block.LOG && this.world != null ? LogAxis.of(this.world.getBlockMetadata(x, y, z)) : -1;
	}

	@Inject(method = "tesselateWithoutAmbientOcclusion", at = @At("RETURN"))
	private void gaiden$endLog(Block block, int x, int y, int z, float r, float g, float b, CallbackInfoReturnable<Boolean> cir) {
		this.logAxis = -1;
		this.rotateFace = false;
	}

	@Unique
	private void beginFace(int face, int sprite) {
		this.rotateFace = this.logAxis > 0 && this.forcedSprite < 0 && LogAxis.isRotatedFace(this.logAxis, face);
		this.faceSprite = sprite;
	}

	@Inject(method = "tesselateBottomFace", at = @At("HEAD"))
	private void gaiden$bottom(Block block, double x, double y, double z, int sprite, CallbackInfo ci) {
		this.beginFace(0, sprite);
	}

	@Inject(method = "tesselateTopFace", at = @At("HEAD"))
	private void gaiden$top(Block block, double x, double y, double z, int sprite, CallbackInfo ci) {
		this.beginFace(1, sprite);
	}

	@Inject(method = "tesselateNorthFace", at = @At("HEAD"))
	private void gaiden$north(Block block, double x, double y, double z, int sprite, CallbackInfo ci) {
		this.beginFace(2, sprite);
	}

	@Inject(method = "tesselateSouthFace", at = @At("HEAD"))
	private void gaiden$south(Block block, double x, double y, double z, int sprite, CallbackInfo ci) {
		this.beginFace(3, sprite);
	}

	@Inject(method = "tesselateWestFace", at = @At("HEAD"))
	private void gaiden$west(Block block, double x, double y, double z, int sprite, CallbackInfo ci) {
		this.beginFace(4, sprite);
	}

	@Inject(method = "tesselateEastFace", at = @At("HEAD"))
	private void gaiden$east(Block block, double x, double y, double z, int sprite, CallbackInfo ci) {
		this.beginFace(5, sprite);
	}

	@Redirect(
		method = {"tesselateBottomFace", "tesselateTopFace", "tesselateNorthFace", "tesselateSouthFace", "tesselateWestFace", "tesselateEastFace"},
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/vertex/Tesselator;vertex(DDDDD)V")
	)
	private void gaiden$rotateBark(Tesselator tesselator, double x, double y, double z, double u, double v) {
		if (this.rotateFace) {
			// Position inside the sprite's 16x16 cell (0-1), turned 90 degrees: (s, t) -> (t, 1 - s)
			double cellU = ((this.faceSprite & 15) << 4) / 256.0;
			double cellV = (this.faceSprite & 240) / 256.0;
			double s = (u - cellU) * 16.0;
			double t = (v - cellV) * 16.0;
			u = cellU + t / 16.0;
			v = cellV + (1.0 - s) / 16.0;
		}

		tesselator.vertex(x, y, z, u, v);
	}
}
