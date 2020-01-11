/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.progression;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.screen.helper.ScalingPoint;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPages;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalProgression;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournalClusterRenderer
 * Created by HellFirePvP
 * Date: 03.08.2019 / 18:06
 */
public class ScreenJournalClusterRenderer {

    private ProgressionSizeHandler progressionSizeHandler;
    private ResearchProgression progression;
    private ScalingPoint mousePointScaled;
    private ScalingPoint previousMousePointScaled;

    private int renderOffsetX, renderOffsetY;
    private int renderGuiHeight, renderGuiWidth;
    private boolean hasPrevOffset = false;

    private float alpha = 1F;

    private Map<Rectangle, ResearchNode> clickableNodes = new HashMap<>();

    public ScreenJournalClusterRenderer(ResearchProgression progression, int guiHeight, int guiWidth, int guiLeft, int guiTop) {
        this.progression = progression;
        this.progressionSizeHandler = new ProgressionSizeHandler(progression, guiHeight, guiWidth);
        this.progressionSizeHandler.setMaxScale(1.2D);
        this.progressionSizeHandler.setMinScale(0.1D);
        this.progressionSizeHandler.setScaleSpeed(0.9D / 20D);
        this.progressionSizeHandler.updateSize();
        this.progressionSizeHandler.forceScaleTo(0.1D);

        this.mousePointScaled = ScalingPoint.createPoint(
                this.progressionSizeHandler.clampX(this.progressionSizeHandler.getMidX()),
                this.progressionSizeHandler.clampY(this.progressionSizeHandler.getMidY()),
                this.progressionSizeHandler.getScalingFactor(),
                false);
        this.renderOffsetX = guiLeft;
        this.renderOffsetY = guiTop;
        this.renderGuiHeight = guiHeight;
        this.renderGuiWidth = guiWidth;
    }

    public boolean propagateClick(ScreenJournalProgression parent, double mouseX, double mouseY) {
        Rectangle frame = new Rectangle(renderOffsetX, renderOffsetY, renderGuiWidth, renderGuiHeight);
        if (frame.contains(mouseX, mouseY)) {
            for (Rectangle r : clickableNodes.keySet()) {
                if (r.contains(mouseX, mouseY)) {
                    ResearchNode clicked = clickableNodes.get(r);
                    Minecraft.getInstance().displayGuiScreen(new ScreenJournalPages(parent, clicked));
                    return true;
                }
            }
        }
        return false;
    }

