package com.pixelmoncreate.neoquantum.lib.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;

import static com.pixelmoncreate.neoquantum.util.Reference.neoSpace;

public class NeoTags {

    public static class Items {

        public static final TagKey<Item> QUANTUMONIUM_BOOK = tag("books");

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, neoSpace(name));
        }

    }

    public static class Blocks {

    }

    public static class Entities {

    }

    public static class Biomes {

    }

    public static class BannerPatterns {
        public static final TagKey<BannerPattern> PATTERN_ITEM_QUANTUMONIUM = tag("pattern_item/quantumonium");

        private static TagKey<BannerPattern> tag(String name) {
            return TagKey.create(Registries.BANNER_PATTERN, neoSpace(name));
        }
    }

    public static class DamageTypes {

    }

}
