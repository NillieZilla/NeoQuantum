package com.pixelmoncreate.neoquantum.mixin;

import com.pixelmoncreate.neoquantum.module.attribute.api.IFormattableAttribute;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.ai.attributes.Attribute;

@Mixin(Attribute.class)
public class AttributeMixin implements IFormattableAttribute {

}
