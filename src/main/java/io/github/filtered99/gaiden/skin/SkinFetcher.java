package io.github.filtered99.gaiden.skin;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Looks up Java Edition skins through Mojang's official services and caches the results on disk.
 * <p>
 * Profiles come from the session server and are only trusted if their {@code textures} property carries a
 * valid Mojang signature (keys from the official public key endpoint). Skin images are only downloaded from
 * {@code textures.minecraft.net}. All methods block, so call them off the render thread.
 */
public class SkinFetcher {
	private static final String NAME_LOOKUP = "https://api.mojang.com/users/profiles/minecraft/";
	private static final String PROFILE_LOOKUP = "https://sessionserver.mojang.com/session/minecraft/profile/";
	private static final String PUBLIC_KEYS = "https://api.minecraftservices.com/publickeys";
	private static final String TEXTURE_HOST = "textures.minecraft.net";

	private static final Pattern NAME = Pattern.compile("[A-Za-z0-9_]{1,16}");
	private static final Pattern PROFILE_ID = Pattern.compile("[0-9a-f]{32}");
	private static final Pattern TEXTURE_PATH = Pattern.compile("/texture/([0-9a-f]{16,128})");

	private static final long NAME_TTL = 24L * 60 * 60 * 1000;
	private static final long PROFILE_TTL = 30L * 60 * 1000;
	private static final long KEYS_TTL = 7L * 24 * 60 * 60 * 1000;
	private static final int MAX_JSON_BYTES = 64 * 1024;
	private static final int MAX_TEXTURE_BYTES = 256 * 1024;

	private final Path cacheDir;

	public SkinFetcher(Path cacheDir) {
		this.cacheDir = cacheDir;
	}

	/** A player's skin: the texture hash on textures.minecraft.net (null if they use a default skin) and model. */
	public static final class SkinInfo {
		public final String textureHash;
		public final boolean slim;

		SkinInfo(String textureHash, boolean slim) {
			this.textureHash = textureHash;
			this.slim = slim;
		}
	}

	public static boolean isValidName(String name) {
		return name != null && NAME.matcher(name).matches();
	}

	public static boolean isValidProfileId(String id) {
		return id != null && PROFILE_ID.matcher(id).matches();
	}

	/** Returns the profile id for a player name, or null if no Java Edition profile has that name. */
	public String resolveName(String name) throws IOException {
		if (!isValidName(name)) {
			return null;
		}

		Path file = this.cacheDir.resolve("names").resolve(name.toLowerCase(Locale.ROOT) + ".json");
		JsonObject cached = readJson(file);
		if (cached != null && !isExpired(cached, NAME_TTL)) {
			return cachedProfileId(cached);
		}

		try {
			Response response = get(NAME_LOOKUP + name, MAX_JSON_BYTES);
			String id = null;
			if (response.status == 200) {
				JsonObject json = parse(response.body);
				id = json.has("id") ? json.get("id").getAsString().toLowerCase(Locale.ROOT) : null;
				if (!isValidProfileId(id)) {
					throw new IOException("Unexpected profile id format");
				}
			} else if (response.status != 204 && response.status != 404) {
				throw new IOException("Name lookup returned HTTP " + response.status);
			}

			JsonObject entry = new JsonObject();
			if (id != null) {
				entry.addProperty("id", id);
			}
			entry.addProperty("time", System.currentTimeMillis());
			writeAtomically(file, entry.toString().getBytes(StandardCharsets.UTF_8));
			return id;
		} catch (IOException e) {
			if (cached != null) {
				return cachedProfileId(cached);
			}
			throw e;
		}
	}

	/** Returns the profile's skin info, or null if the profile does not exist. */
	public SkinInfo fetchSkinInfo(String profileId) throws IOException {
		if (!isValidProfileId(profileId)) {
			throw new IllegalArgumentException("Invalid profile id");
		}

		Path file = this.cacheDir.resolve("profiles").resolve(profileId + ".json");
		JsonObject cached = readJson(file);
		if (cached != null && !isExpired(cached, PROFILE_TTL)) {
			return cachedSkinInfo(cached);
		}

		try {
			Response response = get(PROFILE_LOOKUP + profileId + "?unsigned=false", MAX_JSON_BYTES);
			if (response.status == 204 || response.status == 404) {
				return null;
			}
			if (response.status != 200) {
				throw new IOException("Profile lookup returned HTTP " + response.status);
			}

			SkinInfo info = this.readSignedTextures(parse(response.body), profileId);
			JsonObject entry = new JsonObject();
			if (info.textureHash != null) {
				entry.addProperty("hash", info.textureHash);
			}
			entry.addProperty("slim", info.slim);
			entry.addProperty("time", System.currentTimeMillis());
			writeAtomically(file, entry.toString().getBytes(StandardCharsets.UTF_8));
			return info;
		} catch (IOException e) {
			if (cached != null) {
				return cachedSkinInfo(cached);
			}
			throw e;
		}
	}

