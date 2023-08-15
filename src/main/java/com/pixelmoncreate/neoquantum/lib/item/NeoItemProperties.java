package com.pixelmoncreate.neoquantum.lib.item;

import com.pixelmoncreate.neoquantum.common.item.QuantumoniumItem;
import com.pixelmoncreate.neoquantum.lib.registry.NeoItemRegistry;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.apache.logging.log4j.util.TriConsumer;

import static com.pixelmoncreate.neoquantum.util.Reference.neoSpace;

public class NeoItemProperties {

    public static void init(TriConsumer<ItemLike, ResourceLocation, ClampedItemPropertyFunction> consumer) {

        consumer.accept(NeoItemRegistry.quantumonium, neoSpace("neo"), (stack, world, living, seed) -> QuantumoniumItem.isNeo(stack) ? 1 : 0);

    }

    private NeoItemProperties() {}

}
