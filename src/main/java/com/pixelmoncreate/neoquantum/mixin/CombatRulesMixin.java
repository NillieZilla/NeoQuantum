package com.pixelmoncreate.neoquantum.mixin;

import com.pixelmoncreate.neoquantum.module.attribute.asm.ALCombatRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.damagesource.CombatRules;

@Mixin(CombatRules.class)
public class CombatRulesMixin {

    /**
     * @see {@link ALCombatRules#getDamageAfterProtection(net.minecraft.world.entity.LivingEntity, net.minecraft.world.damagesource.DamageSource, float, float)}
     */
    @Overwrite
    public static float getDamageAfterMagicAbsorb(float damage, float protPoints) {
        return damage * ALCombatRules.getProtDamageReduction(protPoints);
    }

}
