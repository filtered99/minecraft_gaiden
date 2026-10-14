package io.github.filtered99.gaiden.auth;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;

import io.github.filtered99.gaiden.skin.GaidenSkins;

import net.minecraft.client.Session;

/**
 * Online-mode login for Alpha servers. Alpha told minecraft.net's joinserver.jsp (long gone) that it was joining a
 * server; this sends the same notice to Mojang's current session server instead. The access token from the
 * launcher's Microsoft session is sent only to sessionserver.mojang.com, never to the game server.
 */
public final class SessionJoin {
	private static final String JOIN_URL = "https://sessionserver.mojang.com/session/minecraft/join";
	// Prism passes legacy versions "token:<access token>:<profile id>"
	private static final Pattern SESSION = Pattern.compile("token:([^:]+):([0-9a-fA-F]{32})");
	private static final Pattern SERVER_ID = Pattern.compile("[0-9A-Za-z\\-_]{1,64}");

	private SessionJoin() {
	}

	/**
	 * Tells Mojang's session server the player is joining the server with this id.
	 *
	 * @return null on success, otherwise the reason to show the player
	 */
	public static String join(Session session, String serverId) {
		Matcher matcher = session.id == null ? null : SESSION.matcher(session.id);
		if (matcher == null || !matcher.matches()) {
			return "This server checks Minecraft accounts. Sign in with a Microsoft account in your launcher to join.";
		}
		if (!SERVER_ID.matcher(serverId).matches()) {
			return "The server sent an invalid login request.";
		}

		JsonObject body = new JsonObject();
		body.addProperty("accessToken", matcher.group(1));
		body.addProperty("selectedProfile", matcher.group(2).toLowerCase());
		body.addProperty("serverId", serverId);

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(JOIN_URL).openConnection();
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(10000);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("User-Agent", "MinecraftGaiden");
			try {
				try (OutputStream out = connection.getOutputStream()) {
					out.write(body.toString().getBytes(StandardCharsets.UTF_8));
				}

				int status = connection.getResponseCode();
				if (status == 204 || status == 200) {
					return null;
				}
				if (status == 403 || status == 401) {
					return "Your Minecraft session has expired. Restart the game from your launcher.";
				}
				GaidenSkins.LOGGER.warn("Mojang session server answered the join request with HTTP {}", status);
				return "Could not log in with Mojang (HTTP " + status + ").";
			} finally {
				connection.disconnect();
			}
		} catch (IOException e) {
			GaidenSkins.LOGGER.warn("Could not reach Mojang's session server: {}", e.toString());
			return "Could not reach Mojang's session server.";
		}
	}
}
