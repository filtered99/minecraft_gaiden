package io.github.filtered99.gaiden.server.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.filtered99.gaiden.server.SpawnAnchorHolder;

import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements SpawnAnchorHolder {
	@Unique
	private boolean hasSpawnAnchor = false;
	@Unique
	private int spawnAnchorX;
	@Unique
	private int spawnAnchorY;
	@Unique
	private int spawnAnchorZ;

	@Inject(method = "readCustomNbt", at = @At("TAIL"))
	private void gaiden$readSpawnAnchor(NbtCompound nbt, CallbackInfo ci) {
		this.hasSpawnAnchor = nbt.getInt("HasSpawnAnchor") == 1;

		if (this.hasSpawnAnchor) {
			this.spawnAnchorX = nbt.getInt("SpawnAnchorX");
			this.spawnAnchorY = nbt.getInt("SpawnAnchorY");
			this.spawnAnchorZ = nbt.getInt("SpawnAnchorZ");
		}
	}

	@Inject(method = "writeCustomNbt", at = @At("TAIL"))
	private void gaiden$writeSpawnAnchor(NbtCompound nbt, CallbackInfo ci) {
		nbt.putInt("HasSpawnAnchor", this.hasSpawnAnchor ? 1 : 0);

		if (this.hasSpawnAnchor) {
			nbt.putInt("SpawnAnchorX", this.spawnAnchorX);
			nbt.putInt("SpawnAnchorY", this.spawnAnchorY);
			nbt.putInt("SpawnAnchorZ", this.spawnAnchorZ);
		}
	}

	@Override
	public boolean gaiden$hasSpawnAnchor() {
		return this.hasSpawnAnchor;
	}

	@Override
	public int gaiden$getSpawnAnchorX() {
		return this.spawnAnchorX;
	}

	@Override
	public int gaiden$getSpawnAnchorY() {
		return this.spawnAnchorY;
	}

	@Override
	public int gaiden$getSpawnAnchorZ() {
		return this.spawnAnchorZ;
	}

	@Override
	public void gaiden$setSpawnAnchor(int x, int y, int z) {
		this.hasSpawnAnchor = true;
		this.spawnAnchorX = x;
		this.spawnAnchorY = y;
		this.spawnAnchorZ = z;
	}

	@Override
	public void gaiden$clearSpawnAnchor() {
		this.hasSpawnAnchor = false;
	}
}
