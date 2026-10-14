package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;

/**
 * A boat broken by hand drops a boat instead of 3 planks and 2 sticks.
 */
@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin {
	@Redirect(method = "takeDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;dropItem(IIF)Lnet/minecraft/entity/ItemEntity;"))
	private ItemEntity gaiden$skipMaterialDrops(BoatEntity boat, int item, int amount, float offsetY) {
		return null;
	}

	@Inject(method = "takeDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;remove()V"))
	private void gaiden$dropBoat(Entity source, int amount, CallbackInfoReturnable<Boolean> cir) {
		((Entity) (Object) this).dropItem(Item.BOAT.id, 1, 0.0F);
	}
}
