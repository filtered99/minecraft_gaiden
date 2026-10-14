package io.github.filtered99.gaiden.mixin;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.filtered99.gaiden.skin.GaidenPlayerModel;
import io.github.filtered99.gaiden.skin.GaidenSkins;
import io.github.filtered99.gaiden.skin.SkinModelSelector;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.entity.HumanoidModel;
import net.minecraft.entity.mob.player.PlayerEntity;

/**
 * Renders players with the 64x64 skin model, picking the classic or slim variant per skin.
 */
@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends MobRenderer implements SkinModelSelector {
	@Shadow
	private HumanoidModel player;
	@Unique
	private GaidenPlayerModel classicModel;
	@Unique
	private GaidenPlayerModel slimModel;

	private PlayerRendererMixin(Model model, float shadowSize) {
		super(model, shadowSize);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void gaiden$createSkinModels(CallbackInfo ci) {
		this.classicModel = new GaidenPlayerModel(false);
		this.slimModel = new GaidenPlayerModel(true);
		this.model = this.classicModel;
		this.player = this.classicModel;
	}

	@Override
	public void gaiden$selectSkinModel(String skinKey) {
		GaidenPlayerModel model = GaidenSkins.isSlim(skinKey) ? this.slimModel : this.classicModel;
		this.model = model;
		this.player = model;
	}

	@Inject(method = "render(Lnet/minecraft/entity/mob/player/PlayerEntity;DDDFF)V", at = @At("HEAD"))
	private void gaiden$selectModel(PlayerEntity entity, double dx, double dy, double dz, float yaw, float tickDelta, CallbackInfo ci) {
		this.gaiden$selectSkinModel(entity.httpTexture1);
	}

	// First-person arm: draw the sleeve layer over it
	@Inject(method = "renderRightArm", at = @At("TAIL"))
	private void gaiden$renderSleeve(CallbackInfo ci) {
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		((GaidenPlayerModel) this.player).rightSleeve.render(0.0625F);
		GL11.glPopAttrib();
	}
}
