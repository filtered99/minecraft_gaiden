package io.github.filtered99.gaiden.server.mixin;

import java.io.IOException;
import java.util.logging.Logger;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.filtered99.gaiden.server.auth.SessionCheck;

import net.minecraft.network.packet.LoginPacket;
import net.minecraft.server.network.handler.ServerLoginNetworkHandler;

/**
 * The online-mode login check thread. Verifies the player with Mojang's session server instead of the dead
 * minecraft.net checkserver.jsp.
 */
@Mixin(targets = "net.minecraft.server.network.handler.ServerLoginNetworkHandler__15575233")
public abstract class LoginCheckThreadMixin {
	@Unique
	private static final Logger LOGGER = Logger.getLogger("Minecraft");

	@Shadow
	@Final
	ServerLoginNetworkHandler f_55814992;
	@Shadow
	@Final
	LoginPacket f_12137288;

	@Inject(method = "run", at = @At("HEAD"), cancellable = true)
	private void gaiden$checkWithSessionServer(CallbackInfo ci) {
		ci.cancel();
		ServerLoginNetworkHandler handler = this.f_55814992;
		String username = this.f_12137288.username;
		if (!SessionCheck.isValidName(username)) {
			handler.disconnect("Invalid username!");
			return;
		}

		try {
			SessionCheck.Profile profile = SessionCheck.hasJoined(username, ((ServerLoginNetworkHandlerAccessor) handler).gaiden$getKey());
			if (profile != null && profile.name.equalsIgnoreCase(username)) {
				LOGGER.info("Verified " + profile.name + " (" + profile.id + ") with Mojang");
				((ServerLoginNetworkHandlerAccessor) handler).gaiden$setAcceptedLogin(this.f_12137288);
			} else {
				handler.disconnect("Failed to verify username!");
			}
		} catch (IOException e) {
			LOGGER.warning("Could not verify " + username + " with Mojang: " + e.getMessage());
			handler.disconnect("Could not reach Mojang to verify your account. Try again later.");
		}
	}
}
