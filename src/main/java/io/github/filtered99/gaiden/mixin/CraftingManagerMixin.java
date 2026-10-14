package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.filtered99.gaiden.block.GaidenBlocks;

import net.minecraft.block.Block;
import net.minecraft.crafting.CraftingManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Rewrites the recipes registered directly in the CraftingManager constructor and adds
 * Gaiden's extra recipes at the same positions they had in the RetroMCP source, before
 * vanilla sorts the list.
 */
@Mixin(CraftingManager.class)
public abstract class CraftingManagerMixin {
	@Shadow
	abstract void registerShaped(ItemStack result, Object... args);

	@Redirect(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/crafting/CraftingManager;registerShaped(Lnet/minecraft/item/ItemStack;[Ljava/lang/Object;)V"
		)
	)
	private void gaiden$registerShaped(CraftingManager manager, ItemStack result, Object[] args) {
		int id = result.id;

		if (id == Item.BOOK.id) {
			result.size = 2;
		} else if (id == Block.FENCE.id) {
			result.size = 3;
		} else if (id == Block.BOOKSHELF.id) {
			result.size = 2;
		} else if (id == Block.STONE_SLAB.id) {
			result.size = 6;
		} else if (id == Block.LADDER.id) {
			result.size = 3;
		} else if (id == Item.WOODEN_DOOR.id) {
			result.size = 3;
		} else if (id == Item.IRON_DOOR.id) {
			result.size = 3;
		} else if (id == Item.SIGN.id) {
			result.size = 3;
		} else if (id == Block.OAK_STAIRS.id) {
			result.size = 6;
		} else if (id == Block.STONE_STAIRS.id) {
			result.size = 6;
		} else if (id == Block.GLOWSTONE.id) {
			args = new Object[]{"##", "##", '#', Item.GLOWSTONE_DUST};
		} else if (id == Block.WOOL.id) {
			args = new Object[]{"##", "##", '#', Item.STRING};
		} else if (id == Block.STONE_BUTTON.id) {
			args = new Object[]{"#", '#', Block.STONE};
		} else if (id == Block.STONE_PRESSURE_PLATE.id) {
			args = new Object[]{"##", '#', Block.STONE};
		} else if (id == Block.WOODEN_PRESSURE_PLATE.id) {
			args = new Object[]{"##", '#', Block.PLANKS};
		} else if (id == Block.BRICKS.id) {
			this.registerShaped(new ItemStack(Item.BRICK, 4), "#", '#', Block.BRICKS);
		}

		this.registerShaped(result, args);

		if (id == Block.SNOW.id) {
			this.registerShaped(new ItemStack(Item.SNOWBALL, 4), "#", '#', Block.SNOW);
		} else if (id == Block.CLAY.id) {
			this.registerShaped(new ItemStack(Item.CLAY, 4), "#", '#', Block.CLAY);
		} else if (id == Block.WOODEN_PRESSURE_PLATE.id) {
			this.registerShaped(new ItemStack(GaidenBlocks.SPAWN_ANCHOR, 1), "###", "#X#", "###", '#', Block.OBSIDIAN, 'X', Item.COMPASS);
		}
	}
}
