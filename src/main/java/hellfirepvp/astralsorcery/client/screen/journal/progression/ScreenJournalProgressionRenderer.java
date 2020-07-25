/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.progression;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.screen.helper.ScalingPoint;
import hellfirepvp.astralsorcery.client.screen.helper.ScreenRenderBoundingBox;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalProgression;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournalProgressionRenderer
 * Created by HellFirePvP
 * Date: 03.08.2019 / 17:02
 */
public class ScreenJournalProgressionRenderer {

    private GalaxySizeHandler sizeHandler;
    private ScreenJournalProgression parentGui;

    public ScreenRenderBoundingBox realRenderBox;
    private int realCoordLowerX, realCoordLowerY;
    private int realRenderWidth, realRenderHeight;

    private ScalingPoint mousePointScaled;
    private ScalingPoint previousMousePointScaled;

    private ResearchProgression focusedClusterZoom = null, focusedClusterMouse = null;
    private ScreenJournalClusterRenderer clusterRenderer = null;

    private long doubleClickLast = 0L;

    private boolean hasPrevOffset = false;
    private Map<Rectangle, ResearchProgression> clusterRectMap = new HashMap<>();

    public ScreenJournalProgressionRenderer(ScreenJournalProgression gui) {
        this.parentGui = gui;
        this.sizeHandler = new GalaxySizeHandler();
        refreshSize();
        this.mousePointScaled = ScalingPoint.createPoint(
                this.sizeHandler.clampX(this.sizeHandler.getTotalWidth() / 2F),
                this.sizeHandler.clampY(this.sizeHandler.getTotalHeight() / 2F),
                this.sizeHandler.getScalingFactor(),
                false);
        this.moveMouse(this.sizeHandler.getTotalWidth() / 2, this.sizeHandler.getTotalHeight() / 2);
        applyMovedMouseOffset();
    }

    public void refreshSize() {
        this.sizeHandler.updateSize();
    }

    public void setBox(int left, int top, int right, int bottom) {
        this.realRenderBox = new ScreenRenderBoundingBox(left, top, right, bottom);
        this.realRenderWidth = (int) this.realRenderBox.getWidth();
        this.realRenderHeight = (int) this.realRenderBox.getHeight();
    }

    public void moveMouse(float changedX, float changedY) {
        if (sizeHandler.getScalingFactor() >= 6.1D && clusterRenderer != null) {
            clusterRenderer.moveMouse(changedX, changedY);
        } else {
            if (hasPrevOffset) {
                mousePointScaled.updateScaledPos(
                        sizeHandler.clampX(previousMousePointScaled.getScaledPosX() + changedX),
                        sizeHandler.clampY(previousMousePointScaled.getScaledPosY() + changedY),
                        sizeHandler.getScalingFactor());
            } else {
                mousePointScaled.updateScaledPos(
                        sizeHandler.clampX(mousePointScaled.getScaledPosX()),
                        sizeHandler.clampY(mousePointScaled.getScaledPosY()),
                        sizeHandler.getScalingFactor());
            }
        }
    }

    public void applyMovedMouseOffset() {
        if (sizeHandler.getScalingFactor() >= 6.1D && clusterRenderer != null) {
            clusterRenderer.applyMovedMouseOffset();
        } else {
            this.previousMousePointScaled = ScalingPoint.createPoint(
                    mousePointScaled.getScaledPosX(),
                    mousePointScaled.getScaledPosY(),
                    sizeHandler.getScalingFactor(),
                    true);
            this.hasPrevOffset = true;
        }
    }

    public void updateOffset(int guiLeft, int guiTop) {
        this.realCoordLowerX = guiLeft;
        this.realCoordLowerY = guiTop;
    }

    public void centerMouse() {
        this.moveMouse(parentGui.getGuiLeft() + this.sizeHandler.getTotalWidth() / 2F, parentGui.getGuiTop() + this.sizeHandler.getTotalHeight() / 2F);
    }

    public void updateMouseState() {
        moveMouse(0, 0);
    }

    public void unfocus() {
        focusedClusterZoom = null;
    }

    public void focus(@Nonnull ResearchProgression researchCluster) {
        this.focusedClusterZoom = researchCluster;
        this.clusterRenderer = new ScreenJournalClusterRenderer(researchCluster, realRenderHeight, realRenderWidth, realCoordLowerX, realCoordLowerY);
    }

