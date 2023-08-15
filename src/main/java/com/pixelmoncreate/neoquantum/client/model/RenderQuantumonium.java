package com.pixelmoncreate.neoquantum.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import com.pixelmoncreate.neoquantum.client.ClientTickHandler;
import com.pixelmoncreate.neoquantum.common.item.QuantumoniumItem;
import com.pixelmoncreate.neoquantum.lib.registry.NeoItemRegistry;
import com.pixelmoncreate.neoquantum.proxy.Proxy;
import com.pixelmoncreate.neoquantum.util.Const;
import com.pixelmoncreate.neoquantum.util.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import static com.pixelmoncreate.neoquantum.util.Reference.neoSpace;

public class RenderQuantumonium {
    private static BookModel model = null;
    public static final Material TEXTURE = new Material(InventoryMenu.BLOCK_ATLAS, neoSpace("item/quantumonium_3d"));
    public static final Material NEO_TEXTURE = new Material(InventoryMenu.BLOCK_ATLAS, neoSpace("item/quantumonium_base_3d"));

    private static BookModel getModel() {
        if (model == null) {
            model = new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));
        }
        return model;
    }

    public static boolean renderHand(ItemStack stack, ItemDisplayContext type, boolean leftHanded, PoseStack ms, MultiBufferSource buffers, int light) {
        if (!Proxy.quantumonium3dmodel()
                || !type.firstPerson()
                || stack.isEmpty()
                || !stack.is(NeoItemRegistry.quantumonium)) {
            return false;
        }
        try {
            doRender(stack, leftHanded, ms, buffers, light, ClientTickHandler.partialTicks);
            return true;
        } catch (Throwable throwable) {
            Const.LOGGER.warn("Failed to render quantumonium", throwable);
            return false;
        }
    }

    private static void doRender(ItemStack stack, boolean leftHanded, PoseStack ms, MultiBufferSource buffers, int light, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        ms.pushPose();

        float ticks = ClientTickHandler.tickswithQuantumoniumOpen;
        if (ticks > 0 && ticks < 10) {
            if (QuantumoniumItem.isOpen()) {
                ticks += partialTicks;
            } else {
                ticks -= partialTicks;
            }
        }

        if (!leftHanded) {
            ms.translate(0.3F + 0.02F * ticks, 0.125F + 0.01F * ticks, -0.2F - 0.035F * ticks);
            ms.mulPose(VecHelper.rotateY(180F + ticks * 6));
        } else {
            ms.translate(0.1F - 0.02F * ticks, 0.125F + 0.01F * ticks, -0.2F - 0.035F * ticks);
            ms.mulPose(VecHelper.rotateY(200F + ticks * 10));
        }
        ms.mulPose(VecHelper.rotateZ(-0.3F + ticks * 2.85F));
        float opening = Mth.clamp(ticks / 12F, 0, 1);

        float pageFlipTicks = ClientTickHandler.pageFlipTicks;
        if (pageFlipTicks > 0) {
            pageFlipTicks -= ClientTickHandler.partialTicks;
        }

        float pageFlip = pageFlipTicks / 5F;

        float leftPageAngle = Mth.frac(pageFlip + 0.25F) * 1.6F - 0.3F;
        float rightPageAngle = Mth.frac(pageFlip + 0.75F) * 1.6F - 0.3F;
        var model = getModel();
        model.setupAnim(ClientTickHandler.total(), Mth.clamp(leftPageAngle, 0.0F, 1.0F), Mth.clamp(rightPageAngle, 0.0F, 1.0F), opening);

        Material mat = QuantumoniumItem.isNeo(stack) ? NEO_TEXTURE : TEXTURE;
        VertexConsumer buffer = mat.buffer(buffers, RenderType::entitySolid);
        model.renderToBuffer(ms, buffer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        if (ticks < 3) {
            Font font = Minecraft.getInstance().font;
            ms.mulPose(VecHelper.rotateZ(180F));
            ms.translate(-0.30F, -0.24F, -0.07F);
            ms.scale(0.0030F, 0.0030F, -0.0030F);

            ms.translate(0F, 10F, 0F);
            ms.scale(0.6F, 0.6F, 0.6F);
            Component edition = QuantumoniumItem.getEdition().copy().withStyle(ChatFormatting.ITALIC, ChatFormatting.BOLD);
            font.drawInBatch(edition, 0, 0, 0xA07100, false, ms.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, light);

            ms.translate(8F, 110F, 0F);
            String blurb = I18n.get("neoquantummisc.quantumoniumcover0");
            font.drawInBatch(blurb, 0, 0, 0x79ff92, false, ms.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, light);

            ms.translate(0F, -30F, 0F);

            String authorTitle = I18n.get("neoquantummisc.quantumoniumcover1");
            int len = font.width(authorTitle);
            font.drawInBatch(authorTitle, 58 - len / 2F, -8, 0xD69700, false, ms.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, light);
        }

        ms.popPose();
    }

    @SuppressWarnings("SameParameterValue")
    private static void renderText(int x, int y, int width, int paragraphSize, int color, String unlocalizedText, Matrix4f matrix, MultiBufferSource buffers, int light) {
        x += 2;
        y += 10;
        width -= 4;

        Font font = Minecraft.getInstance().font;
        String text = I18n.get(unlocalizedText).replaceAll("&", "\u00a7");
        String[] textEntries = text.split("<br>");

        List<List<String>> lines = new ArrayList<>();

        String controlCodes;
        for (String s : textEntries) {
            List<String> words = new ArrayList<>();
            String lineStr = "";
            String[] tokens = s.split(" ");
            for (String token : tokens) {
                String prev = lineStr;
                String spaced = token + " ";
                lineStr += spaced;

                controlCodes = toControlCodes(getControlCodes(prev));
                if (font.width(lineStr) > width) {
                    lines.add(words);
                    lineStr = controlCodes + spaced;
                    words = new ArrayList<>();
                }

                words.add(controlCodes + token);
            }

            if (!lineStr.isEmpty()) {
                lines.add(words);
            }
            lines.add(new ArrayList<>());
        }

        for (List<String> words : lines) {
            int xi = x;
            int spacing = 4;

            for (String s : words) {
                int extra = 0;
                font.drawInBatch(s, xi, y, color, false, matrix, buffers, Font.DisplayMode.NORMAL, 0, light);
                xi += font.width(s) + spacing + extra;
            }

            y += words.isEmpty() ? paragraphSize : 10;
        }
    }

    private static String getControlCodes(String s) {
        String controls = s.replaceAll("(?<!\u00a7)(.)", "");
        return controls.replaceAll(".*r", "r");
    }

    private static String toControlCodes(String s) {
        return s.replaceAll(".", "\u00a7$0");
    }
}