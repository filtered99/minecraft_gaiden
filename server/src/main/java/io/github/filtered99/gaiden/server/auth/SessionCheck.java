package io.github.filtered99.gaiden.server.auth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Online-mode login check for the Alpha server. Alpha asked minecraft.net's checkserver.jsp (long gone) whether a
 * player had joined; this asks Mojang's current session server instead.
 */
public final class SessionCheck {
	private static final String HAS_JOINED_URL = "https://sessionserver.mojang.com/session/minecraft/hasJoined";
	private static final Pattern NAME = Pattern.compile("[A-Za-z0-9_]{1,16}");
	private static final Pattern PROFILE_ID = Pattern.compile("[0-9a-f]{32}");

	private SessionCheck() {
	}

	/** The verified profile of a player who joined through the session server. */
	public static final class Profile {
		public final String id;
		public final String name;

		Profile(String id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	public static boolean isValidName(String name) {
		return name != null && NAME.matcher(name).matches();
	}

	/**
	 * @return the verified profile, or null if Mojang has no record of this player joining with this server id
	 * @throws IOException if the session server could not be reached or answered unexpectedly
	 */
	public static Profile hasJoined(String username, String serverId) throws IOException {
		String url = HAS_JOINED_URL + "?username=" + URLEncoder.encode(username, "UTF-8") + "&serverId=" + URLEncoder.encode(serverId, "UTF-8");
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(10000);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestProperty("User-Agent", "MinecraftGaiden");
		try {
			int status = connection.getResponseCode();
			if (status == 204) {
				return null;
			}
			if (status != 200) {
				throw new IOException("session server answered HTTP " + status);
			}

			ByteArrayOutputStream body = new ByteArrayOutputStream();
			try (InputStream in = connection.getInputStream()) {
				byte[] buffer = new byte[4096];
				int read;
				while ((read = in.read(buffer)) != -1) {
					body.write(buffer, 0, read);
					if (body.size() > 64 * 1024) {
						throw new IOException("session server response too large");
					}
				}
			}

			JsonObject json = JsonParser.parseString(new String(body.toByteArray(), StandardCharsets.UTF_8)).getAsJsonObject();
			String id = json.has("id") ? json.get("id").getAsString() : null;
			String name = json.has("name") ? json.get("name").getAsString() : null;
			if (id == null || !PROFILE_ID.matcher(id).matches() || !isValidName(name)) {
				throw new IOException("unexpected session server response");
			}
			return new Profile(id, name);
		} catch (RuntimeException e) {
			throw new IOException("malformed session server response", e);
		} finally {
			connection.disconnect();
		}
	}
}
