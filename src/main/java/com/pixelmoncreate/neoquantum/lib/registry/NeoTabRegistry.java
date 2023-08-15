package com.pixelmoncreate.neoquantum.lib.registry;

import com.pixelmoncreate.neoquantum.util.Const;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

public class NeoTabRegistry {

    /**
     * The ID of NeoQuantum's Creative Tab
     */
    public static final ResourceKey<CreativeModeTab> NEO_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
            new ResourceLocation(Const.MOD_ID, "neoquantum"));

}
