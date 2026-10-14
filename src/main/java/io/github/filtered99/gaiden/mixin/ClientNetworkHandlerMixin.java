package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.filtered99.gaiden.auth.SessionJoin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.packet.HandshakePacket;
import net.minecraft.network.packet.LoginPacket;

@Mixin(ClientNetworkHandler.class)
public abstract class ClientNetworkHandlerMixin {
	@Shadow
	private Connection connection;
	@Shadow
	private Minecraft minecraft;

	@Shadow
	public abstract void sendPacket(net.minecraft.network.packet.Packet packet);

	// Online-mode servers: join through Mojang's session server instead of the dead minecraft.net joinserver.jsp
	@Inject(method = "handleHandshake", at = @At("HEAD"), cancellable = true)
	private void gaiden$joinWithSessionServer(HandshakePacket packet, CallbackInfo ci) {
		if (packet.key.equals("-")) {
			return;
		}

		ci.cancel();
		String error = SessionJoin.join(this.minecraft.session, packet.key);
		if (error == null) {
			this.sendPacket(new LoginPacket(this.minecraft.session.username, "Password", 6));
		} else {
			this.connection.disconnect(error);
		}
	}
}
