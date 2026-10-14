package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.mob.player.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public interface ClientPlayerEntityAccessor {
	@Accessor("minecraft")
	Minecraft gaiden$getMinecraft();
}