    //Nothing to actually click here, we redirect if we can.
    public boolean propagateClick(float mouseX, float mouseY) {
        if (clusterRenderer != null) {
            if (sizeHandler.getScalingFactor() > 6) {
                if (clusterRenderer.propagateClick(parentGui, mouseX, mouseY)) {
                    return true;
                }
            }
        }
        if (focusedClusterMouse != null) {
            if (sizeHandler.getScalingFactor() <= 6) {
                long current = System.currentTimeMillis();
                if (current - this.doubleClickLast < 400L) {
                    int timeout = 500; //Handles irregular clicks on the GUI so it doesn't loop trying to find a focus cluster
                    while (focusedClusterMouse != null && sizeHandler.getScalingFactor() < 9.9 && timeout > 0) {
                        handleZoomIn(mouseX, mouseY);
                        timeout--;
                    }
                    this.doubleClickLast = 0L;
                    return true;
                }
                this.doubleClickLast = current;
            }
        }
        return false;
    }

    public void drawMouseHighlight(float zLevel, int mouseX, int mouseY) {
        if (clusterRenderer != null && sizeHandler.getScalingFactor() > 6) {
            clusterRenderer.drawMouseHighlight(zLevel, mouseX, mouseY);
        }
    }

    public void resetZoom() {
        sizeHandler.resetZoom();
        rescale(sizeHandler.getScalingFactor());
    }

    public void handleZoomOut() {
        this.sizeHandler.handleZoomOut();
        rescale(sizeHandler.getScalingFactor());

        if (this.sizeHandler.getScalingFactor() <= 4.0) {
            unfocus();
        } else if (this.sizeHandler.getScalingFactor() >= 6.0 && this.clusterRenderer != null) {
            clusterRenderer.handleZoomOut();
        }
    }

    /**
     * Thresholds for zooming in:
     * 1.0 - 4.0 don't care.
     * 4.0 - 6.0 has to have focus + centering to center of cluster
     * 6.0 - 10.0 transition (6.0 - 8.0) + cluster rendering + handling (cursor movement)
     */
    public void handleZoomIn(float mouseX, float mouseY) {
        float scale = sizeHandler.getScalingFactor();
        //double nextScale = Math.min(10.0D, scale + 0.2D);
        if (scale >= 4.0F) {
            if (focusedClusterZoom == null) {
                ResearchProgression prog = tryFocusCluster(mouseX, mouseY);
                if (prog != null) {
                    focus(prog);
                }
            }
            if (focusedClusterZoom == null) {
                return;
            }
            if (scale < 6.1F) { //Floating point shenanigans
                float vDiv = (2F - (scale - 4F)) * 10F;
                JournalCluster cluster = JournalProgressionClusterMapping.getClusterMapping(focusedClusterZoom);
                float x = this.sizeHandler.evRelativePosX(cluster.x);
                float y = this.sizeHandler.evRelativePosY(cluster.y);
                float width  = this.sizeHandler.scaledDistanceX(cluster.x, cluster.maxX);
                float height = this.sizeHandler.scaledDistanceY(cluster.y, cluster.maxY);
                Vector3 center = new Vector3(x + width / 2, y + height / 2, 0);
                Vector3 mousePos = new Vector3(mousePointScaled.getScaledPosX(), mousePointScaled.getScaledPosY(), 0);
                Vector3 dir = center.subtract(mousePos);
                if (vDiv > 0.05) {
                    dir.divide(vDiv);
                }
                if (!hasPrevOffset) {
                    mousePointScaled.updateScaledPos(
                            sizeHandler.clampX((float) (mousePos.getX() + dir.getX())),
                            sizeHandler.clampY((float) (mousePos.getY() + dir.getY())),
                            sizeHandler.getScalingFactor());
                } else {
                    previousMousePointScaled.updateScaledPos(
                            sizeHandler.clampX((float) (mousePos.getX() + dir.getX())),
                            sizeHandler.clampY((float) (mousePos.getY() + dir.getY())),
                            sizeHandler.getScalingFactor());
                }

                updateMouseState();
            } else if (clusterRenderer != null) {
                clusterRenderer.handleZoomIn();
            }
        }
        this.sizeHandler.handleZoomIn();
        this.mousePointScaled.rescale(sizeHandler.getScalingFactor());
        if (this.previousMousePointScaled != null) {
            this.previousMousePointScaled.rescale(sizeHandler.getScalingFactor());
        }
    }

    private void rescale(float newScale) {
        this.mousePointScaled.rescale(newScale);
        if (this.previousMousePointScaled != null) {
            this.previousMousePointScaled.rescale(newScale);
        }
        updateMouseState();
    }

