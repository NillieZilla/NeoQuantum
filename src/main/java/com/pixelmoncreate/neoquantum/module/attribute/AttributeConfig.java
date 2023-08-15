package com.pixelmoncreate.neoquantum.module.attribute;

import com.pixelmoncreate.neoquantum.config.Configuration;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;

public class AttributeConfig {

    protected final Attribute attribute;
    protected final double defaultValue, minValue, maxValue;

    public AttributeConfig(Attribute attribute, double defaultValue, double minValue, double maxValue) {

        this.attribute = attribute;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;

    }

    public double getDefaultValue() {
        return this.defaultValue;
    }

    public double getMinValue() {
        return this.minValue;
    }

    public double getMaxValue() {
        return this.maxValue;
    }

    public static AttributeConfig load(Attribute attribute, Configuration cfg) {

        String category = ForgeRegistries.ATTRIBUTES.getKey(attribute).toString();
        double defaultValue = cfg.getFloat("Default Value", category, 0, 1, 1000, "The Default Value of the Attribute.");
        double minValue = cfg.getFloat("Min Value", category, 0, 1, 1000, "The Minimum Value of the Attribute.");
        double maxValue = cfg.getFloat("Max Value", category, 0, 1, 1000, "The Maximum Value of the Attribute.");
        AttributeConfig config = new AttributeConfig(attribute, defaultValue, minValue, maxValue);
        return config;
    }

}
