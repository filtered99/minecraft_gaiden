package io.github.filtered99.gaiden.mixin;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.filtered99.gaiden.GaidenVersion;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow
	public java.awt.Canvas canvas;

	@ModifyConstant(method = "init", constant = @Constant(stringValue = "Minecraft Minecraft Alpha v1.2.6"))
	private String gaiden$windowTitle(String title) {
		return GaidenVersion.TITLE;
	}

	@ModifyConstant(method = "startAndConnect", constant = @Constant(stringValue = "Minecraft"))
	private static String gaiden$frameTitle(String title) {
		return "Gaiden Edition";
	}

	// When the game is drawn inside a launcher's window (Fabric's applet frame, titled "Minecraft"), Display.setTitle
	// has no effect, so rename the window that holds the game canvas
	@Inject(method = "init", at = @At("TAIL"))
	private void gaiden$titleHostWindow(CallbackInfo ci) {
		Component component = this.canvas;
		while (component != null && !(component instanceof Frame)) {
			component = component.getParent();
		}

		if (component != null) {
			Frame frame = (Frame) component;
			EventQueue.invokeLater(() -> frame.setTitle(GaidenVersion.TITLE));
		}
	}
}