    public void drawMouseHighlight(float zLevel, int mouseX, int mouseY) {
        Rectangle frame = new Rectangle(renderOffsetX, renderOffsetY, renderGuiWidth, renderGuiHeight);
        if (frame.contains(mouseX, mouseY)) {
            for (Rectangle r : clickableNodes.keySet()) {
                if (r.contains(mouseX, mouseY)) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translated(r.getX(), r.getY(), 0);
                    GlStateManager.scaled(progressionSizeHandler.getScalingFactor(), progressionSizeHandler.getScalingFactor(), progressionSizeHandler.getScalingFactor());

                    String name = clickableNodes.get(r).getUnLocalizedName();
                    name = I18n.format(name);

                    RenderingDrawUtils.renderBlueTooltipString(0, 0, Lists.newArrayList(name), Minecraft.getInstance().fontRenderer, false);

                    GlStateManager.popMatrix();
                }
            }
        }
    }

    public void moveMouse(double changedX, double changedY) {
        if (hasPrevOffset) {
            mousePointScaled.updateScaledPos(
                    progressionSizeHandler.clampX(previousMousePointScaled.getScaledPosX() + changedX),
                    progressionSizeHandler.clampY(previousMousePointScaled.getScaledPosY() + changedY),
                    progressionSizeHandler.getScalingFactor());
        } else {
            mousePointScaled.updateScaledPos(
                    progressionSizeHandler.clampX(changedX),
                    progressionSizeHandler.clampY(changedY),
                    progressionSizeHandler.getScalingFactor());
        }
    }

    public void applyMovedMouseOffset() {
        this.previousMousePointScaled = ScalingPoint.createPoint(
                mousePointScaled.getScaledPosX(),
                mousePointScaled.getScaledPosY(),
                progressionSizeHandler.getScalingFactor(),
                true);
        this.hasPrevOffset = true;
    }

    public void handleZoomOut() {
        this.progressionSizeHandler.handleZoomOut();
        rescale(progressionSizeHandler.getScalingFactor());
    }

    public void handleZoomIn() {
        this.progressionSizeHandler.handleZoomIn();
        rescale(progressionSizeHandler.getScalingFactor());
    }

    public double getScaleMouseX() {
        return mousePointScaled.getScaledPosX();
    }

    public double getScaleMouseY() {
        return mousePointScaled.getScaledPosY();
    }

    private void rescale(double newScale) {
        this.mousePointScaled.rescale(newScale);
        if(this.previousMousePointScaled != null) {
            this.previousMousePointScaled.rescale(newScale);
        }
        moveMouse(0, 0);
    }

    public void drawClusterScreen(float zLevel) {
        clickableNodes.clear();

        drawNodesAndConnections(zLevel);
    }

    private void drawNodesAndConnections(float zLevel) {
        alpha = (float) Math.sqrt(progressionSizeHandler.getScalingFactor()); //Clamped between 0.1F and 1F

        double midX = renderGuiWidth  / 2;
        double midY = renderGuiHeight / 2;

        Map<ResearchNode, double[]> displayPositions = new HashMap<>();
        for (ResearchNode node : progression.getResearchNodes()) {
            if (!node.canSee(ResearchHelper.getClientProgress())) {
                continue;
            }
            int absX = node.renderPosX;
            int absZ = node.renderPosZ;
            double lX = midX + (absX * (progressionSizeHandler.getZoomedWHNode() + progressionSizeHandler.getZoomedSpaceBetweenNodes()));
            double lZ = midY + (absZ * (progressionSizeHandler.getZoomedWHNode() + progressionSizeHandler.getZoomedSpaceBetweenNodes()));

            renderConnectionLines(node, lX, lZ, midX, midY, zLevel);

            displayPositions.put(node, new double[] { lX, lZ });
        }
        for (ResearchNode node : displayPositions.keySet()) {
            double[] pos = displayPositions.get(node);
            renderNodeToGUI(node, pos[0], pos[1], zLevel);
        }
    }

    private void renderNodeToGUI(ResearchNode node, double lowerPosX, double lowerPosY, float zLevel) {
        double scaledLeft = this.mousePointScaled.getScaledPosX() - progressionSizeHandler.widthToBorder;
        double scaledTop =  this.mousePointScaled.getScaledPosY() - progressionSizeHandler.heightToBorder;
        double xAdd = lowerPosX - scaledLeft;
        double yAdd = lowerPosY - scaledTop;
        double offsetX = renderOffsetX + xAdd;
        double offsetY = renderOffsetY + yAdd;

        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        node.getBackgroundTexture().resolve().bindTexture();

        double zoomedWH = progressionSizeHandler.getZoomedWHNode();

        if (progressionSizeHandler.getScalingFactor() >= 0.7) {
            clickableNodes.put(new Rectangle(MathHelper.floor(offsetX), MathHelper.floor(offsetY), MathHelper.floor(zoomedWH), MathHelper.floor(zoomedWH)), node);
        }

        drawResearchItemBackground(zoomedWH, xAdd, yAdd, zLevel);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translated(offsetX, offsetY, 0);

        double pxWH = progressionSizeHandler.getZoomedWHNode() / 16D;

        ItemRenderer ri = Minecraft.getInstance().getItemRenderer();
        Tessellator t = Tessellator.getInstance();
        BufferBuilder vb = t.getBuffer();

        switch (node.getRenderType()) {
            case ITEMSTACK:
                GlStateManager.enableRescaleNormal();
                RenderHelper.enableGUIStandardItemLighting();

                GlStateManager.pushMatrix();
                GlStateManager.scaled(progressionSizeHandler.getScalingFactor(), progressionSizeHandler.getScalingFactor(), progressionSizeHandler.getScalingFactor());
                GlStateManager.translated(3, 3, 3);
                GlStateManager.scaled(0.75, 0.75, 0.75);
                GlStateManager.color4f(alpha, alpha, alpha, alpha);

                float oldZ = ri.zLevel;
                ri.zLevel = zLevel - 5;
                ri.renderItemIntoGUI(node.getRenderItemStack(ClientScheduler.getClientTick()), 0, 0);
                ri.zLevel = oldZ;
                GlStateManager.color4f(1F, 1F, 1F, 1F);
                GlStateManager.popMatrix();

                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
                break;
            case TEXTURE_SPRITE:
                Color col = node.getTextureColorHint();

                GlStateManager.disableAlphaTest();

                float r = (col.getRed() / 255F)   * alpha;
                float g = (col.getGreen() / 255F) * alpha;
                float b = (col.getBlue() / 255F)  * alpha;
                float a = (col.getAlpha() / 255F) * alpha;

                SpriteSheetResource res = node.getSpriteTexture().resolveSprite();
                res.getResource().bindTexture();
                Tuple<Double, Double> uvTexture = res.getUVOffset(ClientScheduler.getClientTick());

                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                vb.pos(pxWH, zoomedWH - pxWH, zLevel)
                        .tex(uvTexture.getA(), uvTexture.getB() + res.getVLength())
                        .color(r, g, b, a)
                        .endVertex();
                vb.pos(zoomedWH - pxWH, zoomedWH - pxWH, zLevel)
                        .tex(uvTexture.getA() + res.getULength(), uvTexture.getB() + res.getVLength())
                        .color(r, g, b, a)
                        .endVertex();
                vb.pos(zoomedWH - pxWH, pxWH, zLevel)
                        .tex(uvTexture.getA() + res.getULength(), uvTexture.getB())
                        .color(r, g, b, a)
                        .endVertex();
                vb.pos(pxWH, pxWH, zLevel)
                        .tex(uvTexture.getA(), uvTexture.getB())
                        .color(r, g, b, a)
                        .endVertex();
                t.draw();

                GlStateManager.enableAlphaTest();
                break;
            default:
                break;
        }

        GlStateManager.popMatrix();
    }

    private void renderConnectionLines(ResearchNode node, double lowerPosX, double lowerPosY, double midX, double midY, float zLevel) {
        double xAdd = (lowerPosX - (this.mousePointScaled.getScaledPosX() - progressionSizeHandler.widthToBorder)) + progressionSizeHandler.getZoomedWHNode() / 2;
        double yAdd = (lowerPosY - (this.mousePointScaled.getScaledPosY() - progressionSizeHandler.heightToBorder)) + progressionSizeHandler.getZoomedWHNode() / 2;
        for (ResearchNode other : node.getConnectionsTo()) {
            renderConnection(other, xAdd, yAdd, midX, midY, zLevel);
        }
    }

    private void renderConnection(ResearchNode to, double fromX, double fromY, double midX, double midY, float zLevel) {
        double relToX = midX + (to.renderPosX * (progressionSizeHandler.getZoomedWHNode() + progressionSizeHandler.getZoomedSpaceBetweenNodes()));
        double relToY = midY + (to.renderPosZ * (progressionSizeHandler.getZoomedWHNode() + progressionSizeHandler.getZoomedSpaceBetweenNodes()));
        double targetXOffset = (relToX - (this.mousePointScaled.getScaledPosX() - progressionSizeHandler.widthToBorder)) +  (progressionSizeHandler.getZoomedWHNode() / 2);
        double targetYOffset = (relToY - (this.mousePointScaled.getScaledPosY() - progressionSizeHandler.heightToBorder)) + (progressionSizeHandler.getZoomedWHNode() / 2);
        drawConnection(fromX, fromY, targetXOffset, targetYOffset, zLevel);
    }

    private void drawConnection(double originX, double originY, double targetX, double targetY, float zLevel) {
        GlStateManager.pushMatrix();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        GlStateManager.disableTexture();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.lineWidth(3.5F);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        long clientTicks = ClientScheduler.getClientTick();

        Vector3 origin = new Vector3(originX, originY, 0);
        Vector3 line = origin.vectorFromHereTo(targetX, targetY, 0);
        int segments = (int) Math.ceil(line.length() / 1); //1 = max line segment length
        int activeSegment = (int) (clientTicks % segments);
        Vector3 segmentIter = line.divide(segments);
        for (int i = segments; i >= 0; i--) {
            double lx = origin.getX();
            double ly = origin.getY();
            origin.add(segmentIter);

            float brightness = 0.4F;
            brightness += (0.6F * evaluateBrightness(i, activeSegment));

            drawLinePart(lx, ly, origin.getX(), origin.getY(), zLevel, brightness);
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.lineWidth(2.0F);
        GlStateManager.enableTexture();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.popMatrix();
    }

    private void drawLinePart(double lx, double ly, double hx, double hy, double zLevel, float brightness) {
        lx += renderOffsetX;
        ly += renderOffsetY;
        hx += renderOffsetX;
        hy += renderOffsetY;
        brightness *= alpha;
        GL11.glColor4f(brightness, brightness, brightness, 0.5F * alpha);
        Tessellator t = Tessellator.getInstance();
        BufferBuilder vb = t.getBuffer();
        vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        vb.pos(lx, ly, zLevel).endVertex();
        vb.pos(hx, hy, zLevel).endVertex();
        t.draw();
    }

    private float evaluateBrightness(int segment, int activeSegment) {
        if (segment == activeSegment) return 1.0F;
        float res = ((float) (10 - Math.abs(activeSegment - segment))) / 10F;
        return Math.max(0, res);
    }


    private void drawResearchItemBackground(double zoomedWH, double xAdd, double yAdd, float zLevel) {
        Tessellator t = Tessellator.getInstance();
        BufferBuilder vb = t.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        vb.pos(renderOffsetX + xAdd,            renderOffsetY + yAdd + zoomedWH, zLevel).tex(0, 1).color(alpha, alpha, alpha, alpha).endVertex();
        vb.pos(renderOffsetX + xAdd + zoomedWH, renderOffsetY + yAdd + zoomedWH, zLevel).tex(1, 1).color(alpha, alpha, alpha, alpha).endVertex();
        vb.pos(renderOffsetX + xAdd + zoomedWH, renderOffsetY + yAdd,            zLevel).tex(1, 0).color(alpha, alpha, alpha, alpha).endVertex();
        vb.pos(renderOffsetX + xAdd,            renderOffsetY + yAdd,            zLevel).tex(0, 0).color(alpha, alpha, alpha, alpha).endVertex();
        t.draw();
        GlStateManager.color4f(1F, 1F, 1F, 1F);
    }

}