    public void drawProgressionPart(float zLevel, int mouseX, int mouseY) {
        drawBackground(zLevel);

        drawClusters(zLevel);

        focusedClusterMouse = tryFocusCluster(mouseX, mouseY);

        float scaleX = this.mousePointScaled.getPosX();
        float scaleY = this.mousePointScaled.getPosY();

        if (sizeHandler.getScalingFactor() >= 6.1D && focusedClusterZoom != null && clusterRenderer != null) {
            JournalCluster cluster = JournalProgressionClusterMapping.getClusterMapping(focusedClusterZoom);
            drawClusterBackground(cluster.clusterBackgroundTexture, zLevel);

            clusterRenderer.drawClusterScreen(this.parentGui, zLevel);
            scaleX = clusterRenderer.getMouseX();
            scaleY = clusterRenderer.getMouseY();
        }

        if (focusedClusterMouse != null) {
            JournalCluster cluster = JournalProgressionClusterMapping.getClusterMapping(focusedClusterMouse);
            float width  = this.sizeHandler.scaledDistanceX(cluster.x, cluster.maxX);
            float height = this.sizeHandler.scaledDistanceY(cluster.y, cluster.maxY);
            Point.Float offset = this.sizeHandler.scalePointToGui(this.parentGui, this.mousePointScaled, new Point.Float(cluster.x, cluster.y));

            float scale = sizeHandler.getScalingFactor();
            float br = 1F;
            if (scale > 8.01F) {
                br = 0F;
            } else if (scale >= 6F) {
                br = 1F - ((scale - 6F) / 2F);
            }

            String name = focusedClusterMouse.getName().getFormattedText();
            float length = Minecraft.getInstance().fontRenderer.getStringWidth(name) * 1.4F;

            RenderSystem.pushMatrix();
            RenderSystem.translated(offset.x + (width / 2F) - length / 2D, offset.y + (height / 3F), 0);
            RenderSystem.scaled(1.4, 1.4, 1.4);
            int alpha = 0xCC;
            alpha *= br;
            alpha = Math.max(alpha, 5);
            int color = 0x5A28FF | (alpha << 24);
            RenderingDrawUtils.renderStringWithShadowAtCurrentPos(null, name, color);

            RenderSystem.popMatrix();
        }

        drawStarParallaxLayers(scaleX, scaleY, zLevel);
    }

    @Nullable
    private ResearchProgression tryFocusCluster(double mouseX, double mouseY) {
        for (Rectangle r : this.clusterRectMap.keySet()) {
            if (r.contains(mouseX, mouseY)) {
                return this.clusterRectMap.get(r);
            }
        }
        return null;
    }

    private void drawClusters(float zLevel) {
        clusterRectMap.clear();
        if (sizeHandler.getScalingFactor() >= 8.01) return;

        PlayerProgress thisProgress = ResearchHelper.getClientProgress();
        for (ResearchProgression progress : thisProgress.getResearchProgression()) {
            renderCluster(progress, JournalProgressionClusterMapping.getClusterMapping(progress), zLevel);
        }
    }

