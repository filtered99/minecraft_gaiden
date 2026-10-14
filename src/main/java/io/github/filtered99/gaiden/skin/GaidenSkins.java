package io.github.filtered99.gaiden.skin;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.filtered99.gaiden.mixin.TextureManagerAccessor;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Session;
import net.minecraft.client.render.texture.TextureManager;

/**
 * Account skins for players. Each player's {@code httpTexture1} is set to a key handled here instead of the
 * dead minecraft.net skin URL; Alpha's own HttpTexture thread, texture upload and release are reused.
 */
public final class GaidenSkins {
	public static final Logger LOGGER = LogManager.getLogger("Gaiden Skins");
	private static final String KEY_PREFIX = "gaiden-skin:";
	private static final String PROFILE_KEY = KEY_PREFIX + "profile:";
	private static final String NAME_KEY = KEY_PREFIX + "name:";
	/** Players without an account skin; they render with the default skin in the 64x64 layout. */
	public static final String NO_SKIN_KEY = KEY_PREFIX + "none";
	// Prism passes legacy versions "token:<access token>:<profile id>"; only the profile id is read
	private static final Pattern SESSION_PROFILE_ID = Pattern.compile("token:[^:]+:([0-9a-fA-F]{32})");

	private static final SkinFetcher FETCHER = new SkinFetcher(FabricLoader.getInstance().getGameDir().resolve("gaiden").resolve("skins"));
	private static final Map<String, Boolean> SLIM = new ConcurrentHashMap<>();
	private static int defaultTextureId = -1;

	private GaidenSkins() {
	}

	/** Key for the signed-in player, from the Microsoft account session the launcher passed in. */
	public static String keyForSession(Session session) {
		Matcher matcher = session.id == null ? null : SESSION_PROFILE_ID.matcher(session.id);
		if (matcher != null && matcher.matches()) {
			return PROFILE_KEY + matcher.group(1).toLowerCase();
		}

		LOGGER.info("The launcher did not provide a Microsoft account session; using the default skin. "
			+ "Sign in with a Microsoft account in the launcher to use your Minecraft skin.");
		return NO_SKIN_KEY;
	}

	/** Key for another player, looked up by name (the only identity Alpha multiplayer sends). */
	public static String keyForName(String name) {
		return SkinFetcher.isValidName(name) ? NAME_KEY + name : NO_SKIN_KEY;
	}

	public static boolean isKey(String key) {
		return key != null && key.startsWith(KEY_PREFIX);
	}

	public static boolean isSlim(String key) {
		return key != null && SLIM.getOrDefault(key, false);
	}

	/**
	 * Loads the skin for a key. Runs on Alpha's HttpTexture download thread; returns null to keep the default skin.
	 */
	public static BufferedImage load(String key) {
		try {
			String profileId;
			if (key.startsWith(PROFILE_KEY)) {
				profileId = key.substring(PROFILE_KEY.length());
			} else if (key.startsWith(NAME_KEY)) {
				String name = key.substring(NAME_KEY.length());
				profileId = FETCHER.resolveName(name);
				if (profileId == null) {
					LOGGER.info("No Minecraft profile named {}; using the default skin", name);
					return null;
				}
			} else {
				return null;
			}

			SkinFetcher.SkinInfo info = FETCHER.fetchSkinInfo(profileId);
			if (info == null || info.textureHash == null) {
				return null;
			}

			BufferedImage image = SkinImages.toModernLayout(FETCHER.loadTexture(info.textureHash));
			SLIM.put(key, info.slim);
			return image;
		} catch (Exception e) {
			LOGGER.warn("Could not load skin ({}); using the default skin", e.toString());
			return null;
		}
	}

	/** The default skin converted to the 64x64 layout, used while a skin loads or if it is unavailable. */
	public static int defaultTexture(TextureManager textureManager) {
		if (defaultTextureId < 0) {
			BufferedImage image = null;
			try (InputStream in = ((TextureManagerAccessor) textureManager).gaiden$getTexturePacks().selectedPack.getResource("/mob/char.png")) {
				image = in == null ? null : ImageIO.read(in);
			} catch (Exception e) {
				LOGGER.warn("Could not read the default skin", e);
			}

			if (!SkinImages.hasValidSize(image)) {
				return textureManager.load("/mob/char.png");
			}
			defaultTextureId = textureManager.load(SkinImages.toModernLayout(image));
		}

		return defaultTextureId;
	}
}
