package com.pixelmoncreate.neoquantum.client;

import com.pixelmoncreate.neoquantum.common.item.QuantumoniumItem;
import net.minecraft.client.Minecraft;

public final class ClientTickHandler {

    private ClientTickHandler() {}

    public static int tickswithQuantumoniumOpen = 0;
    public static int pageFlipTicks = 0;
    public static int ticksInGame = 0;
    public static float partialTicks = 0;

    public static float total() {
        return ticksInGame + partialTicks;
    }

    public static void renderTick(float renderTickTime) {
        partialTicks = renderTickTime;
    }

    public static void clientTickEnd(Minecraft mc) {

        int ticksToOpen = 10;
        if (QuantumoniumItem.isOpen()) {
            if (tickswithQuantumoniumOpen < 0) {
                tickswithQuantumoniumOpen = 0;
            }
            if (tickswithQuantumoniumOpen < ticksToOpen) {
                tickswithQuantumoniumOpen++;
            }
            if (pageFlipTicks > 0) {
                pageFlipTicks--;
            }
        } else {
            pageFlipTicks = 0;
            if (tickswithQuantumoniumOpen > 0) {
                if (tickswithQuantumoniumOpen > ticksToOpen) {
                    tickswithQuantumoniumOpen = ticksToOpen;
                }
                tickswithQuantumoniumOpen--;
            }
        }
    }

    public static void notifyPageChange() {
        if (pageFlipTicks == 0) {
            pageFlipTicks = 5;
        }
    }

}