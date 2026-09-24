/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderQuadUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.item.base.PerkExperienceRevealer;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPerkExperienceOverlay
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderPerkExperienceOverlay {

    public static final ResourceLocation PERK_EXPERIENCE_ID = AstralSorcery.key("perk_experience_overlay");
    public static final LayerRenderer RENDERER = new LayerRenderer();

    public static void registerLayers(RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.EXPERIENCE_BAR, PERK_EXPERIENCE_ID, RENDERER);
    }

    public static void onClientTick(ClientTickEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (MiscUtil.getMainOrOffHand(player,
                stack -> stack.getItem() instanceof PerkExperienceRevealer revealer && revealer.shouldReveal(stack))
                .isPresent()) {
            RENDERER.revealExperienceBar(20);
        }

        RENDERER.updateRevealTicks();
    }

    public static class LayerRenderer implements LayeredDraw.Layer {

        private static final int fadeTicks = 15;
        private static final float visibilityChange = 1F / ((float) fadeTicks);

        private int revealTicks = 0;
        private float visibilityAlpha = 0F;

        public void revealExperienceBar(int ticks) {
            this.revealTicks = Math.max(this.revealTicks, ticks);
        }

        private void updateRevealTicks() {
            this.revealTicks--;

            if ((this.revealTicks - fadeTicks) < 0) {
                if (this.visibilityAlpha > 0) {
                    this.visibilityAlpha = Math.max(0, this.visibilityAlpha - visibilityChange);
                }
            } else {
                if (this.visibilityAlpha < 1) {
                    this.visibilityAlpha = Math.min(1, this.visibilityAlpha + visibilityChange);
                }
            }
        }

        @Override
        public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
            if (Minecraft.getInstance().options.hideGui) return;
            Player player = Minecraft.getInstance().player;
            if (player == null) return;

            PlayerProgress progress = ResearchManager.getClientProgress();
            if (!progress.isAttuned()) return;

            PoseStack pose = guiGraphics.pose();
            float frameHeight  = 128F;
            float frameWidth   =  32F;
            float frameOffsetX =   0F;
            float frameOffsetY =   5F;

            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();

            TexturesAS.SCREEN_PERK_EXPERIENCE_FRAME.bindTexture();
            RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
                RenderQuadUtil.rect(buf, pose, frameOffsetX, frameOffsetY, frameWidth, frameHeight)
                        .color(1F, 1F, 1F, visibilityAlpha * 0.9F)
                        .draw();
            });

            float perc = progress.getPerkData().getPercentToNextLevel(player, LogicalSide.CLIENT);
            float expHeight  =  78F * perc;
            float expWidth   =  32F;
            float expOffsetX =   0F;
            float expOffsetY =  27.5F + (1F - perc) * 78F;
            ColorWrapper barColor = ColorsAS.PERK_EXPERIENCE_BAR.copyWithAlpha((int) (this.visibilityAlpha * 255F));

            TexturesAS.SCREEN_PERK_EXPERIENCE_BAR.bindTexture();
            RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
                RenderQuadUtil.rect(buf, pose, expOffsetX, expOffsetY, expWidth, expHeight)
                        .color(barColor)
                        .tex(0, 0, 1, 1 - perc)
                        .draw();
            });

            Font font = Minecraft.getInstance().font;
            String strLevel = String.valueOf(progress.getPerkData().getPerkLevel(player, LogicalSide.CLIENT));
            MutableComponent cmpLevel = Component.literal(strLevel);
            int width = font.width(cmpLevel);
            float size = 1.2F;

            if (this.visibilityAlpha > 1E-4) {
                pose.pushPose();
                pose.translate(16 - (width / 2F) * size, 94, 10);
                pose.scale(size, size, 1F);
                int color = 0xDDDDDD;
                color |= ((int) (this.visibilityAlpha * 255F)) << 24;
                guiGraphics.drawString(font, cmpLevel, 0, 0, color);
                pose.popPose();
            }
        }
    }
}
