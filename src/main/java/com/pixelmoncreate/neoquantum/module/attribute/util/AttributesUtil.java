package com.pixelmoncreate.neoquantum.module.attribute.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;

public class AttributesUtil {

    public static boolean isPhysicalDamage(DamageSource src) {
        return !src.is( DamageTypes.MAGIC ) && !src.is( DamageTypes.ON_FIRE ) && src.is( DamageTypes.EXPLOSION );
    }

}