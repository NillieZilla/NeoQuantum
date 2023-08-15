package com.pixelmoncreate.neoquantum.module.attribute.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A Modifier Source is a container object around any potential Attribute Modifier Source.<br>
 * It has the code necessary to render and compare the object for display in the Attributes screen.
 */
public abstract class ModifierSource<T> implements Comparable<ModifierSource<T>> {

    protected final ModifierSourceType<T> type;
    protected final T data;

    public ModifierSource(ModifierSourceType<T> type, T data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Render this ModifierSource as whatever visual representation it may take.
     *
     * @param font
     * @param x
     * @param y
     * @param stack
     * @param itemRenderer
     * @param pBlitOffset
     */
    public abstract void render(Font font, int x, int y, PoseStack stack, ItemRenderer itemRenderer, int pBlitOffset);

    public ModifierSourceType<T> getType() {
        return this.type;
    }

    public final T getData() {
        return this.data;
    }


    public static class ItemModifierSource extends ModifierSource<ItemStack> {

        @SuppressWarnings("deprecation")
        public ItemModifierSource(ItemStack data) {
            super(ModifierSourceType.EQUIPMENT, data);
        }

        @Override
        public void render(Font font, int x, int y, PoseStack stack, ItemRenderer itemRenderer, int pBlitOffset) {
            PoseStack mvStack = RenderSystem.getModelViewStack();
            mvStack.pushPose();
            float scale = 0.5F;
            mvStack.scale(scale, scale, 1);
            mvStack.translate(1 + x / scale, 1 + y / scale, 0);
            mvStack.popPose();
            RenderSystem.applyModelViewMatrix();
        }

        @Override
        public int compareTo(@NotNull ModifierSource<ItemStack> o) {
            return 0;
        }
    }

}