package hellfirepvp.astralsorcery.client.gui.journal;

import hellfirepvp.astralsorcery.client.util.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.AssetLoader;
import hellfirepvp.astralsorcery.client.util.BindableResource;
import hellfirepvp.astralsorcery.client.util.ClientJournalMapping;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

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
    private static final BindableResource black = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "black");

    private GalaxySizeHandler sizeHandler;

    //Rendering relevant properties. Those coords represent _unscaled_ exact positions.
    public GuiRenderBoundingBox realRenderBox;
    private int realCoordLowerX, realCoordLowerY;
    private int realRenderWidth, realRenderHeight;

    private ScalingPoint mousePointScaled;
    private ScalingPoint previousMousePointScaled;

    private boolean hasPrevOffset = false;

    //private Map<Vector3, ResearchNode> rectMapNodes = new HashMap<>();

    public GuiProgressionRenderer(int guiHeight, int guiWidth) {
        this.sizeHandler = new GalaxySizeHandler(guiHeight, guiWidth);
        this.mousePointScaled = ScalingPoint.createPoint(
                this.sizeHandler.adjustX(this.sizeHandler.getMidX()),
                this.sizeHandler.adjustY(this.sizeHandler.getMidY()),
                this.sizeHandler.getScalingFactor(),
                false);
    }

    public void refreshSize() {
        this.sizeHandler.updateSize();
    }

    public void setBox(int left, int top, int right, int bottom) {
        this.realRenderBox = new GuiRenderBoundingBox(left, top, right, bottom);
        this.realRenderWidth = (int) this.realRenderBox.getWidth();
        this.realRenderHeight = (int) this.realRenderBox.getHeight();
    }

    public void moveMouse(double changedX, double changedY) {
        if (hasPrevOffset) {
            mousePointScaled.updateScaledPos(
                    sizeHandler.adjustX(previousMousePointScaled.getScaledPosX() + changedX),
                    sizeHandler.adjustY(previousMousePointScaled.getScaledPosY() + changedY),
                    sizeHandler.getScalingFactor());
        } else {
            mousePointScaled.updateScaledPos(
                    sizeHandler.adjustX(changedX),
                    sizeHandler.adjustY(changedY),
                    sizeHandler.getScalingFactor());
        }
    }

    public void applyMovedMouseOffset() {
        this.previousMousePointScaled = ScalingPoint.createPoint(
                mousePointScaled.getScaledPosX(),
                mousePointScaled.getScaledPosY(),
                sizeHandler.getScalingFactor(),
                true);
        this.hasPrevOffset = true;
    }

    public void updateOffset(int guiLeft, int guiTop) {
        this.realCoordLowerX = guiLeft;
        this.realCoordLowerY = guiTop;
    }

    public void centerMouse() {
        moveMouse(sizeHandler.getMidX(), sizeHandler.getMidY());
    }

    public void updateMouseState() {
        moveMouse(0, 0);
    }

    public void unfocus() {

    }

    public void resetZoom() {
        sizeHandler.resetZoom();
        rescale(sizeHandler.getScalingFactor());
    }

    public void handleZoomOut() {
        this.sizeHandler.handleZoomOut();
        rescale(sizeHandler.getScalingFactor());
    }

    public void handleZoomIn() {
        this.sizeHandler.handleZoomIn();
        rescale(sizeHandler.getScalingFactor());
    }

    private void rescale(double newScale) {
        this.mousePointScaled.rescale(newScale);
        if(this.previousMousePointScaled != null) {
            this.previousMousePointScaled.rescale(newScale);
        }
        updateMouseState();
    }

    public void drawProgressionPart(float zLevel) {
        drawBaseBackground(zLevel);

        drawClusters(zLevel);

        int mouseX = Mouse.getX();
        int mouseY = Mouse.getY();
        //System.out.println("mouse: " + mouseX + ", " + mouseY);

        drawBlendedStarfieldLayers(zLevel);
    }

    private void drawClusters(float zLevel) {
        PlayerProgress thisProgress = ResearchManager.clientProgress;
        for (ResearchProgression progress : thisProgress.getResearchProgression()) {
            ClientJournalMapping.JournalCluster cluster = ClientJournalMapping.getClusterMapping(progress);
            double lX = sizeHandler.evRelativePosX(cluster.leftMost );
            double rX = sizeHandler.evRelativePosX(cluster.rightMost);
            double lY = sizeHandler.evRelativePosY(cluster.upperMost );
            double rY = sizeHandler.evRelativePosY(cluster.lowerMost);
            //System.out.println("render at " + lX + ", " + lY + " to " + rX + ", " + rY);
            renderCluster(cluster, lX, lY, rX, rY, zLevel);
        }
    }

    private void renderCluster(ClientJournalMapping.JournalCluster cluster, double lowerPosX, double lowerPosY, double higherPosX, double higherPosY, float zLevel) {
        double scaledLeft = this.mousePointScaled.getScaledPosX() - sizeHandler.widthToBorder;
        double scaledTop =  this.mousePointScaled.getScaledPosY() - sizeHandler.heightToBorder;
        double xAdd = lowerPosX - scaledLeft;
        double yAdd = lowerPosY - scaledTop;

        GL11.glPushMatrix();
        GL11.glTranslated(realCoordLowerX + xAdd, realCoordLowerY + yAdd, 0);

        double width =  higherPosX - lowerPosX;
        double height = higherPosY - lowerPosY;

        Tessellator t = Tessellator.getInstance();
        VertexBuffer vb = t.getBuffer();
        cluster.cloudTexture.bind();
        GL11.glColor4f(1F, 1F, 1F, 1F);

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(0,     height, zLevel).tex(0, 1).endVertex();
        vb.pos(width, height, zLevel).tex(1, 1).endVertex();
        vb.pos(width, 0,      zLevel).tex(1, 0).endVertex();
        vb.pos(0,     0,      zLevel).tex(0, 0).endVertex();
        t.draw();
        GL11.glPopMatrix();
    }

    private void drawBlendedStarfieldLayers(float zLevel) {
        drawBlendedStarfieldOverlay(zLevel, 0.5);
        drawBlendedStarfieldOverlay(zLevel, 1.5);
        drawBlendedStarfieldOverlay(zLevel, 2.5);
        drawBlendedStarfieldOverlay(zLevel, 3.5);
    }

    private void drawBaseBackground(float zLevel) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        black.bind();

        /*float lowU = (((float) this.leftOffset) - sizeHandler.widthToBorder) / ((float) sizeHandler.getTotalWidth());
        float highU = lowU + (((float) renderWidth) / ((float) sizeHandler.getTotalWidth()));
        float lowV = (((float) this.topOffset) - sizeHandler.heightToBorder) / ((float) sizeHandler.getTotalHeight());
        float highV = lowV + (((float) renderHeight) / ((float) sizeHandler.getTotalHeight()));*/

        VertexBuffer vb = Tessellator.getInstance().getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(realCoordLowerX,                   realCoordLowerY + realRenderHeight, zLevel).tex(0, 1).endVertex();
        vb.pos(realCoordLowerX + realRenderWidth, realCoordLowerY + realRenderHeight, zLevel).tex(1, 1).endVertex();
        vb.pos(realCoordLowerX + realRenderWidth, realCoordLowerY,                    zLevel).tex(1, 0).endVertex();
        vb.pos(realCoordLowerX,                   realCoordLowerY,                    zLevel).tex(0, 0).endVertex();
        Tessellator.getInstance().draw();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    private void drawBlendedStarfieldOverlay(float zLevel, double scaleFactor) {
        GL11.glColor4f(0.6F, 0.6F, 0.6F, 0.5F);
        GL11.glPushMatrix();
        GL11.glScaled(scaleFactor, scaleFactor, scaleFactor);
        textureResOVL.bind();

        double th = sizeHandler.getTotalHeight() / sizeHandler.getScalingFactor();
        double tw = sizeHandler.getTotalWidth() / sizeHandler.getScalingFactor();

        double lowU = (this.mousePointScaled.posX - sizeHandler.widthToBorder) / tw;
        double highU = lowU + (((float) realRenderWidth) / tw);
        double lowV = (this.mousePointScaled.posY - sizeHandler.heightToBorder) / th;
        double highV = lowV + (((float) realRenderHeight) / th);

        VertexBuffer vb = Tessellator.getInstance().getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(0,               realRenderHeight, zLevel).tex(lowU,  highV).endVertex();
        vb.pos(realRenderWidth, realRenderHeight, zLevel).tex(highU, highV).endVertex();
        vb.pos(realRenderWidth, 0,                zLevel).tex(highU, lowV) .endVertex();
        vb.pos(0,               0,                zLevel).tex(lowU,  lowV) .endVertex();
        Tessellator.getInstance().draw();
        GL11.glPopMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    public static class ScalingPoint {

        private double posX, posY;
        private double scaledX, scaledY;

        private ScalingPoint() {}

        public static ScalingPoint createPoint(double posX, double posY, double scale, boolean arePositionsScaled) {
            ScalingPoint sp = new ScalingPoint();
            if(arePositionsScaled) {
                sp.updateScaledPos(posX, posY, scale);
            } else {
                sp.updatePos(posX, posY, scale);
            }
            return sp;
        }

        public void updatePos(double posX, double posY, double scale) {
            this.posX = posX;
            this.posY = posY;
            this.scaledX = scale * this.posX;
            this.scaledY = scale * this.posY;
        }

        public void updateScaledPos(double scaledX, double scaledY, double scale) {
            this.scaledX = scaledX;
            this.scaledY = scaledY;
            this.posX = this.scaledX / scale;
            this.posY = this.scaledY / scale;
        }

        public double getScaledPosX() {
            return scaledX;
        }

        public double getScaledPosY() {
            return scaledY;
        }

        public void rescale(double newScale) {
            this.scaledX = this.posX * newScale;
            this.scaledY = this.posY * newScale;
        }
    }

}
