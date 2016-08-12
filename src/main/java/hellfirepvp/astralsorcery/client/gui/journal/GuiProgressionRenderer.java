package hellfirepvp.astralsorcery.client.gui.journal;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.util.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.AssetLoader;
import hellfirepvp.astralsorcery.client.util.BindableResource;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiProgressionRenderer
 * Created by HellFirePvP
 * Date: 12.08.2016 / 17:31
 */
public class GuiProgressionRenderer {

    private static final BindableResource textureResBack = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJResBG");
    private static final BindableResource textureResOVL = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJResOverlay");
    private static final BindableResource frameBlank = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "frameBlank");
    private static final BindableResource frameWooden = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "frameWooden");

    private BGSizeHandler sizeHandler;

    public GuiRenderBoundingBox renderBox;
    private int renderOffsetX, renderOffsetY;
    private int renderWidth, renderHeight;
    private int leftOffset, topOffset;
    private int prevMouseX, prevMouseY;
    private boolean hasPrevOffset = false;

    private Map<Vector3, ResearchNode> rectMapNodes = new HashMap<>();

    public GuiProgressionRenderer(int guiHeight, int guiWidth) {
        this.sizeHandler = new BGSizeHandler(guiHeight, guiWidth);
    }

    public void refreshSize() {
        this.sizeHandler.updateSize();
    }

    public void setBox(int left, int top, int right, int bottom) {
        this.renderBox = new GuiRenderBoundingBox(left, top, right, bottom);
        this.renderWidth = (int) this.renderBox.getWidth();
        this.renderHeight = (int) this.renderBox.getHeight();
    }

    public void moveMouse(int changedX, int changedY) {
        if (hasPrevOffset) {
            this.leftOffset = sizeHandler.adjustX(prevMouseX + changedX);
            this.topOffset = sizeHandler.adjustY(prevMouseY + changedY);
        } else {
            this.leftOffset = sizeHandler.adjustX(changedX);
            this.topOffset = sizeHandler.adjustY(changedY);
        }
    }

    public void applyMovedMouseOffset() {
        this.prevMouseX = leftOffset;
        this.prevMouseY = topOffset;
        this.hasPrevOffset = true;
    }

    public void updateOffset(int guiLeft, int guiTop) {
        this.renderOffsetX = guiLeft;
        this.renderOffsetY = guiTop;
    }

    public void centerMouse() {
        moveMouse(sizeHandler.getMidX(), sizeHandler.getMidY());
    }

    public void drawBackgroundScreen(float zLevel) {
        drawCurrentBackground(zLevel);

        drawNodesAndConnections(zLevel);

        drawCurrentBackgroundBlended(zLevel, 1.5);
        drawCurrentBackgroundBlended(zLevel, 2.5);
        drawCurrentBackgroundBlended(zLevel, 3.5);
    }

    private void drawCurrentBackground(float zLevel) {
        GL11.glColor4f(0.2F, 0.2F, 0.2F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        textureResBack.bind();

        float lowU = (((float) this.leftOffset) - sizeHandler.widthToBorder) / ((float) sizeHandler.getTotalWidth());
        float highU = lowU + (((float) renderWidth) / ((float) sizeHandler.getTotalWidth()));
        float lowV = (((float) this.topOffset) - sizeHandler.heightToBorder) / ((float) sizeHandler.getTotalHeight());
        float highV = lowV + (((float) renderHeight) / ((float) sizeHandler.getTotalHeight()));

        VertexBuffer vb = Tessellator.getInstance().getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(renderOffsetX,               renderOffsetY + renderHeight, zLevel).tex(lowU,  highV).endVertex();
        vb.pos(renderOffsetX + renderWidth, renderOffsetY + renderHeight, zLevel).tex(highU, highV).endVertex();
        vb.pos(renderOffsetX + renderWidth, renderOffsetY,                zLevel).tex(highU, lowV) .endVertex();
        vb.pos(renderOffsetX,               renderOffsetY,                zLevel).tex(lowU,  lowV) .endVertex();
        Tessellator.getInstance().draw();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    private void drawCurrentBackgroundBlended(float zLevel, double scaleFactor) {
        GL11.glColor4f(0.6F, 0.6F, 0.6F, 0.5F);
        GL11.glPushMatrix();
        GL11.glScaled(scaleFactor, scaleFactor, scaleFactor);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        textureResOVL.bind();

        float lowU = (((float) this.leftOffset) - sizeHandler.widthToBorder) / ((float) sizeHandler.getTotalWidth());
        float highU = lowU + (((float) renderWidth) / ((float) sizeHandler.getTotalWidth()));
        float lowV = (((float) this.topOffset) - sizeHandler.heightToBorder) / ((float) sizeHandler.getTotalHeight());
        float highV = lowV + (((float) renderHeight) / ((float) sizeHandler.getTotalHeight()));

        VertexBuffer vb = Tessellator.getInstance().getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(0,           renderHeight, zLevel).tex(lowU,  highV).endVertex();
        vb.pos(renderWidth, renderHeight, zLevel).tex(highU, highV).endVertex();
        vb.pos(renderWidth, 0,            zLevel).tex(highU, lowV) .endVertex();
        vb.pos(0,           0,            zLevel).tex(lowU,  lowV) .endVertex();
        Tessellator.getInstance().draw();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPopMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    private void drawNodesAndConnections(float zLevel) {
        int left = this.leftOffset - sizeHandler.widthToBorder;
        int top = this.topOffset - sizeHandler.heightToBorder;
        GuiRenderBoundingBox renderBox = new GuiRenderBoundingBox(left, top, left + renderWidth, top + renderHeight);
        PlayerProgress thisProgress = ResearchManager.clientProgress;
        Map<ResearchNode, int[]> display = new HashMap<>();
        for (ResearchProgression progress : thisProgress.getResearchProgression()) {
            for (ResearchNode node : progress.getResearchNodes()) {
                int absX = node.renderPosX;
                int absZ = node.renderPosZ;
                int lX = sizeHandler.evRelativePosX(absX, false);
                int rX = sizeHandler.evRelativePosX(absX, true);
                int lZ = sizeHandler.evRelativePosY(absZ, false);
                int rZ = sizeHandler.evRelativePosY(absZ, true);
                int[] positions = new int[] { lX, lZ, rX, rZ };
                //Use || for "Render as long as there is 1 point of the graphic inside the screen"
                //Use && for "Render only if its fully inside the screen"
                if (renderBox.isInBox(lX, lZ) ||
                        renderBox.isInBox(rX, rZ)) {

                    display.put(node, positions);
                }
                renderConnectionLines(node, positions, zLevel);
            }
        }

        rectMapNodes.clear();
        for (Map.Entry<ResearchNode, int[]> nodeEntry : display.entrySet()) {
            renderNodeToGUI(nodeEntry.getKey(), nodeEntry.getValue(), zLevel);
        }
    }

    private void renderNodeToGUI(ResearchNode node, int[] position, float zLevel) {
        int left = (this.leftOffset - sizeHandler.widthToBorder);
        int top = (this.topOffset - sizeHandler.heightToBorder);
        int xAdd = position[0] - left;
        int yAdd = position[1] - top;

        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_BLEND);

        /*if (state == RenderState.HALF && item.researchable()) {
            GL11.glColor4f(0.1F, 0.1F, 0.1F, 0.5F);
        } else {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }*/
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        rectMapNodes.put(new Vector3(renderOffsetX + xAdd, renderOffsetY + yAdd, 0), node);

        if(node.isSpecial()) {
            frameWooden.bind();
        } else {
            frameBlank.bind();
        }

        drawResearchItemBackground(xAdd, yAdd, zLevel);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(renderOffsetX + xAdd + 1, renderOffsetY + yAdd + 1, 0);

        RenderItem ri = Minecraft.getMinecraft().getRenderItem();
        Tessellator t = Tessellator.getInstance();
        VertexBuffer vb = t.getBuffer();
        switch (node.getRenderType()) {
            case ITEMSTACK:
                RenderHelper.enableGUIStandardItemLighting();
                GL11.glPushMatrix();
                GL11.glTranslated(2, 2, 2);
                GL11.glScaled(0.75, 0.75, 0.75);
                float oldZ = ri.zLevel;
                ri.zLevel = zLevel - 5;
                ri.renderItemIntoGUI(node.getRenderItemStack(), 0, 0);
                ri.zLevel = oldZ;
                GL11.glPopMatrix();
                RenderHelper.disableStandardItemLighting();
                break;
            case TEXTURE:
                node.getTexture().bind();
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                vb.pos(0,                          BGSizeHandler.W_H_NODE - 1, zLevel).tex(0, 1).endVertex();
                vb.pos(BGSizeHandler.W_H_NODE - 1, BGSizeHandler.W_H_NODE - 1, zLevel).tex(1, 1).endVertex();
                vb.pos(BGSizeHandler.W_H_NODE - 1, 0,                          zLevel).tex(1, 0).endVertex();
                vb.pos(0,                          0,                          zLevel).tex(0, 0).endVertex();
                t.draw(); //TODO test some time.
                break;
        }
        GL11.glPopMatrix();
    }

    private void drawResearchItemBackground(int xAdd, int yAdd, float zLevel) {
        Tessellator t = Tessellator.getInstance();
        VertexBuffer vb = t.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(renderOffsetX + xAdd,                          renderOffsetY + yAdd + BGSizeHandler.W_H_NODE, zLevel).tex(0, 1).endVertex();
        vb.pos(renderOffsetX + xAdd + BGSizeHandler.W_H_NODE, renderOffsetY + yAdd + BGSizeHandler.W_H_NODE, zLevel).tex(1, 1).endVertex();
        vb.pos(renderOffsetX + xAdd + BGSizeHandler.W_H_NODE, renderOffsetY + yAdd,                          zLevel).tex(1, 0).endVertex();
        vb.pos(renderOffsetX + xAdd,                          renderOffsetY + yAdd,                          zLevel).tex(0, 0).endVertex();
        t.draw();
    }

    private void renderConnectionLines(ResearchNode node, int[] positions, float zLevel) {
        int xAdd = (positions[0] - (this.leftOffset - sizeHandler.widthToBorder)) + BGSizeHandler.W_H_NODE / 2;
        int yAdd = (positions[1] - (this.topOffset - sizeHandler.heightToBorder)) + BGSizeHandler.W_H_NODE / 2;
        for (ResearchNode other : node.getConnectionsTo()) {
            renderConnection(other, xAdd, yAdd, zLevel);
        }
    }

    private void renderConnection(ResearchNode to, int fromX, int fromY, float zLevel) {
        int targetXOffset = (sizeHandler.evRelativePosX(to.renderPosX, false) - (this.leftOffset - sizeHandler.widthToBorder)) + (BGSizeHandler.W_H_NODE / 2);
        int targetYOffset = (sizeHandler.evRelativePosY(to.renderPosZ, false) - (this.topOffset - sizeHandler.heightToBorder)) + (BGSizeHandler.W_H_NODE / 2);
        drawConnection(fromX, fromY, targetXOffset, targetYOffset, zLevel);
    }

    private void drawConnection(int originX, int originY, int targetX, int targetY, float zLevel) {
        double nodeWidth = (BGSizeHandler.W_H_NODE / 2) - 1;

        GuiRenderBoundingBox originBox = new GuiRenderBoundingBox(originX - nodeWidth, originY - nodeWidth,
                originX + nodeWidth, originY + nodeWidth);

        GuiRenderBoundingBox targetBox = new GuiRenderBoundingBox(targetX - nodeWidth, targetY - nodeWidth,
                targetX + nodeWidth, targetY + nodeWidth);

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

            if(originBox.isInBox(lx, ly) || targetBox.isInBox(lx, ly)) continue;
            if(originBox.isInBox(origin.getX(), origin.getY()) || targetBox.isInBox(origin.getX(), origin.getY())) continue;

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
        GL11.glColor4f(brightness, brightness, brightness, 0.5F);
        Tessellator t = Tessellator.getInstance();
        VertexBuffer vb = t.getBuffer();
        vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        vb.pos(lx, ly, zLevel).endVertex();
        vb.pos(hx, hy, zLevel).endVertex();
        t.draw();
    }

    //TODO how about no?
    private float evaluateBrightness(int segment, int activeSegment) {
        if (segment == activeSegment) return 1.0F;
        switch (Math.abs(activeSegment - segment)) {
            case 0:
                return 1.0F; //Already done though
            case 1:
                return 0.9F;
            case 2:
                return 0.8F;
            case 3:
                return 0.7F;
            case 4:
                return 0.6F;
            case 5:
                return 0.5F;
            case 6:
                return 0.4F;
            case 7:
                return 0.3F;
            case 8:
                return 0.2F;
            case 9:
                return 0.1F;
        }
        return 0.0F;
    }

}
