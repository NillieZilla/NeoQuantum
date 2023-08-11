package com.pixelmoncreate.neoquantum.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class Config {
    public static final class Common {

        static final ForgeConfigSpec spec;

        static {

            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            spec = builder.build();
        }

        private Common() {}

    }

    private static boolean isResourceLocation(Object o) {

        return o instanceof String && ResourceLocation.tryParse((String) o) != null;

    }

    public static final class Client {

        static final ForgeConfigSpec spec;

        static {

            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            spec = builder.build();
        }

        private Client() {}

    }

    private Config() {}

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Common.spec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Client.spec);
    }
}