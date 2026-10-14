package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import io.github.filtered99.gaiden.GaidenVersion;

import net.minecraft.client.CrashReportPanel;

@Mixin(CrashReportPanel.class)
public abstract class CrashReportPanelMixin {
	@ModifyConstant(method = "<init>", constant = @Constant(stringValue = "Minecraft: Minecraft Alpha v1.2.6\n"))
	private String gaiden$version(String text) {
		return GaidenVersion.TITLE + "\n";
	}

	@ModifyConstant(method = "<init>", constant = @Constant(stringValue = "Minecraft was unable to start because it failed to find an accelerated OpenGL mode.\n"))
	private String gaiden$openGlError(String text) {
		return "Gaiden Edition was unable to start because it failed to find an accelerated OpenGL mode.\n";
	}

	@ModifyConstant(method = "<init>", constant = @Constant(stringValue = "      Minecraft has crashed!      \n"))
	private String gaiden$crashedHeader(String text) {
		return "      Gaiden Edition has crashed!      \n";
	}

	@ModifyConstant(method = "<init>", constant = @Constant(stringValue = "Minecraft has stopped running because it encountered a problem.\n"))
	private String gaiden$crashedBody(String text) {
		return "Gaiden Edition has stopped running because it encountered a problem.\n";
	}

	@ModifyConstant(method = "<init>", constant = @Constant(stringValue = "If you wish to report this, please copy this entire text and email it to support@mojang.com.\n"))
	private String gaiden$reportInstructions(String text) {
		return "If you wish to report this, please copy this entire text when reporting the issue.\n";
	}
}
