package com.pixelmoncreate.neoquantum.lib.item;

import com.pixelmoncreate.neoquantum.network.KeyPressOnItemPacket;
import net.minecraft.world.item.ItemStack;

public interface ICycleItem {
    void onCycleKeyPress(ItemStack stack, KeyPressOnItemPacket.Type direction);

}
