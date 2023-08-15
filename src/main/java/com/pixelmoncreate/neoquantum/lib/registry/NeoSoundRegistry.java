package com.pixelmoncreate.neoquantum.lib.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.pixelmoncreate.neoquantum.util.Reference.neoSpace;

public class NeoSoundRegistry {

    private static final List<SoundEvent> EVENTS = new ArrayList<>();

    public static final SoundEvent quantumoniumopen = makeSoundEvent("quantumonium_open");

    private static SoundEvent makeSoundEvent(String name) {
        SoundEvent event = SoundEvent.createVariableRangeEvent(neoSpace(name));
        EVENTS.add(event);
        return event;
    }

    public static void init(BiConsumer<SoundEvent, ResourceLocation> r) {
        for (SoundEvent event : EVENTS) {
            r.accept(event, event.getLocation());
        }
    }

    private NeoSoundRegistry() {}

}
