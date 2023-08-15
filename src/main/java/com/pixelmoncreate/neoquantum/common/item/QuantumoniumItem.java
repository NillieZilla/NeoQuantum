package com.pixelmoncreate.neoquantum.common.item;

import com.pixelmoncreate.neoquantum.lib.item.IBannerPatternItem;
import com.pixelmoncreate.neoquantum.lib.registry.NeoItemRegistry;
import com.pixelmoncreate.neoquantum.lib.registry.NeoSoundRegistry;
import com.pixelmoncreate.neoquantum.lib.registry.NeoTriggerRegistry;
import com.pixelmoncreate.neoquantum.lib.tag.NeoTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.NotNull;

import vazkii.patchouli.api.PatchouliAPI;

import java.util.List;

import static com.pixelmoncreate.neoquantum.util.Reference.neo;

public class QuantumoniumItem extends Item implements IBannerPatternItem {

    public static final String TAG_NEO_UNLOCK = neo("neo_unlock");

    public QuantumoniumItem(Properties settings) {
        super(settings);
    }

    public static boolean isOpen() {
        return BuiltInRegistries.ITEM.getKey(NeoItemRegistry.quantumonium).equals(PatchouliAPI.get().getOpenBookGui());
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(getEdition().copy().withStyle(ChatFormatting.GRAY));
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        if (playerIn instanceof ServerPlayer player) {
            NeoTriggerRegistry.INSTANCE.trigger(player, stack, player.serverLevel(), player.getX(), player.getY(), player.getZ());
            PatchouliAPI.get().openBookGUI(player, BuiltInRegistries.ITEM.getKey(this));
            playerIn.playSound(NeoSoundRegistry.quantumoniumopen, 1F, (float) (0.7 + Math.random() * 0.4));
        }

        return InteractionResultHolder.sidedSuccess(stack, worldIn.isClientSide());
    }

    public static Component getEdition() {
        try {
            return PatchouliAPI.get().getSubtitle(BuiltInRegistries.ITEM.getKey(NeoItemRegistry.quantumonium));
        } catch (IllegalArgumentException e) {
            return Component.literal("");
        }
    }

    public static Component getTitle(ItemStack stack) {
        Component title = stack.getHoverName();

        String akashicTomeNBT = "akashictome:displayName";
        if (stack.hasTag() && stack.getTag().contains(akashicTomeNBT)) {
            title = Component.Serializer.fromJson(stack.getTag().getString(akashicTomeNBT));
        }

        return title;
    }

    public static boolean isNeo(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(TAG_NEO_UNLOCK);
    }

    public static BlockHitResult doRayTrace(Level world, Player player, ClipContext.Fluid fluidMode) {
        return Item.getPlayerPOVHitResult(world, player, fluidMode);
    }

    @Override
    public TagKey<BannerPattern> getBannerPattern() {
        return NeoTags.BannerPatterns.PATTERN_ITEM_QUANTUMONIUM;
    }
}