package io.github.filtered99.gaiden.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.filtered99.gaiden.block.GaidenBlocks;

import net.minecraft.block.Block;

@Mixin(Block.class)
public abstract class BlockMixin {
	// Obsidian hardness 10.0 -> 5.0
	@ModifyConstant(
		method = "<clinit>",
		constant = @Constant(floatValue = 10.0F, ordinal = 0),
		slice = @Slice(from = @At(value = "NEW", target = "Lnet/minecraft/block/ObsidianBlock;"))
	)
	private static float gaiden$obsidianHardness(float hardness) {
		return 5.0F;
	}

	// The last vanilla block is assigned right before the block item loop
	@Inject(
		method = "<clinit>",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/block/Block;STONE_BUTTON:Lnet/minecraft/block/Block;",
			opcode = Opcodes.PUTSTATIC,
			shift = At.Shift.AFTER
		)
	)
	private static void gaiden$registerBlocks(CallbackInfo ci) {
		GaidenBlocks.register();
	}
}
