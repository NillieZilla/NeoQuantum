package com.pixelmoncreate.neoquantum.module.attribute;

import com.pixelmoncreate.neoquantum.config.Configuration;
import com.pixelmoncreate.neoquantum.module.attribute.compat.CuriosCompat;
import com.pixelmoncreate.neoquantum.module.attribute.impl.AttributeEvents;
import com.pixelmoncreate.neoquantum.proxy.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.pixelmoncreate.neoquantum.util.Const.LOGGER;

public class AttributesLib {

    public static final String MODID = "attributeslib";
    static Configuration attConfig = null;
    public static final Map<Attribute, AttributeConfig> ATTRIBUTE_CONFIG = new HashMap<>();

    public AttributesLib() {

        AttributeRegistry.init();
        MinecraftForge.EVENT_BUS.register(new AttributeEvents());

    }

    @SafeVarargs
    private static void addAll(EntityType<? extends LivingEntity> type, BiConsumer<EntityType<? extends LivingEntity>, Attribute> add, RegistryObject<Attribute>... attribs) {
        for (RegistryObject<Attribute> a : attribs)
            add.accept(type, a.get());
    }

    @SubscribeEvent
    public void applyAttribs(EntityAttributeModificationEvent e) {
        e.getTypes().forEach(type -> {
            addAll(type, e::add,
                    AttributeRegistry.DRAW_SPEED,
                    AttributeRegistry.CRIT_CHANCE,
                    AttributeRegistry.CRIT_DAMAGE,
                    AttributeRegistry.COLD_DAMAGE,
                    AttributeRegistry.FIRE_DAMAGE,
                    AttributeRegistry.LIFE_STEAL,
                    AttributeRegistry.CURRENT_HP_DAMAGE,
                    AttributeRegistry.OVERHEAL,
                    AttributeRegistry.GHOST_HEALTH,
                    AttributeRegistry.MINING_SPEED,
                    AttributeRegistry.ARROW_DAMAGE,
                    AttributeRegistry.ARROW_VELOCITY,
                    AttributeRegistry.EXPERIENCE_GAINED,
                    AttributeRegistry.HEALING_RECEIVED,
                    AttributeRegistry.ARMOR_PIERCE,
                    AttributeRegistry.ARMOR_SHRED,
                    AttributeRegistry.PROT_PIERCE,
                    AttributeRegistry.PROT_SHRED);
        });

        e.add(EntityType.PLAYER, ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6);
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public void setup(FMLCommonSetupEvent e) {
        this.reload(null);
        AttributeSupplier playerAttribs = ForgeHooks.getAttributesView().get(EntityType.PLAYER);
        for (Attribute attr : ForgeRegistries.ATTRIBUTES.getValues()) {
            if (playerAttribs.hasAttribute(attr)) attr.setSyncable(true);
        }
        if (ModList.get().isLoaded("curios")) {
            e.enqueueWork(CuriosCompat::init);
        }
    }

    public void reload(Proxy.NeoQuantumReloadEvent e) {
        attConfig = new Configuration(new File(Proxy.configDir, "attributes.cfg"));
        attConfig.setTitle("Neo Quantum Attribute Module");
        attConfig.setComment("This file contains configurable data for each attribute.\nThe names of each category correspond to the registry names of every loaded attribute.");
        ATTRIBUTE_CONFIG.clear();

        for (Attribute attribute : ForgeRegistries.ATTRIBUTES) {
            ATTRIBUTE_CONFIG.put(attribute, AttributeConfig.load(attribute, attConfig));
        }

        for (Attribute attribute : ForgeRegistries.ATTRIBUTES) {
            AttributeConfig cfg = ATTRIBUTE_CONFIG.get(attribute);
            for (int i = 1; i <= cfg.getMaxValue(); i++)
                if (cfg.getMinValue() > cfg.getMaxValue())
                    LOGGER.warn("Attribute {} has min/max value {}/{} at level {}, making this value unobtainable.", ForgeRegistries.ATTRIBUTES.getKey(attribute), cfg.getMinValue(), cfg.getMaxValue(), i);
        }

        if (e == null || attConfig.hasChanged()) attConfig.save();
    }

    public static TooltipFlag getTooltipFlag() {
        if (FMLEnvironment.dist.isClient()) return ClientAccess.getTooltipFlag();
        return TooltipFlag.Default.NORMAL;
    }

    static class ClientAccess {
        static TooltipFlag getTooltipFlag() {
            return Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
        }
    }

}
