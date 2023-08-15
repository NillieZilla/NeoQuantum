package com.pixelmoncreate.neoquantum.module.attribute;

import com.pixelmoncreate.neoquantum.module.attribute.impl.PercentBasedAttribute;
import com.pixelmoncreate.neoquantum.util.Const;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.pixelmoncreate.neoquantum.util.Reference.neo;

public class AttributeRegistry {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.Keys.ATTRIBUTES, Const.MOD_ID);

    public static final RegistryObject<Attribute> DRAW_SPEED = ATTRIBUTES.register("draw_speed", () -> new PercentBasedAttribute(neo("draw_speed"), 1.0D, 0.0D, 4.0D).setSyncable(true));
    public static final RegistryObject<Attribute> CRIT_CHANCE = ATTRIBUTES.register("crit_chance", () -> new PercentBasedAttribute(neo("crit_chance"), 0.05D, 0.0D, 10.0D).setSyncable(true));
    public static final RegistryObject<Attribute> CRIT_DAMAGE = ATTRIBUTES.register("crit_damage", () -> new PercentBasedAttribute(neo("crit_damage"), 1.5D, 1.0D, 100.0D).setSyncable(true));
    public static final RegistryObject<Attribute> COLD_DAMAGE = ATTRIBUTES.register("cold_damage", () -> new RangedAttribute(neo("cold_damage"), 0.0D, 0.0D, 1000.0D).setSyncable(true));
    public static final RegistryObject<Attribute> FIRE_DAMAGE = ATTRIBUTES.register("fire_damage", () -> new RangedAttribute(neo("fire_damage"), 0.0D, 0.0D, 1000.0D).setSyncable(true));
    public static final RegistryObject<Attribute> LIFE_STEAL = ATTRIBUTES.register("life_steal", () -> new PercentBasedAttribute(neo("life_steal"), 0.0D, 0.0D, 10.0D).setSyncable(true));
    public static final RegistryObject<Attribute> CURRENT_HP_DAMAGE = ATTRIBUTES.register("current_hp_damage", () -> new PercentBasedAttribute(neo("current_hp_damage"), 0.0D, 0.0D, 1.0D).setSyncable(true));
    public static final RegistryObject<Attribute> OVERHEAL = ATTRIBUTES.register("overheal", () -> new PercentBasedAttribute(neo("overheal"), 0.0D, 0.0D, 10.0D).setSyncable(true));
    public static final RegistryObject<Attribute> GHOST_HEALTH = ATTRIBUTES.register("ghost_health", () -> new RangedAttribute(neo("ghost_health"), 0.0D, 0.0D, 1000.0D).setSyncable(true));
    public static final RegistryObject<Attribute> MINING_SPEED = ATTRIBUTES.register("mining_speed", () -> new PercentBasedAttribute(neo("mining_speed"), 1.0D, 0.0D, 10.0D).setSyncable(true));
    public static final RegistryObject<Attribute> ARROW_DAMAGE = ATTRIBUTES.register("arrow_damage", () -> new PercentBasedAttribute(neo("arrow_damage"), 1.0D, 0.0D, 10.0D).setSyncable(true));
    public static final RegistryObject<Attribute> ARROW_VELOCITY = ATTRIBUTES.register("arrow_velocity", () -> new PercentBasedAttribute(neo("arrow_velocity"), 1.0D, 0.0D, 10.0D).setSyncable(true));
    public static final RegistryObject<Attribute> EXPERIENCE_GAINED = ATTRIBUTES.register("experience_gained", () -> new PercentBasedAttribute(neo("experience_gained"), 1.0D, 0.0D, 10.0D).setSyncable(true));
    public static final RegistryObject<Attribute> HEALING_RECEIVED = ATTRIBUTES.register("healing_received", () -> new PercentBasedAttribute(neo("healing_received"), 1.0D, 0.0D, 10.0D).setSyncable(true));
    public static final RegistryObject<Attribute> ARMOR_PIERCE = ATTRIBUTES.register("armor_pierce", () -> new RangedAttribute(neo("armor_pierce"), 0.0D, 0.0D, 1000.0D).setSyncable(true));
    public static final RegistryObject<Attribute> ARMOR_SHRED = ATTRIBUTES.register("armor_shred", () -> new PercentBasedAttribute(neo("armor_shred"), 0.0D, 0.0D, 2.0D).setSyncable(true));
    public static final RegistryObject<Attribute> PROT_PIERCE = ATTRIBUTES.register("prot_pierce", () -> new RangedAttribute(neo("prot_pierce"), 0.0D, 0.0D, 34.0D).setSyncable(true));
    public static final RegistryObject<Attribute> PROT_SHRED = ATTRIBUTES.register("prot_shred", () -> new PercentBasedAttribute(neo("prot_shred"), 0.0D, 0.0D, 1.0D).setSyncable(true));

    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ATTRIBUTES.register(modEventBus);
    }
}
