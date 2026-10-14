package io.github.filtered99.gaiden.skin;

import io.github.filtered99.gaiden.mixin.ModelPartAccessor;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.render.model.Polygon;
import net.minecraft.client.render.model.entity.HumanoidModel;

/**
 * The Alpha humanoid model with the 64x64 skin layout: separate left arm and leg textures, outer layers
 * (jacket, sleeves, pants) and optional slim arms. Animation is Alpha's own; the outer layers copy their
 * base part's pose.
 */
public class GaidenPlayerModel extends HumanoidModel {
	public final ModelPart jacket;
	public final ModelPart rightSleeve;
	public final ModelPart leftSleeve;
	public final ModelPart rightPants;
	public final ModelPart leftPants;

	public GaidenPlayerModel(boolean slim) {
		super(0.0F);
		int armWidth = slim ? 3 : 4;
		float armY = slim ? 2.5F : 2.0F;

		this.head = part(0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, 0.0F, 0.0F);
		this.hat = part(32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F, 0.0F, 0.0F);
		this.body = part(16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, 0.0F, 0.0F);
		this.jacket = part(16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F, 0.0F, 0.0F);
		this.rightArm = part(40, 16, slim ? -2.0F : -3.0F, -2.0F, -2.0F, armWidth, 12, 4, 0.0F, -5.0F, armY);
		this.rightSleeve = part(40, 32, slim ? -2.0F : -3.0F, -2.0F, -2.0F, armWidth, 12, 4, 0.25F, -5.0F, armY);
		this.leftArm = part(32, 48, -1.0F, -2.0F, -2.0F, armWidth, 12, 4, 0.0F, 5.0F, armY);
		this.leftSleeve = part(48, 48, -1.0F, -2.0F, -2.0F, armWidth, 12, 4, 0.25F, 5.0F, armY);
		this.rightLeg = part(0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, -2.0F, 12.0F);
		this.rightPants = part(0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, -2.0F, 12.0F);
		this.leftLeg = part(16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, 2.0F, 12.0F);
		this.leftPants = part(0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, 2.0F, 12.0F);
	}

	@Override
	public void render(float walkAnimationProgress, float walkAnimationSpeed, float bob, float yaw, float pitch, float scale) {
		super.render(walkAnimationProgress, walkAnimationSpeed, bob, yaw, pitch, scale);
		this.jacket.render(scale);
		this.rightSleeve.render(scale);
		this.leftSleeve.render(scale);
		this.rightPants.render(scale);
		this.leftPants.render(scale);
	}

	@Override
	public void setupAnimation(float walkAnimationProgress, float walkAnimationSpeed, float bob, float yaw, float pitch, float scale) {
		super.setupAnimation(walkAnimationProgress, walkAnimationSpeed, bob, yaw, pitch, scale);
		copyPose(this.body, this.jacket);
		copyPose(this.rightArm, this.rightSleeve);
		copyPose(this.leftArm, this.leftSleeve);
		copyPose(this.rightLeg, this.rightPants);
		copyPose(this.leftLeg, this.leftPants);
	}

	private static void copyPose(ModelPart from, ModelPart to) {
		to.x = from.x;
		to.y = from.y;
		to.z = from.z;
		to.rotationX = from.rotationX;
		to.rotationY = from.rotationY;
		to.rotationZ = from.rotationZ;
	}

	private static ModelPart part(int u, int v, float x, float y, float z, int sizeX, int sizeY, int sizeZ, float inflate, float pivotX, float pivotY) {
		ModelPart part = new ModelPart(u, v);
		part.addBox(x, y, z, sizeX, sizeY, sizeZ, inflate);
		part.setPos(pivotX, pivotY, 0.0F);

		// ModelPart always maps V over a 32 pixel tall texture; player skins are 64 tall
		for (Polygon polygon : ((ModelPartAccessor) part).gaiden$getPolygons()) {
			for (int i = 0; i < polygon.vertices.length; i++) {
				polygon.vertices[i] = polygon.vertices[i].remap(polygon.vertices[i].u, polygon.vertices[i].v * 0.5F);
			}
		}

		return part;
	}
}
