package io.github.filtered99.gaiden;

import net.fabricmc.loader.api.FabricLoader;

/**
 * The Gaiden version shown in game, taken from the mod's own metadata (gradle.properties {@code version}).
 */
public final class GaidenVersion {
	public static final String VERSION = FabricLoader.getInstance().getModContainer("gaiden")
		.map(mod -> mod.getMetadata().getVersion().getFriendlyString())
		.map(version -> version.contains("+") ? version.substring(0, version.indexOf('+')) : version)
		.orElse("unknown");

	/** "Gaiden Edition: Alpha v1.2.8", used for the window, title screen and crash report. */
	public static final String TITLE = "Gaiden Edition: Alpha v" + VERSION;

	/** "Gaiden Edition Alpha v1.2.8", used on the in-game HUD. */
	public static final String HUD = "Gaiden Edition Alpha v" + VERSION;

	private GaidenVersion() {
	}
}
