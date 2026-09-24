/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.resource.AssetLocation;
import hellfirepvp.astralsorcery.client.screen.base.FixedSizeScreen;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyHandler;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.util.ColorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.stream.Streams;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ScreenConstellationPaper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ScreenConstellationPaper extends FixedSizeScreen {

    private final BaseConstellation constellation;
    private final ColorWrapper color;
    private List<MoonPhase> activePhases = null;

    public ScreenConstellationPaper(BaseConstellation constellation) {
        super(GameNarrator.NO_TITLE, 274, 235);
        this.constellation = constellation;
        this.color = ColorUtil.blendColors(this.constellation.getConstellationColor(), ColorWrapper.opaque(0x4D4D4D), 0.2F);
        this.resolvePhases();
    }

    private void resolvePhases() {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        LevelSkyHandler.getContext(level).ifPresent(ctx -> {
            this.activePhases = new ArrayList<>();
            this.activePhases.addAll(ctx.getConstellationHandler().getActiveIndexedPhases(this.constellation));

        });
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);

        RenderingDrawUtil.drawTexturedRect(guiGraphics.pose(), TexturesAS.SCREEN_CONSTELLATION_PAPER, this.getScreenRectangle());
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(this.getScreenLeft(), this.getScreenTop(), 0);

        this.drawHeadline(guiGraphics);
        this.drawConstellation(guiGraphics);
        this.drawPhases(guiGraphics);

        pose.popPose();
    }

    private void drawHeadline(GuiGraphics graphics) {
        Component name = this.constellation.getName();
        float scale = 2F;
        float length = this.font.width(name) * scale;

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(this.getScreenWidth() / 2F - length / 2F, 20, 0);
        pose.scale(scale, scale, 1F);
        graphics.drawString(this.font, name, 0, 0, this.color.getColor(), false);
        pose.popPose();
    }

    private void drawConstellation(GuiGraphics graphics) {
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();

        RenderConstellationUtil.drawConstellationUI(
                this.color,
                this.constellation,
                graphics.pose(),
                this.getScreenWidth() / 2F - 122 / 2F, 66,
                122, 122,
                3F,
                () -> 0.8F,
                true,
                false);

        RenderSystem.disableBlend();
    }

    private void drawPhases(GuiGraphics graphics) {
        if (this.activePhases == null) this.resolvePhases();
        if (this.activePhases == null) return;

        if (this.activePhases.isEmpty()) {
            PoseStack pose = graphics.pose();
            pose.pushPose();

            Component cmp = Component.translatable("constellation.astralsorcery.phases.unknown");
            float scale = 16F / 9F;
            float length = this.font.width(cmp) * scale;
            pose.translate(this.getScreenWidth() / 2F - length / 2F, 203, 0);
            pose.scale(scale, scale, 1F);

            graphics.drawString(this.font, cmp,
                    0, 0,
                    0xFF4D4D4D, false);

            pose.popPose();
        } else {
            int spriteSize = 16;

            int offsetX = (this.getScreenWidth() / 2) - (this.activePhases.size() * (spriteSize + 2)) / 2;
            int offsetY = 202;

            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();

            for (int i = 0; i < this.activePhases.size(); i++) {
                MoonPhase phase = this.activePhases.get(i);
                phase.getAssetQuery().resolve().bindTexture();

                int renderOffsetX = offsetX + (i * (spriteSize + 2));
                RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
                    RenderQuadUtil.rect(buf, graphics.pose(), renderOffsetX, offsetY, spriteSize, spriteSize)
                            .draw();
                });
            }

            RenderSystem.disableBlend();
        }
    }

    @Override
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        super.removed();
        PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_CLOSE).forUI().play();
    }
}
