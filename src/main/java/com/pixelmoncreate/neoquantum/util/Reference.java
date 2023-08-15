package com.pixelmoncreate.neoquantum.util;

import net.minecraft.resources.ResourceLocation;

public class Reference {

    public static String neo(String path) {
        return "neoquantum:" + path;
    }
    public static String forge(String path) {
        return "forge:" + path;
    }

    public static ResourceLocation neoSpace(String path) {
        return new ResourceLocation(Const.MOD_ID, path);
    }
    public static ResourceLocation forgeSpace(String path) {
        return new ResourceLocation("forge" + path);
    }
}
