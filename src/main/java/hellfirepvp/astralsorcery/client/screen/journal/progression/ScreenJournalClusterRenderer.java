/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.progression;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.screen.base.WidthHeightScreen;
import hellfirepvp.astralsorcery.client.screen.helper.ScalingPoint;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPages;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalProgression;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextProperties;
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
        this.progressionSizeHandler = new ProgressionSizeHandler(progression);
        this.progressionSizeHandler.setMaxScale(1.2F);
        this.progressionSizeHandler.setMinScale(0.1F);
        this.progressionSizeHandler.setScaleSpeed(0.9F / 20F);
        this.progressionSizeHandler.updateSize();
        this.progressionSizeHandler.forceScaleTo(0.1F);

        this.mousePointScaled = ScalingPoint.createPoint(0, 0, this.progressionSizeHandler.getScalingFactor(), false);
        this.centerMouse();
        this.applyMovedMouseOffset();

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

    public void drawMouseHighlight(MatrixStack renderStack, float zLevel, int mouseX, int mouseY) {
        Rectangle frame = new Rectangle(renderOffsetX, renderOffsetY, renderGuiWidth, renderGuiHeight);
        if (frame.contains(mouseX, mouseY)) {
            for (Rectangle r : clickableNodes.keySet()) {
                if (r.contains(mouseX, mouseY)) {
                    ITextProperties name = clickableNodes.get(r).getName();

                    renderStack.push();
                    renderStack.translate(r.getX(), r.getY(), zLevel + 200);
                    renderStack.scale(progressionSizeHandler.getScalingFactor(), progressionSizeHandler.getScalingFactor(), 1F);
                    RenderingDrawUtils.renderBlueTooltipComponents(renderStack, 0, 0, 0, Lists.newArrayList(name), Minecraft.getInstance().fontRenderer, false);
                    renderStack.pop();
                }
            }
        }
    }

    public void centerMouse() {
        Point.Float center = this.progressionSizeHandler.getRelativeCenter();
        this.moveMouse(center.x, center.y);
    }

    public void moveMouse(float changedX, float changedY) {
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

    public float getMouseX() {
        return mousePointScaled.getPosX();
    }

    public float getMouseY() {
        return mousePointScaled.getPosY();
    }

    private void rescale(float newScale) {
        this.mousePointScaled.rescale(newScale);
        if (this.previousMousePointScaled != null) {
            this.previousMousePointScaled.rescale(newScale);
        }
        moveMouse(0, 0);
    }

    public void drawClusterScreen(MatrixStack renderStack, WidthHeightScreen parentGui, float zLevel) {
        clickableNodes.clear();

        drawNodesAndConnections(renderStack, parentGui, zLevel);
    }

    private void drawNodesAndConnections(MatrixStack renderStack, WidthHeightScreen parentGui, float zLevel) {
        alpha = progressionSizeHandler.getScalingFactor(); //between 0.25F and ~1F
        alpha -= 0.25F;
        alpha /= 0.75F;
        alpha = MathHelper.clamp(alpha, 0F, 1F);

        Map<ResearchNode, Point.Float> displayPositions = new HashMap<>();
        for (ResearchNode node : progression.getResearchNodes()) {
            if (!node.canSee(ResearchHelper.getClientProgress())) {
                continue;
            }
            Point.Float from = this.progressionSizeHandler.scalePointToGui(parentGui, this.mousePointScaled, new Point.Float(node.renderPosX, node.renderPosZ));
            for (ResearchNode target : node.getConnectionsTo()) {
                Point.Float to = this.progressionSizeHandler.scalePointToGui(parentGui, this.mousePointScaled, new Point.Float(target.renderPosX, target.renderPosZ));
                drawConnection(renderStack, from.x, from.y, to.x, to.y, zLevel);
            }

            displayPositions.put(node, from);
        }
        displayPositions.forEach((node, pos) -> renderNodeToGUI(renderStack, node, pos, zLevel));
    }

    private void renderNodeToGUI(MatrixStack renderStack, ResearchNode node, Point.Float offset, float zLevel) {
        float zoomedWH = progressionSizeHandler.getZoomedWHNode();
        float offsetX = offset.x - zoomedWH / 2F;
        float offsetY = offset.y - zoomedWH / 2F;

        node.getBackgroundTexture().resolve().bindTexture();
        if (progressionSizeHandler.getScalingFactor() >= 0.7) {
            clickableNodes.put(new Rectangle(MathHelper.floor(offsetX), MathHelper.floor(offsetY), MathHelper.floor(zoomedWH), MathHelper.floor(zoomedWH)), node);
        }
        drawResearchItemBackground(zoomedWH, offsetX, offsetY, zLevel);

        float pxWH = progressionSizeHandler.getZoomedWHNode() / 16F;

        switch (node.getNodeRenderType()) {
            case ITEMSTACK:
                renderStack.push();
                renderStack.translate(offsetX, offsetY, 0);
                renderStack.scale(progressionSizeHandler.getScalingFactor(), progressionSizeHandler.getScalingFactor(), 1);
                renderStack.translate(3, 3, 100);
                renderStack.scale(0.75F, 0.75F, 1);

                RenderHelper.enableStandardItemLighting();
                RenderingUtils.renderTranslucentItemStackModelGUI(node.getRenderItemStack(ClientScheduler.getClientTick()),
                        renderStack, Color.WHITE, Blending.DEFAULT, MathHelper.clamp((int) (alpha * 255F), 0, 255));
                RenderHelper.disableStandardItemLighting();

                renderStack.pop();
                break;
            case TEXTURE_SPRITE:
                Color col = node.getTextureColorHint();

                float r = (col.getRed() / 255F)   * alpha;
                float g = (col.getGreen() / 255F) * alpha;
                float b = (col.getBlue() / 255F)  * alpha;
                float a = (col.getAlpha() / 255F) * alpha;

                SpriteSheetResource res = node.getSpriteTexture().resolveSprite();
                res.getResource().bindTexture();
                Tuple<Float, Float> uvTexture = res.getUVOffset(ClientScheduler.getClientTick());

                renderStack.push();
                renderStack.translate(offsetX, offsetY, 0);

                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();

                RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
                    Matrix4f matr = renderStack.getLast().getMatrix();
                    buf.pos(matr, pxWH, zoomedWH - pxWH, zLevel)
                            .color(r, g, b, a)
                            .tex(uvTexture.getA(), uvTexture.getB() + res.getVLength())
                            .endVertex();
                    buf.pos(matr, zoomedWH - pxWH, zoomedWH - pxWH, zLevel)
                            .color(r, g, b, a)
                            .tex(uvTexture.getA() + res.getULength(), uvTexture.getB() + res.getVLength())
                            .endVertex();
                    buf.pos(matr, zoomedWH - pxWH, pxWH, zLevel)
                            .color(r, g, b, a)
                            .tex(uvTexture.getA() + res.getULength(), uvTexture.getB())
                            .endVertex();
                    buf.pos(matr, pxWH, pxWH, zLevel)
                            .color(r, g, b, a)
                            .tex(uvTexture.getA(), uvTexture.getB())
                            .endVertex();
                });

                RenderSystem.defaultBlendFunc();
                RenderSystem.disableBlend();

                renderStack.pop();
                break;
            default:
                break;
        }
    }

    private void drawConnection(MatrixStack renderStack, float originX, float originY, float targetX, float targetY, float zLevel) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.lineWidth(4F);

        long clientTicks = ClientScheduler.getClientTick();
        Vector3 origin = new Vector3(originX, originY, 0);
        Vector3 line = origin.vectorFromHereTo(targetX, targetY, 0);
        int segments = (int) Math.ceil(line.length() / 1); //1 = max line segment length
        int activeSegment = (int) (clientTicks % segments);
        Vector3 segmentIter = line.divide(segments);
        RenderingUtils.draw(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR, buf -> {
            for (int i = segments; i >= 0; i--) {
                double lx = origin.getX();
                double ly = origin.getY();
                origin.add(segmentIter);

                float brightness = 0.6F;
                brightness += (0.4F * evaluateBrightness(i, activeSegment));

                drawLinePart(buf, renderStack, lx, ly, origin.getX(), origin.getY(), zLevel, brightness);
            }
        });

        RenderSystem.lineWidth(2.0F);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    private void drawLinePart(IVertexBuilder buf, MatrixStack renderStack, double lx, double ly, double hx, double hy, float zLevel, float brightness) {
        Matrix4f offset = renderStack.getLast().getMatrix();
        buf.pos(offset, (float) lx, (float) ly, zLevel)
                .color(brightness * alpha, brightness * alpha, brightness * alpha, 0.4F * alpha)
                .endVertex();
        buf.pos(offset, (float) hx, (float) hy, zLevel)
                .color(brightness * alpha, brightness * alpha, brightness * alpha, 0.4F * alpha)
                .endVertex();
    }

    private float evaluateBrightness(int segment, int activeSegment) {
        if (segment == activeSegment) return 1.0F;
        float res = ((float) (10 - Math.abs(activeSegment - segment))) / 10F;
        return Math.max(0, res);
    }

    private void drawResearchItemBackground(double zoomedWH, double xAdd, double yAdd, float zLevel) {
        RenderSystem.enableBlend();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            buf.pos(xAdd,            yAdd + zoomedWH, zLevel).color(alpha, alpha, alpha, alpha).tex(0, 1).endVertex();
            buf.pos(xAdd + zoomedWH, yAdd + zoomedWH, zLevel).color(alpha, alpha, alpha, alpha).tex(1, 1).endVertex();
            buf.pos(xAdd + zoomedWH, yAdd,            zLevel).color(alpha, alpha, alpha, alpha).tex(1, 0).endVertex();
            buf.pos(xAdd,            yAdd,            zLevel).color(alpha, alpha, alpha, alpha).tex(0, 0).endVertex();
        });
        RenderSystem.disableBlend();
    }
}
