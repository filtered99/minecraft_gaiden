package io.github.filtered99.gaiden.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.render.model.Polygon;

@Mixin(ModelPart.class)
public interface ModelPartAccessor {
	@Accessor("polygons")
	Polygon[] gaiden$getPolygons();
}
