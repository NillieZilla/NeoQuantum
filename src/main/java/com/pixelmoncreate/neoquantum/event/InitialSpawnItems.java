package com.pixelmoncreate.neoquantum.event;

import com.pixelmoncreate.neoquantum.util.Const;
import com.pixelmoncreate.neoquantum.util.PlayerUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Can be used to give players items when they first join a world. Call {@link
 * #add(ResourceLocation, Supplier)} in either init or post-init. <em>This should be used
 * sparingly</em>; we spawn with enough junk already. It is recommended to have config options to
 * disable spawn items.
 *
 * @author SilentChaos512
 * @since 3.0.3
 */
@ParametersAreNonnullByDefault
public final class InitialSpawnItems {
    private static final InitialSpawnItems INSTANCE = new InitialSpawnItems();
    private static final String NBT_KEY = Const.MOD_ID + ".SpawnItemsGiven";

    private final Map<ResourceLocation, Function<Player, Collection<ItemStack>>> spawnItems = new HashMap<>();

    private InitialSpawnItems() {
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
    }

    /**
     * Add a spawn item. If the supplier returns an empty stack ({@link ItemStack#EMPTY}), nothing
     * is given. If the item is given, the player will never receive a spawn item with the same
     * {@code key} again. If no item is given, the key will not be marked as given.
     *
     * @param key   The key to uniquely identify the spawn item
     * @param stack The {@link ItemStack} supplier. If it returns an empty stack, nothing is given.
     * @deprecated Use {@link #add(ResourceLocation, Function)} instead
     */
    @Deprecated
    public static void add(ResourceLocation key, Supplier<ItemStack> stack) {
        INSTANCE.spawnItems.put(key, p -> {
            ItemStack s = stack.get();
            return s.isEmpty() ? Collections.emptyList() : Collections.singleton(s);
        });
    }

    /**
     * Add a spawn item. If the supplier returns an empty stack ({@link ItemStack#EMPTY}), nothing
     * is given. If the item is given, the player will never receive a spawn item with the same
     * {@code key} again. If no item is given, the key will not be marked as given.
     *
     * @param key         The key to uniquely identify the spawn item
     * @param itemFactory The item stack producer. Should not contain empty stacks.
     */
    public static void add(ResourceLocation key, Function<Player, Collection<ItemStack>> itemFactory) {
        INSTANCE.spawnItems.put(key, itemFactory);
    }

    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        CompoundTag givenItems = PlayerUtils.getPersistedDataSubcompound(player, NBT_KEY);

        spawnItems.forEach((key, factory) -> handleSpawnItems(player, givenItems, key, factory.apply(player)));
    }

    private static void handleSpawnItems(Player player, CompoundTag givenItems, ResourceLocation key, Collection<ItemStack> items) {
        if (items.isEmpty()) return;

        String nbtKey = key.toString().replace(':', '.');
        if (!givenItems.getBoolean(nbtKey)) {
            items.forEach(stack -> {
                Const.LOGGER.debug("Giving player {} spawn item \"{}\": {}", player.getScoreboardName(), nbtKey, stack);
                PlayerUtils.giveItem(player, stack);
                givenItems.putBoolean(nbtKey, true);
            });
        }
    }
}