	/** Returns the skin image for a texture hash, from the cache or textures.minecraft.net. */
	public BufferedImage loadTexture(String textureHash) throws IOException {
		if (textureHash == null || !textureHash.matches("[0-9a-f]{16,128}")) {
			throw new IllegalArgumentException("Invalid texture hash");
		}

		Path file = this.cacheDir.resolve("textures").resolve(textureHash + ".png");
		if (Files.isRegularFile(file)) {
			BufferedImage image = readSkinImage(Files.readAllBytes(file));
			if (image != null) {
				return image;
			}
			Files.deleteIfExists(file);
		}

		Response response = get("https://" + TEXTURE_HOST + "/texture/" + textureHash, MAX_TEXTURE_BYTES);
		if (response.status != 200) {
			throw new IOException("Texture download returned HTTP " + response.status);
		}

		BufferedImage image = readSkinImage(response.body);
		if (image == null) {
			throw new IOException("Downloaded texture is not a 64x64 or 64x32 PNG");
		}

		writeAtomically(file, response.body);
		return image;
	}

	private SkinInfo readSignedTextures(JsonObject profile, String profileId) throws IOException {
		String id = profile.has("id") ? profile.get("id").getAsString().toLowerCase(Locale.ROOT) : null;
		if (!profileId.equals(id)) {
			throw new IOException("Profile response is for a different player");
		}

		JsonArray properties = profile.has("properties") ? profile.getAsJsonArray("properties") : new JsonArray();
		for (JsonElement element : properties) {
			JsonObject property = element.getAsJsonObject();
			if (!"textures".equals(property.get("name").getAsString())) {
				continue;
			}

			String value = property.get("value").getAsString();
			if (!property.has("signature") || !this.isSignedByMojang(value, property.get("signature").getAsString())) {
				throw new IOException("Textures property is missing a valid Mojang signature");
			}

			JsonObject textures = parse(Base64.getDecoder().decode(value));
			String texturesProfileId = textures.has("profileId") ? textures.get("profileId").getAsString().toLowerCase(Locale.ROOT) : null;
			if (!profileId.equals(texturesProfileId)) {
				throw new IOException("Textures property is for a different player");
			}

			JsonObject skin = textures.has("textures") && textures.getAsJsonObject("textures").has("SKIN")
				? textures.getAsJsonObject("textures").getAsJsonObject("SKIN")
				: null;
			if (skin == null) {
				return new SkinInfo(null, false);
			}

			boolean slim = skin.has("metadata")
				&& skin.getAsJsonObject("metadata").has("model")
				&& "slim".equals(skin.getAsJsonObject("metadata").get("model").getAsString());
			return new SkinInfo(textureHashFromUrl(skin.get("url").getAsString()), slim);
		}

		return new SkinInfo(null, false);
	}

	static String textureHashFromUrl(String url) throws IOException {
		URI uri = URI.create(url);
		String scheme = uri.getScheme();
		if (!("http".equals(scheme) || "https".equals(scheme)) || !TEXTURE_HOST.equalsIgnoreCase(uri.getHost())
			|| uri.getPort() != -1 || uri.getQuery() != null || uri.getPath() == null) {
			throw new IOException("Skin URL is not on " + TEXTURE_HOST);
		}

		Matcher matcher = TEXTURE_PATH.matcher(uri.getPath());
		if (!matcher.matches()) {
			throw new IOException("Unexpected skin URL path");
		}

		return matcher.group(1);
	}

