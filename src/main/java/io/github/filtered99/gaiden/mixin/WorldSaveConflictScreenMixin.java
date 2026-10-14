package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.gui.screen.menu.world.WorldSaveConflictScreen;

@Mixin(WorldSaveConflictScreen.class)
public abstract class WorldSaveConflictScreenMixin {
	@ModifyConstant(method = "render", constant = @Constant(stringValue = "Minecraft detected a conflict in the level save data."))
	private String gaiden$conflictText(String text) {
		return "Gaiden Edition detected a conflict in the level save data.";
	}
}