    private void renderCluster(ResearchProgression p, JournalCluster cluster, float zLevel) {
        Point.Float offset = this.sizeHandler.scalePointToGui(this.parentGui, this.mousePointScaled, new Point.Float(cluster.x, cluster.y));
        float width  = this.sizeHandler.scaledDistanceX(cluster.x, cluster.maxX);
        float height = this.sizeHandler.scaledDistanceY(cluster.y, cluster.maxY);

        Rectangle r = new Rectangle(MathHelper.floor(offset.x), MathHelper.floor(offset.y), MathHelper.floor(width), MathHelper.floor(height));
        clusterRectMap.put(r, p);

        cluster.cloudTexture.bindTexture();

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
        Blending.ADDITIVEDARK.apply();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            buf.pos(offset.x + 0,     offset.y + height, zLevel).color(br, br, br, br).tex(0, 1).endVertex();
            buf.pos(offset.x + width, offset.y + height, zLevel).color(br, br, br, br).tex(1, 1).endVertex();
            buf.pos(offset.x + width, offset.y + 0,      zLevel).color(br, br, br, br).tex(1, 0).endVertex();
            buf.pos(offset.x + 0,     offset.y + 0,      zLevel).color(br, br, br, br).tex(0, 0).endVertex();
        });

        Blending.DEFAULT.apply();
        RenderSystem.disableBlend();
    }

    private void drawClusterBackground(AbstractRenderableTexture tex, float zLevel) {
        float scale = sizeHandler.getScalingFactor();
        float br;
        if (scale > 8.01F) {
            br = 0.75F;
        } else if (scale >= 6F) {
            br = ((scale - 6F) / 2F) * 0.75F;
        } else {
            br = 0F;
        }

        tex.bindTexture();
        RenderSystem.enableBlend();
        Blending.ADDITIVEDARK.apply();

        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            buf.pos(realCoordLowerX,                   realCoordLowerY + realRenderHeight, zLevel).color(br, br, br, br).tex(0, 1).endVertex();
            buf.pos(realCoordLowerX + realRenderWidth, realCoordLowerY + realRenderHeight, zLevel).color(br, br, br, br).tex(1, 1).endVertex();
            buf.pos(realCoordLowerX + realRenderWidth, realCoordLowerY,                    zLevel).color(br, br, br, br).tex(1, 0).endVertex();
            buf.pos(realCoordLowerX,                   realCoordLowerY,                    zLevel).color(br, br, br, br).tex(0, 0).endVertex();
        });

        Blending.DEFAULT.apply();
        RenderSystem.disableBlend();
    }

    private void drawBackground(float zLevel) {
        float br = 0.35F;
        TexturesAS.TEX_GUI_BACKGROUND_DEFAULT.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            buf.pos(realCoordLowerX,                   realCoordLowerY + realRenderHeight, zLevel).color(br, br, br, 1.0F).tex(0, 1).endVertex();
            buf.pos(realCoordLowerX + realRenderWidth, realCoordLowerY + realRenderHeight, zLevel).color(br, br, br, 1.0F).tex(1, 1).endVertex();
            buf.pos(realCoordLowerX + realRenderWidth, realCoordLowerY,                    zLevel).color(br, br, br, 1.0F).tex(1, 0).endVertex();
            buf.pos(realCoordLowerX,                   realCoordLowerY,                    zLevel).color(br, br, br, 1.0F).tex(0, 0).endVertex();
        });
    }

    private void drawStarParallaxLayers(float scalePosX, float scalePosY, float zLevel) {
        TexturesAS.TEX_GUI_STARFIELD_OVERLAY.bindTexture();
        RenderSystem.enableBlend();
        Blending.OVERLAYDARK.apply();

        float offsetX = scalePosX / 2000F;
        float offsetY = scalePosY / 1000F;

        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            drawStarOverlay(buf, zLevel, offsetX, offsetY, 2F);
            drawStarOverlay(buf, zLevel, offsetX, offsetY, 1.5F);
            drawStarOverlay(buf, zLevel, offsetX, offsetY, 1F);
            drawStarOverlay(buf, zLevel, offsetX, offsetY, 0.75F);
            drawStarOverlay(buf, zLevel, offsetX, offsetY, 0.5F);
            drawStarOverlay(buf, zLevel, offsetX, offsetY, 0.3F);
        });

        Blending.DEFAULT.apply();
        RenderSystem.disableBlend();
    }

    private void drawStarOverlay(IVertexBuilder buf, float zLevel, float scalePosX, float scalePosY, float scaleFactor) {
        float scale = this.sizeHandler.getScalingFactor() / 40F;

        float x      = this.parentGui.getGuiLeft();
        float y      = this.parentGui.getGuiTop();
        float width  = this.parentGui.getGuiWidth();
        float height = this.parentGui.getGuiHeight();

        float u  = 0.2F + scalePosX + scaleFactor + scale;
        float v  = 0.2F + scalePosY + scaleFactor + scale;
        float uL = 0.6F * scaleFactor - (scale * 2);
        float vL = 0.6F * scaleFactor - (scale * 2);

        if (vL <= 0 || uL <= 0) {
            return;
        }

        buf.pos(x, y + height, zLevel)
                .color(0.75F, 0.75F, 0.75F, 0.7F).tex(u,  v + vL).endVertex();
        buf.pos(x + width, y + height, zLevel)
                .color(0.75F, 0.75F, 0.75F, 0.7F).tex(u + uL, v + vL).endVertex();
        buf.pos(x + width, y, zLevel)
                .color(0.75F, 0.75F, 0.75F, 0.7F).tex(u + uL, v).endVertex();
        buf.pos(x, y, zLevel)
                .color(0.75F, 0.75F, 0.75F, 0.7F).tex(u, v).endVertex();
    }
}
