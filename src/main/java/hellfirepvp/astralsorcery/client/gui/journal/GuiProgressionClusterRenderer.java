package hellfirepvp.astralsorcery.client.gui.journal;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.gui.GuiJournalProgression;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiProgressionClusterRenderer
 * Created by HellFirePvP
 * Date: 13.08.2016 / 12:03
 */
public class GuiProgressionClusterRenderer {

    private static final BindableResource frameBlank = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "frameBlank");
    private static final BindableResource frameWooden = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "frameWooden");

    private PartSizeHandler partSizeHandler;
    private ResearchProgression progression;
    private ScalingPoint mousePointScaled;
    private ScalingPoint previousMousePointScaled;

    private int renderOffsetX, renderOffsetY;
    private int renderGuiHeight, renderGuiWidth;
    private boolean hasPrevOffset = false;

    private float renderLoopBrFactor = 1F;

    private Map<Rectangle, ResearchNode> clickableNodes = new HashMap<>();

    public GuiProgressionClusterRenderer(ResearchProgression progression, int guiHeight, int guiWidth, int guiLeft, int guiTop) {
        this.progression = progression;
        this.partSizeHandler = new PartSizeHandler(progression, guiHeight, guiWidth);
        this.partSizeHandler.setMaxScale(1.0D);
        this.partSizeHandler.setMinScale(0.1D);
        this.partSizeHandler.setScaleSpeed(0.9D / 20D);
        this.partSizeHandler.updateSize();
        this.partSizeHandler.forceScaleTo(0.1D);

        this.mousePointScaled = ScalingPoint.createPoint(
                this.partSizeHandler.clampX(this.partSizeHandler.getMidX()),
                this.partSizeHandler.clampY(this.partSizeHandler.getMidY()),
                this.partSizeHandler.getScalingFactor(),
                false);
        this.renderOffsetX = guiLeft;
        this.renderOffsetY = guiTop;
        this.renderGuiHeight = guiHeight;
        this.renderGuiWidth = guiWidth;
    }

    public void propagateClick(GuiJournalProgression parent, Point p) {
        Rectangle frame = new Rectangle(renderOffsetX, renderOffsetY, renderGuiWidth, renderGuiHeight);
        if(frame.contains(p)) {
            for (Rectangle r : clickableNodes.keySet()) {
                if(r.contains(p)) {
                    ResearchNode clicked = clickableNodes.get(r);
                    Minecraft.getMinecraft().displayGuiScreen(new GuiJournalPages(parent, clicked));
                }
            }
        }
    }

    public void drawMouseHighlight(float zLevel, Point mousePoint) {
        Rectangle frame = new Rectangle(renderOffsetX, renderOffsetY, renderGuiWidth, renderGuiHeight);
        if(frame.contains(mousePoint)) {
            for (Rectangle r : clickableNodes.keySet()) {
                if(r.contains(mousePoint)) {
                    GL11.glPushMatrix();
                    GL11.glTranslated(r.getX(), r.getY(), 0);
                    GL11.glScaled(partSizeHandler.getScalingFactor(), partSizeHandler.getScalingFactor(), partSizeHandler.getScalingFactor());
                    String name = clickableNodes.get(r).getUnLocalizedName();
                    name = I18n.translateToLocal(name);
                    RenderingUtils.renderTooltip(0, 0, Lists.newArrayList(name), new Color(0x00100033), new Color(0xf0100010), Minecraft.getMinecraft().fontRendererObj);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    public void moveMouse(double changedX, double changedY) {
        if (hasPrevOffset) {
            mousePointScaled.updateScaledPos(
                    partSizeHandler.clampX(previousMousePointScaled.getScaledPosX() + changedX),
                    partSizeHandler.clampY(previousMousePointScaled.getScaledPosY() + changedY),
                    partSizeHandler.getScalingFactor());
        } else {
            mousePointScaled.updateScaledPos(
                    partSizeHandler.clampX(changedX),
                    partSizeHandler.clampY(changedY),
                    partSizeHandler.getScalingFactor());
        }
    }

    public void applyMovedMouseOffset() {
        this.previousMousePointScaled = ScalingPoint.createPoint(
                mousePointScaled.getScaledPosX(),
                mousePointScaled.getScaledPosY(),
                partSizeHandler.getScalingFactor(),
                true);
        this.hasPrevOffset = true;
    }

    public void handleZoomOut() {
        this.partSizeHandler.handleZoomOut();
        rescale(partSizeHandler.getScalingFactor());
    }

    public void handleZoomIn() {
        this.partSizeHandler.handleZoomIn();
        rescale(partSizeHandler.getScalingFactor());
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
        renderLoopBrFactor = (float) Math.sqrt(partSizeHandler.getScalingFactor()); //Clamped between 0.1F and 1F

        double midX = renderGuiWidth  / 2;
        double midY = renderGuiHeight / 2;
        Map<ResearchNode, double[]> displayPositions = new HashMap<>();
        for (ResearchNode node : progression.getResearchNodes()) {
            int absX = node.renderPosX;
            int absZ = node.renderPosZ;
            double lX = midX + (absX * (partSizeHandler.getZoomedWHNode() + partSizeHandler.getZoomedSpaceBetweenNodes()));
            double lZ = midY + (absZ * (partSizeHandler.getZoomedWHNode() + partSizeHandler.getZoomedSpaceBetweenNodes()));

            renderConnectionLines(node, lX, lZ, midX, midY, zLevel);

            displayPositions.put(node, new double[] { lX, lZ });
        }
        for (ResearchNode node : displayPositions.keySet()) {
            double[] pos = displayPositions.get(node);
            renderNodeToGUI(node, pos[0], pos[1], zLevel);
        }
    }

    private void renderNodeToGUI(ResearchNode node, double lowerPosX, double lowerPosY, float zLevel) {
        double scaledLeft = this.mousePointScaled.getScaledPosX() - partSizeHandler.widthToBorder;
        double scaledTop =  this.mousePointScaled.getScaledPosY() - partSizeHandler.heightToBorder;
        double xAdd = lowerPosX - scaledLeft;
        double yAdd = lowerPosY - scaledTop;
        double offsetX = renderOffsetX + xAdd;
        double offsetY = renderOffsetY + yAdd;

        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        /*if(node.isSpecial()) {
            frameWooden.bind();
        } else {
            frameBlank.bind();
        }*/
        frameWooden.bind();

        double zoomedWH = partSizeHandler.getZoomedWHNode();

        if(partSizeHandler.getScalingFactor() >= 0.7) {
            clickableNodes.put(new Rectangle(MathHelper.floor_double(offsetX), MathHelper.floor_double(offsetY), MathHelper.floor_double(zoomedWH), MathHelper.floor_double(zoomedWH)), node);
        }

        drawResearchItemBackground(zoomedWH, xAdd, yAdd, zLevel);
        TextureHelper.refreshTextureBindState();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(offsetX, offsetY, 0);

        RenderItem ri = Minecraft.getMinecraft().getRenderItem();
        Tessellator t = Tessellator.getInstance();
        VertexBuffer vb = t.getBuffer();
        switch (node.getRenderType()) {
            case ITEMSTACK:
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                RenderHelper.enableGUIStandardItemLighting();
                GL11.glPushMatrix();
                GL11.glScaled(partSizeHandler.getScalingFactor(), partSizeHandler.getScalingFactor(), partSizeHandler.getScalingFactor());
                GL11.glTranslated(3, 3, 3);
                GL11.glScaled(0.75, 0.75, 0.75);
                GL11.glColor4f(renderLoopBrFactor, renderLoopBrFactor, renderLoopBrFactor, renderLoopBrFactor);
                float oldZ = ri.zLevel;
                ri.zLevel = zLevel - 5;
                ri.renderItemIntoGUI(node.getRenderItemStack(ClientScheduler.getClientTick()), 0, 0);
                ri.zLevel = oldZ;
                GL11.glColor4f(1F, 1F, 1F, 1F);
                GL11.glPopMatrix();
                RenderHelper.disableStandardItemLighting();
                GL11.glPopAttrib();
                break;
            case TEXTURE:
                GL11.glColor4f(renderLoopBrFactor, renderLoopBrFactor, renderLoopBrFactor, renderLoopBrFactor);
                node.getTexture().bind();
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                vb.pos(0,            zoomedWH - 1, zLevel).tex(0, 1).endVertex();
                vb.pos(zoomedWH - 1, zoomedWH - 1, zLevel).tex(1, 1).endVertex();
                vb.pos(zoomedWH - 1, 0,            zLevel).tex(1, 0).endVertex();
                vb.pos(0,            0,            zLevel).tex(0, 0).endVertex();
                t.draw();
                GL11.glColor4f(1F, 1F, 1F, 1F);
                TextureHelper.refreshTextureBindState();
                break;
        }
        GL11.glPopMatrix();
    }

    private void renderConnectionLines(ResearchNode node, double lowerPosX, double lowerPosY, double midX, double midY, float zLevel) {
        double xAdd = (lowerPosX - (this.mousePointScaled.getScaledPosX() - partSizeHandler.widthToBorder)) + partSizeHandler.getZoomedWHNode() / 2;
        double yAdd = (lowerPosY - (this.mousePointScaled.getScaledPosY() - partSizeHandler.heightToBorder)) + partSizeHandler.getZoomedWHNode() / 2;
        for (ResearchNode other : node.getConnectionsTo()) {
            renderConnection(other, xAdd, yAdd, midX, midY, zLevel);
        }
    }

    private void renderConnection(ResearchNode to, double fromX, double fromY, double midX, double midY, float zLevel) {
        double relToX = midX + (to.renderPosX * (partSizeHandler.getZoomedWHNode() + partSizeHandler.getZoomedSpaceBetweenNodes()));
        double relToY = midY + (to.renderPosZ * (partSizeHandler.getZoomedWHNode() + partSizeHandler.getZoomedSpaceBetweenNodes()));
        double targetXOffset = (relToX - (this.mousePointScaled.getScaledPosX() - partSizeHandler.widthToBorder)) +  (partSizeHandler.getZoomedWHNode() / 2);
        double targetYOffset = (relToY - (this.mousePointScaled.getScaledPosY() - partSizeHandler.heightToBorder)) + (partSizeHandler.getZoomedWHNode() / 2);
        drawConnection(fromX, fromY, targetXOffset, targetYOffset, zLevel);
    }

    private void drawConnection(double originX, double originY, double targetX, double targetY, float zLevel) {
        GL11.glPushMatrix();
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(3.0F);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        long clientTicks = EffectHandler.getClientEffectTick();

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

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glPopMatrix();
    }

    private void drawLinePart(double lx, double ly, double hx, double hy, double zLevel, float brightness) {
        lx += renderOffsetX;
        ly += renderOffsetY;
        hx += renderOffsetX;
        hy += renderOffsetY;
        brightness *= renderLoopBrFactor;
        GL11.glColor4f(brightness, brightness, brightness, 0.5F * renderLoopBrFactor);
        Tessellator t = Tessellator.getInstance();
        VertexBuffer vb = t.getBuffer();
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
        GL11.glColor4f(renderLoopBrFactor, renderLoopBrFactor, renderLoopBrFactor, renderLoopBrFactor);
        Tessellator t = Tessellator.getInstance();
        VertexBuffer vb = t.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(renderOffsetX + xAdd,            renderOffsetY + yAdd + zoomedWH, zLevel).tex(0, 1).endVertex();
        vb.pos(renderOffsetX + xAdd + zoomedWH, renderOffsetY + yAdd + zoomedWH, zLevel).tex(1, 1).endVertex();
        vb.pos(renderOffsetX + xAdd + zoomedWH, renderOffsetY + yAdd,            zLevel).tex(1, 0).endVertex();
        vb.pos(renderOffsetX + xAdd,            renderOffsetY + yAdd,            zLevel).tex(0, 0).endVertex();
        t.draw();
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

}
