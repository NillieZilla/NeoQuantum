package com.pixelmoncreate.neoquantum.lib.registry;

import com.pixelmoncreate.neoquantum.common.item.QuantumoniumItem;
import com.pixelmoncreate.neoquantum.lib.util.LibItemNames;
import com.pixelmoncreate.neoquantum.lib.util.NeoAbstract;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.pixelmoncreate.neoquantum.util.Reference.neoSpace;

public class NeoItemRegistry {

    private static final Map<ResourceLocation, Item> ALL = new LinkedHashMap<>();

    public static final QuantumoniumItem quantumonium = make(neoSpace(LibItemNames.QUANTUMONIUM), new QuantumoniumItem(unstackable().rarity(Rarity.UNCOMMON)));


    private static <T extends Item> T make(ResourceLocation id, T item) {
        var old = ALL.put(id, item);
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + id);
        }
        return item;
    }

    public static Item.Properties defaultBuilder() {
        return NeoAbstract.defaultItemBuilder();
    }

    private static Item.Properties stackTo16() {
        return defaultBuilder().stacksTo(16);

    }

    private static Item.Properties unstackable() {
        return defaultBuilder().stacksTo(1);
    }

    public static void registerItems(BiConsumer<Item, ResourceLocation> r) {
        for (var e : ALL.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

}
