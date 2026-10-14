package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@ModifyConstant(method = "init", constant = @Constant(stringValue = "Minecraft Minecraft Alpha v1.2.6"))
	private String gaiden$windowTitle(String title) {
		return "Gaiden Edition: Alpha v1.2.7";
	}

	@ModifyConstant(method = "startAndConnect", constant = @Constant(stringValue = "Minecraft"))
	private static String gaiden$frameTitle(String title) {
		return "Gaiden Edition";
	}
}
