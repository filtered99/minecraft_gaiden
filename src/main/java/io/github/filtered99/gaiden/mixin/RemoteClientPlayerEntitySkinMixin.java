package io.github.filtered99.gaiden.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.filtered99.gaiden.skin.GaidenSkins;

import net.minecraft.client.entity.mob.player.RemoteClientPlayerEntity;
import net.minecraft.world.World;

@Mixin(RemoteClientPlayerEntity.class)
public abstract class RemoteClientPlayerEntitySkinMixin {
	// Other players are looked up by name, the only identity Alpha multiplayer sends
	@Redirect(
		method = "<init>",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/mob/player/RemoteClientPlayerEntity;httpTexture1:Ljava/lang/String;", opcode = Opcodes.PUTFIELD)
	)
	private void gaiden$accountSkin(RemoteClientPlayerEntity player, String legacyUrl, World world, String name) {
		player.httpTexture1 = GaidenSkins.keyForName(name);
	}

	// Vanilla leaves the key unset without a name; every player needs a key for the 64x64 model
	@Inject(method = "<init>", at = @At("TAIL"))
	private void gaiden$ensureSkinKey(CallbackInfo ci) {
		RemoteClientPlayerEntity player = (RemoteClientPlayerEntity) (Object) this;
		if (player.httpTexture1 == null) {
			player.httpTexture1 = GaidenSkins.NO_SKIN_KEY;
		}
	}
}
