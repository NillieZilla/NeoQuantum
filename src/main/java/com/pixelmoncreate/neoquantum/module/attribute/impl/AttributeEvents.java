package com.pixelmoncreate.neoquantum.module.attribute.impl;

import com.pixelmoncreate.neoquantum.NeoQuantum;
import com.pixelmoncreate.neoquantum.module.attribute.AttributeRegistry;
import com.pixelmoncreate.neoquantum.proxy.Proxy;
import com.pixelmoncreate.neoquantum.util.Const;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map.Entry;

import static java.util.Objects.requireNonNull;

public class AttributeEvents {

    // Fixes attributes which had their base values changed.
    @SubscribeEvent
    public void fixChangedAttributes(PlayerLoggedInEvent e) {
        AttributeMap map = e.getEntity().getAttributes();
        for (Entry<ResourceKey<Attribute>, Attribute> entry : ForgeRegistries.ATTRIBUTES.getEntries()) {
            if (Const.MOD_ID.equals(entry.getKey().location().getNamespace())) {
                requireNonNull(map.getInstance(entry.getValue())).setBaseValue(entry.getValue().getDefaultValue());
            }
        }
        requireNonNull(map.getInstance(ForgeMod.STEP_HEIGHT_ADDITION.get())).setBaseValue(0.6);
    }

    private boolean canBenefitFromDrawSpeed(ItemStack stack) {
        return stack.getItem() instanceof ProjectileWeaponItem || stack.getItem() instanceof TridentItem;
    }

    /**
     * This event handler is the implementation for {@link AttributeRegistry#DRAW_SPEED}.<br>
     * Each full point of draw speed provides an extra using tick per game tick.<br>
     * Each partial point of draw speed provides an extra using tick periodically.
     */
    @SubscribeEvent
    public void drawSpeed(LivingEntityUseItemEvent.Tick e) {
        if (e.getEntity() instanceof Player player) {
            double t = requireNonNull(player.getAttribute(AttributeRegistry.DRAW_SPEED.get())).getValue() - 1;
            if (t == 0 || !this.canBenefitFromDrawSpeed(e.getItem())) return;

            // Handle negative draw speed.
            int offset = -1;
            if (t < 0) {
                offset = 1;
                t = -t;
            }

            while (t > 1) { // Every 100% triggers an immediate extra tick
                e.setDuration(e.getDuration() + offset);
                t--;
            }

            if (t > 0.5F) { // Special case 0.5F so that values in (0.5, 1) don't round to 1.
                if (e.getEntity().tickCount % 2 == 0) e.setDuration(e.getDuration() + offset);
                t -= 0.5F;
            }

            int mod = (int) Math.floor(1 / Math.min(1, t));
            if (e.getEntity().tickCount % mod == 0) e.setDuration(e.getDuration() + offset);
            t--;
        }
    }

    /**
     * This event handler manages the Life Steal and Overheal attributes.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void lifeStealOverheal(LivingHurtEvent e) {
        if (e.getSource().getDirectEntity() instanceof LivingEntity attacker) {
            float lifesteal = (float) attacker.getAttributeValue(AttributeRegistry.LIFE_STEAL.get());
            float dmg = Math.min(e.getAmount(), e.getEntity().getHealth());
            if (lifesteal > 0.001) {
                attacker.heal(dmg * lifesteal);
            }
            float overheal = (float) attacker.getAttributeValue(AttributeRegistry.OVERHEAL.get());
            float maxOverheal = attacker.getMaxHealth() * 0.5F;
            if (overheal > 0 && attacker.getAbsorptionAmount() < maxOverheal) {
                attacker.setAbsorptionAmount(Math.min(maxOverheal, attacker.getAbsorptionAmount() + dmg * overheal));
            }
        }
    }

    /**
     * Recursion guard for {@link #meleeDamageAttributes(LivingAttackEvent)}.<br>
     * Doesn't need to be ThreadLocal as attack logic is main-thread only.
     */
    private static boolean noRecurse = false;

