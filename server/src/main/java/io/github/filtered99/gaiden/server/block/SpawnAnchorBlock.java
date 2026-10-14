package io.github.filtered99.gaiden.server.block;

import io.github.filtered99.gaiden.server.SpawnAnchorHolder;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.network.packet.ChatMessagePacket;
import net.minecraft.server.entity.mob.player.ServerPlayerEntity;
import net.minecraft.world.World;

/**
 * Right-clicking sets the clicking player's own respawn point to this anchor.
 */
public class SpawnAnchorBlock extends Block {
	public SpawnAnchorBlock(int id, int sprite) {
		super(id, sprite, Material.STONE);
		this.setStrength(10.0F);
		this.setBlastResistance(2000.0F);
		this.setSounds(STONE_SOUNDS);
	}

	@Override
	public boolean use(World world, int x, int y, int z, PlayerEntity player) {
		((SpawnAnchorHolder) player).gaiden$setSpawnAnchor(x, y, z);

		if (player instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) player).networkHandler.sendPacket(new ChatMessagePacket("Spawn point set."));
		}

		return true;
	}
}