	private boolean isSignedByMojang(String value, String signature) throws IOException {
		byte[] signatureBytes = Base64.getDecoder().decode(signature);
		for (PublicKey key : this.loadPublicKeys()) {
			try {
				Signature verifier = Signature.getInstance("SHA1withRSA");
				verifier.initVerify(key);
				verifier.update(value.getBytes(StandardCharsets.UTF_8));
				if (verifier.verify(signatureBytes)) {
					return true;
				}
			} catch (Exception ignored) {
				// try the next key
			}
		}

		return false;
	}

	private List<PublicKey> loadPublicKeys() throws IOException {
		Path file = this.cacheDir.resolve("publickeys.json");
		JsonObject cached = readJson(file);
		JsonObject keys = cached;
		if (cached == null || isExpired(cached, KEYS_TTL)) {
			try {
				Response response = get(PUBLIC_KEYS, MAX_JSON_BYTES);
				if (response.status != 200) {
					throw new IOException("Public key lookup returned HTTP " + response.status);
				}

				keys = parse(response.body);
				keys.addProperty("time", System.currentTimeMillis());
				writeAtomically(file, keys.toString().getBytes(StandardCharsets.UTF_8));
			} catch (IOException e) {
				if (cached == null) {
					throw e;
				}
			}
		}

		List<PublicKey> result = new ArrayList<>();
		if (keys.has("profilePropertyKeys")) {
			for (JsonElement element : keys.getAsJsonArray("profilePropertyKeys")) {
				try {
					byte[] der = Base64.getDecoder().decode(element.getAsJsonObject().get("publicKey").getAsString());
					result.add(KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der)));
				} catch (Exception ignored) {
					// skip malformed keys
				}
			}
		}

		if (result.isEmpty()) {
			throw new IOException("No Mojang profile property keys available");
		}
		return result;
	}

	private static BufferedImage readSkinImage(byte[] bytes) {
		// PNG signature
		if (bytes.length < 8 || (bytes[0] & 0xFF) != 0x89 || bytes[1] != 'P' || bytes[2] != 'N' || bytes[3] != 'G') {
			return null;
		}

		try {
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
			return SkinImages.hasValidSize(image) ? image : null;
		} catch (IOException e) {
			return null;
		}
	}

	private static String cachedProfileId(JsonObject cached) {
		String id = cached.has("id") ? cached.get("id").getAsString() : null;
		return isValidProfileId(id) ? id : null;
	}

	private static SkinInfo cachedSkinInfo(JsonObject cached) {
		String hash = cached.has("hash") ? cached.get("hash").getAsString() : null;
		return new SkinInfo(hash, cached.has("slim") && cached.get("slim").getAsBoolean());
	}

	private static boolean isExpired(JsonObject cached, long ttl) {
		return !cached.has("time") || System.currentTimeMillis() - cached.get("time").getAsLong() > ttl;
	}

	private static JsonObject readJson(Path file) {
		try {
			return Files.isRegularFile(file) ? parse(Files.readAllBytes(file)) : null;
		} catch (Exception e) {
			return null;
		}
	}

	private static JsonObject parse(byte[] bytes) throws IOException {
		try {
			return JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8)).getAsJsonObject();
		} catch (RuntimeException e) {
			throw new IOException("Malformed JSON", e);
		}
	}

	private static void writeAtomically(Path file, byte[] bytes) throws IOException {
		Files.createDirectories(file.getParent());
		Path temp = Files.createTempFile(file.getParent(), file.getFileName().toString(), ".tmp");
		try {
			Files.write(temp, bytes);
			Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
		} finally {
			Files.deleteIfExists(temp);
		}
	}

	private static final class Response {
		final int status;
		final byte[] body;

		Response(int status, byte[] body) {
			this.status = status;
			this.body = body;
		}
	}

	private static Response get(String url, int maxBytes) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(10000);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestProperty("User-Agent", "MinecraftGaiden");
		try {
			int status = connection.getResponseCode();
			if (status != 200) {
				return new Response(status, new byte[0]);
			}

			try (InputStream in = connection.getInputStream()) {
				byte[] buffer = new byte[maxBytes + 1];
				int total = 0;
				int read;
				while (total <= maxBytes && (read = in.read(buffer, total, buffer.length - total)) != -1) {
					total += read;
				}
				if (total > maxBytes) {
					throw new IOException("Response from " + connection.getURL().getHost() + " is too large");
				}

				byte[] body = new byte[total];
				System.arraycopy(buffer, 0, body, 0, total);
				return new Response(status, body);
			}
		} finally {
			connection.disconnect();
		}
	}
}
