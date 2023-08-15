package com.pixelmoncreate.neoquantum.lib.registry;

import com.pixelmoncreate.neoquantum.common.advancements.UseItemTrigger;
import net.minecraft.advancements.CriteriaTriggers;

import static com.pixelmoncreate.neoquantum.util.Reference.neoSpace;


public class NeoTriggerRegistry {

    public static final UseItemTrigger INSTANCE = new UseItemTrigger(neoSpace("use_item_success"));
    public static void init() {

        CriteriaTriggers.register(INSTANCE);

    }

}
