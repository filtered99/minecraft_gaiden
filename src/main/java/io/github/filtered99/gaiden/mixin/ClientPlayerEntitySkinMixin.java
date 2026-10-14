package io.github.filtered99.gaiden.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.filtered99.gaiden.skin.GaidenSkins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Session;
import net.minecraft.client.entity.mob.player.ClientPlayerEntity;
import net.minecraft.world.World;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntitySkinMixin {
	// The signed-in player's skin comes from their Microsoft account instead of the dead minecraft.net URL
	@Redirect(
		method = "<init>",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/mob/player/ClientPlayerEntity;httpTexture1:Ljava/lang/String;", opcode = Opcodes.PUTFIELD)
	)
	private void gaiden$accountSkin(ClientPlayerEntity player, String legacyUrl, Minecraft minecraft, World world, Session session, int dimension) {
		player.httpTexture1 = GaidenSkins.keyForSession(session);
	}

	// Vanilla leaves the key unset without a name; every player needs a key for the 64x64 model
	@Inject(method = "<init>", at = @At("TAIL"))
	private void gaiden$ensureSkinKey(CallbackInfo ci) {
		ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
		if (player.httpTexture1 == null) {
			player.httpTexture1 = GaidenSkins.NO_SKIN_KEY;
		}
	}
}
