package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.gui.screen.menu.TitleScreen;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
	@ModifyConstant(method = "render", constant = @Constant(stringValue = "Minecraft Alpha v1.2.6"))
	private String gaiden$version(String text) {
		return "Gaiden Edition: Alpha v1.2.7";
	}

	@ModifyConstant(method = "render", constant = @Constant(stringValue = "Copyright Mojang Specifications. Do not distribute."))
	private String gaiden$footer(String text) {
		return "Gaiden Edition - Unofficial Minecraft project.";
	}
}