    /**
     * Applies the following melee damage attributes:<br>
     * <ul>
     * <li>{@link AttributeRegistry#CURRENT_HP_DAMAGE}</li>
     * <li>{@link AttributeRegistry#FIRE_DAMAGE}</li>
     * <li>{@link AttributeRegistry#COLD_DAMAGE}</li>
     * </ul>
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void meleeDamageAttributes(LivingAttackEvent e) {
        if (e.getEntity().level().isClientSide) return;
        if (noRecurse) return;
        noRecurse = true;
        if (e.getSource().getDirectEntity() instanceof LivingEntity attacker) {
            float hpDmg = (float) attacker.getAttributeValue(AttributeRegistry.CURRENT_HP_DAMAGE.get());
            float fireDmg = (float) attacker.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
            float coldDmg = (float) attacker.getAttributeValue(AttributeRegistry.COLD_DAMAGE.get());
            LivingEntity target = e.getEntity();
            int time = target.invulnerableTime;
            target.invulnerableTime = 0;
            if (hpDmg > 0.001 && Proxy.localAtkStrength >= 0.85F) {
                target.hurt(src(attacker), Proxy.localAtkStrength * hpDmg * target.getHealth());
            }
            target.invulnerableTime = 0;
            if (fireDmg > 0.001 && Proxy.localAtkStrength >= 0.55F) {
                target.hurt(src(attacker), Proxy.localAtkStrength * fireDmg);
                target.setRemainingFireTicks(target.getRemainingFireTicks() + (int) (10 * fireDmg));
            }
            target.invulnerableTime = 0;
            if (coldDmg > 0.001 && Proxy.localAtkStrength >= 0.55F) {
                target.hurt(src(attacker), Proxy.localAtkStrength * coldDmg);
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (15 * coldDmg), Mth.floor(coldDmg / 5)));
            }
            target.invulnerableTime = time;
        }
        noRecurse = false;
    }

    public DamageSource src(LivingEntity entity) {
        return entity instanceof Player p ? p.damageSources().playerAttack(p) : entity.damageSources().mobAttack(entity);
    }

    /**
     * Handles {@link AttributeRegistry#CRIT_CHANCE} and {@link AttributeRegistry#CRIT_DAMAGE}
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void neoCriticalStrike(LivingHurtEvent e) {
        LivingEntity attacker = e.getSource().getEntity() instanceof LivingEntity le ? le : null;
        if (attacker == null) return;

        double critChance = attacker.getAttributeValue(AttributeRegistry.CRIT_CHANCE.get());
        float critDmg = (float) attacker.getAttributeValue(AttributeRegistry.CRIT_DAMAGE.get());

        RandomSource rand = e.getEntity().random;

        float critMult = 1.0F;

        while (rand.nextFloat() <= critChance && critDmg > 1.0F) {
            critChance--;
            critMult *= critDmg;
            critDmg *= 0.85F;
        }

        e.setAmount(e.getAmount() * critMult);

    }

    /**
     * Handles {@link AttributeRegistry#CRIT_DAMAGE}'s interactions with vanilla critical strikes.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void vanillaCritDmg(CriticalHitEvent e) {
        float critDmg = (float) e.getEntity().getAttributeValue(AttributeRegistry.CRIT_DAMAGE.get());
        if (e.isVanillaCritical()) {
            e.setDamageModifier(Math.max(e.getDamageModifier(), critDmg));
        }
    }

    /**
     * Handles {@link AttributeRegistry#MINING_SPEED}
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void breakSpd(BreakSpeed e) {
        e.setNewSpeed(e.getNewSpeed() * (float) e.getEntity().getAttributeValue(AttributeRegistry.MINING_SPEED.get()));
    }

    /**
     * This event, and {@linkplain #mobXp(LivingExperienceDropEvent) the event below} handle {@link AttributeRegistry#EXPERIENCE_GAINED}
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void blockBreak(BreakEvent e) {
        double xpMult = e.getPlayer().getAttributeValue(AttributeRegistry.EXPERIENCE_GAINED.get());
        e.setExpToDrop((int) (e.getExpToDrop() * xpMult));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void mobXp(LivingExperienceDropEvent e) {
        Player player = e.getAttackingPlayer();
        if (player == null) return;
        double xpMult = e.getAttackingPlayer().getAttributeValue(AttributeRegistry.EXPERIENCE_GAINED.get());
        e.setDroppedExperience((int) (e.getDroppedExperience() * xpMult));
    }

    /**
     * Handles {@link AttributeRegistry#HEALING_RECEIVED}
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void heal(LivingHealEvent e) {
        float factor = (float) e.getEntity().getAttributeValue(AttributeRegistry.HEALING_RECEIVED.get());
        e.setAmount(e.getAmount() * factor);
        if (e.getAmount() <= 0) e.setCanceled(true);
    }

    /**
     * Handles {@link AttributeRegistry#ARROW_DAMAGE} and {@link AttributeRegistry#ARROW_VELOCITY}
     */
    @SubscribeEvent
    public void arrow(EntityJoinLevelEvent e) {
        if (e.getEntity() instanceof AbstractArrow arrow) {
            if (arrow.level().isClientSide || arrow.getPersistentData().getBoolean("attributeslib.arrow.done")) return;
            if (arrow.getOwner() instanceof LivingEntity le) {
                arrow.setBaseDamage(arrow.getBaseDamage() * le.getAttributeValue(AttributeRegistry.ARROW_DAMAGE.get()));
                arrow.setDeltaMovement(arrow.getDeltaMovement().scale(le.getAttributeValue(AttributeRegistry.ARROW_VELOCITY.get())));
            }
            arrow.getPersistentData().putBoolean("attributeslib.arrow.done", true);
        }
    }

    /**
     * Fix for <a href="https://github.com/MinecraftForge/MinecraftForge/issues/9370">...</a>
     */
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void fixMCF9370(ProjectileImpactEvent e) {
        if (e.isCanceled()) {
            Entity target = e.getRayTraceResult() instanceof EntityHitResult entRes ? entRes.getEntity() : null;
            Projectile proj = e.getProjectile();
            if (target != null && proj instanceof AbstractArrow arrow && arrow.getPierceLevel() > 0) {
                if (arrow.piercingIgnoreEntityIds == null) {
                    arrow.piercingIgnoreEntityIds = new IntOpenHashSet(arrow.getPierceLevel());
                }
                arrow.piercingIgnoreEntityIds.add(target.getId());
            }
        }
    }

    /**
     * Adds a fake modifier to show Attack Range to weapons with Attack Damage.
     */
    //@SubscribeEvent
    //public void affixModifiers(ItemAttributeModifierEvent e) {
    //    boolean hasBaseAD = e.getModifiers().get(Attributes.ATTACK_DAMAGE).stream().filter(m -> ((IFormattableAttribute) Attributes.ATTACK_DAMAGE).getBaseUUID().equals(m.getId())).findAny().isPresent();
    //    if (hasBaseAD) {
    //        boolean hasBaseAR = e.getModifiers().get(ForgeMod.ENTITY_REACH.get()).stream().filter(m -> ((IFormattableAttribute) ForgeMod.ENTITY_REACH.get()).getBaseUUID().equals(m.getId())).findAny().isPresent();
    //        if (!hasBaseAR) {
    //            e.addModifier(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(AttributeHelper.BASE_ATTACK_RANGE, () -> "attributeslib:fake_base_range", 0, Operation.ADDITION));
    //        }
    //    }
    //}
}
