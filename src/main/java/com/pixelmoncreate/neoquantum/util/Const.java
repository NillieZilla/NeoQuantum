package com.pixelmoncreate.neoquantum.util;

import com.mojang.logging.LogUtils;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;

import java.util.Random;

public class Const {

    public static final String MOD_ID = "neoquantum";
    public static final String MOD_NAME = "NeoQuantum";

    public static final String RESOURCE_PREFIX = MOD_ID + ":";

    public static final Random RANDOM = new Random();
    public static final RandomSource RANDOM_SOURCE = RandomSource.create();
    public static final Logger LOGGER = LogUtils.getLogger();

}
