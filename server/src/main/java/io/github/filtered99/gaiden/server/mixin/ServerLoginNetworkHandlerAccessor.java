package io.github.filtered99.gaiden.server.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.packet.LoginPacket;
import net.minecraft.server.network.handler.ServerLoginNetworkHandler;

@Mixin(ServerLoginNetworkHandler.class)
public interface ServerLoginNetworkHandlerAccessor {
	@Accessor("key")
	String gaiden$getKey();

	// The main thread accepts a login once this is set, as vanilla does after a successful check
	@Accessor("packet")
	void gaiden$setAcceptedLogin(LoginPacket packet);
}
