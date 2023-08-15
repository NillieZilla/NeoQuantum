package com.pixelmoncreate.neoquantum.module.attribute.client;

import com.pixelmoncreate.neoquantum.module.attribute.api.AttributeHelper;
import com.pixelmoncreate.neoquantum.util.Comparators;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * A Modifier Source Type is a the registration component of a ModifierSource.
 *
 * @param <T>
 */
public abstract class ModifierSourceType<T> {

    private static final List<ModifierSourceType<?>> SOURCE_TYPES = new ArrayList<>();

    public static final ModifierSourceType<ItemStack> EQUIPMENT = register(new ModifierSourceType<>(){

        @Override
        public void extract(LivingEntity entity, BiConsumer<AttributeModifier, ModifierSource<?>> map) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack item = entity.getItemBySlot(slot);
                item.getAttributeModifiers(slot).values().forEach(modif -> {
                    map.accept(modif, new ModifierSource.ItemModifierSource(item));
                });
            }
        }

        @Override
        public int getPriority() {
            return 0;
        }

    });

    public static Collection<ModifierSourceType<?>> getTypes() {
        return Collections.unmodifiableCollection(SOURCE_TYPES);
    }

    public static <T extends ModifierSourceType<?>> T register(T type) {
        SOURCE_TYPES.add(type);
        return type;
    }

    public static Comparator<AttributeModifier> compareBySource(Map<UUID, ModifierSource<?>> sources) {

        Comparator<AttributeModifier> comp = Comparators.chained(
                Comparator.comparingInt(a -> sources.get(a.getId()).getType().getPriority()),
                Comparator.comparing(a -> sources.get(a.getId())),
                AttributeHelper.modifierComparator());

        return (a1, a2) -> {
            var src1 = sources.get(a1.getId());
            var src2 = sources.get(a2.getId());

            if (src1 != null && src2 != null) return comp.compare(a1, a2);

            return src1 != null ? -1 : src2 != null ? 1 : 0;
        };
    }

    /**
     * Extracts all ModifierSource(s) of this type from the source entity.
     *
     * @param entity
     * @param map
     */
    public abstract void extract(LivingEntity entity, BiConsumer<AttributeModifier, ModifierSource<?>> map);

    /**
     * Integer priority for display sorting.<br>
     * Lower priority values will be displayed at the top of the list.
     */
    public abstract int getPriority();

}