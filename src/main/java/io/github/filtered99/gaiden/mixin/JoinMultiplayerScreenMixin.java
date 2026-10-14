package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.gui.screen.menu.multiplayer.JoinMultiplayerScreen;

@Mixin(JoinMultiplayerScreen.class)
public abstract class JoinMultiplayerScreenMixin {
	@ModifyConstant(method = "render", constant = @Constant(stringValue = "Minecraft Multiplayer is currently not finished, but there"))
	private String gaiden$multiplayerText(String text) {
		return "Gaiden Edition multiplayer is currently not finished, but there";
	}
}
