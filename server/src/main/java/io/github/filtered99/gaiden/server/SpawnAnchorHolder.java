package io.github.filtered99.gaiden.server;

/**
 * Per-player spawn anchor state, implemented on {@code PlayerEntity} by {@code PlayerEntityMixin}.
 */
public interface SpawnAnchorHolder {
	boolean gaiden$hasSpawnAnchor();

	int gaiden$getSpawnAnchorX();

	int gaiden$getSpawnAnchorY();

	int gaiden$getSpawnAnchorZ();

	void gaiden$setSpawnAnchor(int x, int y, int z);

	void gaiden$clearSpawnAnchor();

	default void gaiden$copySpawnAnchorFrom(SpawnAnchorHolder other) {
		if (other.gaiden$hasSpawnAnchor()) {
			this.gaiden$setSpawnAnchor(other.gaiden$getSpawnAnchorX(), other.gaiden$getSpawnAnchorY(), other.gaiden$getSpawnAnchorZ());
		} else {
			this.gaiden$clearSpawnAnchor();
		}
	}
}
