package com.pixelmoncreate.neoquantum.util;

import net.minecraft.resources.ResourceLocation;

import static com.pixelmoncreate.neoquantum.util.Const.MOD_ID;

public class ModResourceLocation extends ResourceLocation {
    public ModResourceLocation(String resourceName) {
        super(addModNamespace(resourceName));
    }

    private static String addModNamespace(String resourceName) {
        if (resourceName.contains(":")) {
            return resourceName;
        }
        return MOD_ID + ":" + resourceName;
    }
}
