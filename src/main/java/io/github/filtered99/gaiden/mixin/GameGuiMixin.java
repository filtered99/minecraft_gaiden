package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import io.github.filtered99.gaiden.GaidenVersion;

import net.minecraft.client.gui.GameGui;

@Mixin(GameGui.class)
public abstract class GameGuiMixin {
	@ModifyConstant(method = "render", constant = @Constant(stringValue = "Minecraft Alpha v1.2.6 ("))
	private String gaiden$debugVersion(String text) {
		return GaidenVersion.HUD + " (";
	}

	@ModifyConstant(method = "render", constant = @Constant(stringValue = "Minecraft Alpha v1.2.6"))
	private String gaiden$version(String text) {
		return GaidenVersion.HUD;
	}
}
