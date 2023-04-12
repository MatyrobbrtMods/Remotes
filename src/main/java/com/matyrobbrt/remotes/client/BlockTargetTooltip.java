package com.matyrobbrt.remotes.client;

import com.matyrobbrt.remotes.block.BlockTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record BlockTargetTooltip(BlockTarget target, Component message, ItemStack renderInstance) implements ClientTooltipComponent {
    public BlockTargetTooltip(BlockTarget target) {
        this(target, Component.translatable("tooltip.remotes.target",
                Component.literal(String.valueOf(target.pos().getX())).withStyle(ChatFormatting.AQUA),
                Component.literal(String.valueOf(target.pos().getY())).withStyle(ChatFormatting.AQUA),
                Component.literal(String.valueOf(target.pos().getZ())).withStyle(ChatFormatting.AQUA),

                Component.literal(target.dimension().location().toString()).withStyle(ChatFormatting.DARK_PURPLE)
        ).withStyle(ChatFormatting.GOLD), target.rememberedState().getBlock().asItem().getDefaultInstance());
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public int getWidth(Font pFont) {
        return pFont.width(message) + (renderInstance.isEmpty() ? 0 : 4 + 16);
    }

    @Override
    public void renderText(Font pFont, int pX, int pY, Matrix4f pMatrix4f, MultiBufferSource.BufferSource pBufferSource) {
        final int textOffset = Math.max(0, (16 - pFont.lineHeight) / 2);
        pFont.drawInBatch(this.message, pX, pY + textOffset, -1, true, pMatrix4f, pBufferSource, false, 0, 15728880);
    }

    @Override
    public void renderImage(Font pFont, int pMouseX, int pMouseY, PoseStack pPoseStack, ItemRenderer pItemRenderer, int pBlitOffset) {
        if (!renderInstance.isEmpty()) {
            pItemRenderer.renderAndDecorateItem(renderInstance, pMouseX + pFont.width(message) + 4, pMouseY);
        }
    }
}
