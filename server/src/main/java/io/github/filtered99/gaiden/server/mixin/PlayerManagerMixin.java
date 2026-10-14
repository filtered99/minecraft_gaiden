package io.github.filtered99.gaiden.server.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.filtered99.gaiden.server.SpawnAnchorHolder;
import io.github.filtered99.gaiden.server.block.GaidenBlocks;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.entity.mob.player.ServerPlayerEntity;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
	@Shadow
	private MinecraftServer server;

	/**
	 * Carries the spawn anchor over to the respawned player and places them on it,
	 * or forgets the anchor if its block is gone.
	 */
	@ModifyExpressionValue(method = "respawn", at = @At(value = "NEW", target = "Lnet/minecraft/server/entity/mob/player/ServerPlayerEntity;"))
	private ServerPlayerEntity gaiden$respawnAtSpawnAnchor(ServerPlayerEntity newPlayer, @Local(argsOnly = true) ServerPlayerEntity oldPlayer) {
		SpawnAnchorHolder anchor = (SpawnAnchorHolder) newPlayer;
		anchor.gaiden$copySpawnAnchorFrom((SpawnAnchorHolder) oldPlayer);

		if (anchor.gaiden$hasSpawnAnchor()
			&& this.server.world.getBlock(anchor.gaiden$getSpawnAnchorX(), anchor.gaiden$getSpawnAnchorY(), anchor.gaiden$getSpawnAnchorZ()) == GaidenBlocks.SPAWN_ANCHOR.id) {
			// Vanilla places respawning players 1.62 above the ground (the player constructor applies the
			// eye height before zeroing it). Respawning flush with the anchor's top left the client stuck.
			newPlayer.setPositionAndAngles(
				(double) anchor.gaiden$getSpawnAnchorX() + 0.5D,
				(double) (anchor.gaiden$getSpawnAnchorY() + 1) + 1.62F,
				(double) anchor.gaiden$getSpawnAnchorZ() + 0.5D,
				0.0F,
				0.0F
			);
		} else {
			anchor.gaiden$clearSpawnAnchor();
		}

		return newPlayer;
	}
}
