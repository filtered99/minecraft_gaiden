package io.github.filtered99.gaiden.block;

import java.util.Random;

import io.github.filtered99.gaiden.mixin.ClientPlayerEntityAccessor;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.mob.player.ClientPlayerEntity;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.world.World;

/**
 * Right-clicking moves the world spawn point to the anchor. Uses the obsidian sprite (37).
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
		if (!world.isMultiplayer) {
			world.spawnPointX = x;
			world.spawnPointY = y;
			world.spawnPointZ = z;

			if (player instanceof ClientPlayerEntity) {
				((ClientPlayerEntityAccessor) player).gaiden$getMinecraft().gui.addChatMessage("Spawn point set.");
			}
		}

		return true;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		double particleX = (double) x + 0.25D + random.nextDouble() * 0.5D;
		double particleY = (double) y + 1.05D;
		double particleZ = (double) z + 0.25D + random.nextDouble() * 0.5D;

		world.addParticle("portal", particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D);
	}
}
