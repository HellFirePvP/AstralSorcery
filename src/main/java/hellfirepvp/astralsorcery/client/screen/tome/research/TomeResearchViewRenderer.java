/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.research;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.base.ResearchClusterSizeHandler;
import hellfirepvp.astralsorcery.client.screen.base.ScalingPoint;
import hellfirepvp.astralsorcery.client.screen.tome.TomeResearchScreen;
import hellfirepvp.astralsorcery.client.screen.tome.TomeStarParallaxLayer;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import hellfirepvp.astralsorcery.common.util.data.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeResearchViewRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomeResearchViewRenderer extends TomeResearchPartRenderer implements TomeStarParallaxLayer {

    private final ResearchClusterSizeHandler sizeHandler;
    private final ScalingPoint mousePoint;
    private final ScalingPoint prevMousePoint;

    private long lastClick = 0L; //For double-click fast zoom in

    //private ResearchTier focusedTier = null;
    private TomeResearchClusterRenderer focusedTierRenderer = null;
    private final Map<FloatRectangle, ResearchTier> clusterMap = new HashMap<>();

    public TomeResearchViewRenderer(TomeResearchScreen screen) {
        super(screen);
        this.sizeHandler = new ResearchClusterSizeHandler();
        this.sizeHandler.updateSize();
        this.mousePoint = ScalingPoint.createPoint(
                this.sizeHandler.clampX(this.sizeHandler.getScaledWidth() / 2F) + this.sizeHandler.getScaledNodeSize(),
                this.sizeHandler.clampY(this.sizeHandler.getScaledHeight() / 2F) + this.sizeHandler.getScaledNodeSize(),
                this.sizeHandler.getScalingFactor(),
                false);
        this.prevMousePoint = ScalingPoint.copy(this.mousePoint);
        this.applyMouseMove();
    }

    public void moveMouse(float changeX, float changeY) {
        if (this.sizeHandler.getScalingFactor() >= 6 && this.focusedTierRenderer != null) {
            this.focusedTierRenderer.moveMouse(changeX, changeY);
            return;
        }

        this.mousePoint.updateScaledPos(
                this.sizeHandler.clampX(this.prevMousePoint.getScaledPosX() + changeX),
                this.sizeHandler.clampY(this.prevMousePoint.getScaledPosY() + changeY),
                this.sizeHandler.getScalingFactor()
        );
    }

    public void applyMouseMove() {
        if (this.sizeHandler.getScalingFactor() >= 6 && this.focusedTierRenderer != null) {
            this.focusedTierRenderer.applyMouseMove();
            return;
        }

        this.prevMousePoint.updateScaledPos(this.mousePoint.getScaledPosX(), this.mousePoint.getScaledPosY(), this.sizeHandler.getScalingFactor());
    }

    @Override
    public void refreshView() {
        this.sizeHandler.resetScale();
        this.mousePoint.rescale(this.sizeHandler.getScalingFactor());
        this.prevMousePoint.rescale(this.sizeHandler.getScalingFactor());
        this.moveMouse(0, 0);
        this.unfocusCluster();
        this.sizeHandler.updateSize();
        this.applyMouseMove();
    }

    private void unfocusCluster() {
        this.focusedTierRenderer = null;
    }

    private void focusCluster(ResearchTier tier) {
        this.focusedTierRenderer = new TomeResearchClusterRenderer(tier);
    }

    private void zoomOut() {
        this.sizeHandler.handleZoomOut();
        this.mousePoint.rescale(this.sizeHandler.getScalingFactor());
        this.prevMousePoint.rescale(this.sizeHandler.getScalingFactor());
        this.moveMouse(0, 0);

        if (this.sizeHandler.getScalingFactor() <= 4) {
            this.unfocusCluster();
        } else if (this.sizeHandler.getScalingFactor() >= 6 && this.focusedTierRenderer != null) {
            this.focusedTierRenderer.zoomOut();
        }
    }

    private void zoomIn(double mouseX, double mouseY) {
        float currentScale = this.sizeHandler.getScalingFactor();

        if (currentScale >= 4) {
            if (this.focusedTierRenderer == null) {
                this.getHoveredTier(mouseX, mouseY).ifPresent(this::focusCluster);
            }
            if (this.focusedTierRenderer == null) {
                return; // No further zooming if no cluster is hovered
            }

            if (currentScale < 6) {
                float vDiv = (2F - (currentScale - 4F)) * 10F;
                IntRectangle rct = this.focusedTierRenderer.getTier().getCloudArea();
                IntPoint mid = rct.center();
                float midX = this.sizeHandler.evRelativePosX(mid.x());
                float midY = this.sizeHandler.evRelativePosY(mid.y());
                Vector3 center = new Vector3(midX, midY, 0);
                Vector3 mousePos = new Vector3(this.mousePoint.getScaledPosX(), this.mousePoint.getScaledPosY(), 0);
                Vector3 dir = center.subtract(mousePos);
                if (vDiv > 0.05) {
                    dir.divide(vDiv);
                }

                this.prevMousePoint.updateScaledPos(
                        this.sizeHandler.clampX((float) (mousePos.getX() + dir.getX())),
                        this.sizeHandler.clampY((float) (mousePos.getY() + dir.getY())),
                        this.sizeHandler.getScalingFactor());
                this.moveMouse(0, 0);
            } else {
                this.focusedTierRenderer.zoomIn();
            }
        }

        this.sizeHandler.handleZoomIn();
        this.mousePoint.rescale(this.sizeHandler.getScalingFactor());
        this.prevMousePoint.rescale(this.sizeHandler.getScalingFactor());
    }

    private Optional<ResearchTier> getHoveredTier(double mouseX, double mouseY) {
        return this.clusterMap.entrySet().stream()
                .filter(entry -> entry.getKey().contains(mouseX, mouseY))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    @Override
    public boolean mouseClick(double mouseX, double mouseY) {
        if (this.sizeHandler.getScalingFactor() > 6 &&
                this.focusedTierRenderer != null &&
                this.getRenderBoundingBox().containsPoint((int) Math.round(mouseX), (int) Math.round(mouseY))) {
            return this.focusedTierRenderer.mouseClick(this.getParentScreen(), mouseX, mouseY);
        }
        //Instant-zoom in on double-click
        if (this.getHoveredTier(mouseX, mouseY).isPresent()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.lastClick < 300L) {
                int loop = 300;
                while (this.getHoveredTier(mouseX, mouseY).isPresent() && this.sizeHandler.getScalingFactor() < 9.9 && loop-- > 0) {
                    this.zoomIn(mouseX, mouseY);
                }
                this.lastClick = 0L;
                return true;
            }
            this.lastClick = currentTime;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        if (scrollDelta > 0) {
            this.zoomIn(mouseX, mouseY);
            return true;
        }
        if (scrollDelta < 0) {
            this.zoomOut();
            return true;
        }
        return false;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, Runnable renderWidgets, float mouseX, float mouseY, float pTicks) {
        this.getParentScreen().renderTransparentBackground(guiGraphics);

        ScreenRectangle rect = this.getParentScreen().getScreenRectangle();
        guiGraphics.enableScissor(rect.left() + 20, rect.top() + 20, rect.right() - 20, rect.bottom() - 20);
        this.drawResearchView(guiGraphics, mouseX, mouseY, pTicks);
        guiGraphics.disableScissor();

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(0, 0, 400);

        RenderingDrawUtil.drawTexturedRect(guiGraphics.pose(), TexturesAS.SCREEN_TOME_FRAME_CUTOUT, this.getParentScreen().getScreenRectangle());
        renderWidgets.run();

        if (this.sizeHandler.getScalingFactor() > 6 &&
                this.focusedTierRenderer != null &&
                this.getRenderBoundingBox().containsPoint(Math.round(mouseX), Math.round(mouseY))) {
            this.focusedTierRenderer.drawHoverHighlight(guiGraphics, mouseX, mouseY);
        }
        pose.popPose();
    }

    private void drawResearchView(GuiGraphics guiGraphics, float mouseX, float mouseY, float pTicks) {
        this.drawBackground(guiGraphics);
        this.drawClusters(guiGraphics);

        float scaleX = this.mousePoint.getPosX();
        float scaleY = this.mousePoint.getPosY();

        if (this.sizeHandler.getScalingFactor() > 6 && this.focusedTierRenderer != null) {
            ResearchTier focusedTier = this.focusedTierRenderer.getTier();
            this.drawClusterBackground(guiGraphics, focusedTier);

            this.focusedTierRenderer.drawClusterView(this.getParentScreen(), guiGraphics, pTicks);
            scaleX = this.focusedTierRenderer.getMouseX();
            scaleY = this.focusedTierRenderer.getMouseY();
        }

        this.getHoveredTier(mouseX, mouseY).ifPresent(hoveredTier -> {
            this.drawHoveredTierName(guiGraphics, hoveredTier);
        });

        this.drawStarParallaxLayers(guiGraphics, this.sizeHandler.getScalingFactor(), this.getParentScreen().getScreenRectangle(), scaleX, scaleY);
    }

    private void drawHoveredTierName(GuiGraphics guiGraphics, ResearchTier hoveredTier) {
        IntRectangle rct = hoveredTier.getCloudArea();
        FloatPoint offset = this.sizeHandler.scalePointToGui(this.getParentScreen(), this.mousePoint, rct.offset().toFloat());
        float width  = this.sizeHandler.scaledDistanceX(rct.x(), rct.maxX());
        float height = this.sizeHandler.scaledDistanceY(rct.y(), rct.maxY());

        float scale = sizeHandler.getScalingFactor();
        float br = 1F;
        if (scale >= 8F) {
            br = 0F;
        } else if (scale >= 5F) {
            br = 1F - ((scale - 5F) / 3F);
        }

        Font font = Minecraft.getInstance().font;
        Component tierName = hoveredTier.getName();
        float length = font.width(tierName) * 1.4F;
        int alpha = Math.round(0xD0 * br);
        // Minecraft font rendering has a Font#adjustColor alpha cutoff of < FC being rounded to full opaque. so we max(4)
        int color = ColorWrapper.WHITE.copyWithAlpha(Math.max(alpha, 4)).getColor();

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(offset.x() + width / 2F - length / 2F, offset.y() + height / 3F, 0);
        poseStack.scale(1.4F, 1.4F, 1.0F);
        guiGraphics.drawString(font, tierName, 0, 0, color, true);
        poseStack.popPose();
    }

    private void drawClusterBackground(GuiGraphics guiGraphics, ResearchTier tier) {
        float scale = sizeHandler.getScalingFactor();
        float br;
        if (scale > 8F) {
            br = 0.6F;
        } else if (scale >= 6F) {
            br = ((scale - 6F) / 2F) * 0.6F;
        } else {
            br = 0F;
        }

        tier.getCloudTexture().resolve().bindTexture();
        ScreenRectangle rct = this.getParentScreen().getScreenRectangle();
        float minX      = rct.left();
        float minY      = rct.top();
        float maxX      = rct.right();
        float maxY      = rct.bottom();

        float ratio = (maxY - minY) / (maxX - minX);
        float part = ratio / 2F;

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            PoseStack.Pose pose = guiGraphics.pose().last();
            buf.addVertex(pose, minX, maxY, 0).setColor(br, br, br, br).setUv(0, 0.5F + part);
            buf.addVertex(pose, maxX, maxY, 0).setColor(br, br, br, br).setUv(1, 0.5F + part);
            buf.addVertex(pose, maxX, minY, 0).setColor(br, br, br, br).setUv(1, 0.5F - part);
            buf.addVertex(pose, minX, minY, 0).setColor(br, br, br, br).setUv(0, 0.5F - part);
        });

        RenderSystem.disableBlend();
    }

    private void drawClusters(GuiGraphics guiGraphics) {
        this.clusterMap.clear();
        if (this.sizeHandler.getScalingFactor() > 8) return;

        PlayerProgress progress = ResearchManager.getClientProgress();
        for (ResearchTier tier : ResearchTier.values()) {
            if (!tier.canSee(progress)) {
                continue;
            }
            IntRectangle rct = tier.getCloudArea();
            tier.getCloudTexture().resolve().bindTexture();
            FloatRectangle renderedRct = this.drawCluster(guiGraphics, rct);
            this.clusterMap.put(renderedRct, tier);
        }
    }

    private FloatRectangle drawCluster(GuiGraphics guiGraphics, IntRectangle rct) {
        FloatPoint offset = this.sizeHandler.scalePointToGui(this.getParentScreen(), this.mousePoint, rct.offset().toFloat());
        float width  = this.sizeHandler.scaledDistanceX(rct.x(), rct.maxX());
        float height = this.sizeHandler.scaledDistanceY(rct.y(), rct.maxY());

        float scale = sizeHandler.getScalingFactor();
        float br;
        if (scale > 8.01F) {
            br = 0F;
        } else if (scale >= 6F) {
            br = 1F - ((scale - 6F) / 2F);
        } else {
            br = 1F;
        }

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            PoseStack.Pose pose = guiGraphics.pose().last();
            buf.addVertex(pose, offset.x(),         offset.y() + height, 0).setColor(br, br, br, br).setUv(0, 1);
            buf.addVertex(pose, offset.x() + width, offset.y() + height, 0).setColor(br, br, br, br).setUv(1, 1);
            buf.addVertex(pose, offset.x() + width, offset.y(),          0).setColor(br, br, br, br).setUv(1, 0);
            buf.addVertex(pose, offset.x(),         offset.y(),          0).setColor(br, br, br, br).setUv(0, 0);
        });

        RenderSystem.disableBlend();

        return new FloatRectangle(offset.x(), offset.y(), width, height);
    }

    private void drawBackground(GuiGraphics guiGraphics) {
        float br = 0.4F;
        ScreenRectangle rct = this.getParentScreen().getScreenRectangle();
        float minX      = rct.left();
        float minY      = rct.top();
        float maxX      = rct.right();
        float maxY      = rct.bottom();

        TexturesAS.SCREEN_TOME_BACKGROUND_RESEARCH.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            PoseStack.Pose pose = guiGraphics.pose().last();
            buf.addVertex(pose, minX, maxY, 0).setColor(br, br, br, 1.0F).setUv(0, 1);
            buf.addVertex(pose, maxX, maxY, 0).setColor(br, br, br, 1.0F).setUv(1, 1);
            buf.addVertex(pose, maxX, minY, 0).setColor(br, br, br, 1.0F).setUv(1, 0);
            buf.addVertex(pose, minX, minY, 0).setColor(br, br, br, 1.0F).setUv(0, 0);
        });
    }


}
