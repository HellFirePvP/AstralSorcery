package hellfirepvp.astralsorcery.client.gui.journal;

import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SizeHandler
 * Created by HellFirePvP
 * Date: 12.08.2016 / 17:41
 */
public abstract class SizeHandler {

    private static final int W_H_NODE = 18;
    private static final int SPACE_BETWEEN_NODES = W_H_NODE;

    //Space between outermost nodes and border.
    public final double heightToBorder;
    public final double widthToBorder;

    private double midX;
    private double midY;

    private double totalWidth;
    private double totalHeight;

    private double scalingFactor = 1.0D;

    public SizeHandler(int height, int width) {
        this.heightToBorder = height / 2;
        this.widthToBorder = width / 2;
    }

    public void updateSize() {
        resetZoom();

        int leftMost = 0;
        int rightMost = 0;
        int upperMost = 0;
        int lowerMost = 0;

        int[] requiredRect = buildRequiredRectangle();
        if(requiredRect != null) {
            leftMost =  requiredRect[0];
            rightMost = requiredRect[1];
            upperMost = requiredRect[2];
            lowerMost = requiredRect[3];
        }

        leftMost = Math.abs(leftMost);
        rightMost = Math.abs(rightMost);

        upperMost = Math.abs(upperMost);
        lowerMost = Math.abs(lowerMost);

        int leftAdded  = (leftMost  * W_H_NODE + leftMost  * SPACE_BETWEEN_NODES);
        int rightAdded = (rightMost * W_H_NODE + rightMost * SPACE_BETWEEN_NODES);

        int upperAdded = (upperMost * W_H_NODE + upperMost * SPACE_BETWEEN_NODES);
        int lowerAdded = (lowerMost * W_H_NODE + lowerMost * SPACE_BETWEEN_NODES);

        midX = widthToBorder + leftAdded;
        totalWidth = widthToBorder + rightAdded + midX;
        midY = heightToBorder + upperAdded;
        totalHeight = heightToBorder + lowerAdded + midY;
    }

    @Nullable
    public abstract int[] buildRequiredRectangle();

    public double getMidX() {
        return midX * scalingFactor;
    }

    public double getMidY() {
        return midY * scalingFactor;
    }

    public double getTotalWidth() {
        return totalWidth * scalingFactor;
    }

    public double getTotalHeight() {
        return totalHeight * scalingFactor;
    }

    public double getScalingFactor() {
        return scalingFactor;
    }

    public double getZoomedWHNode() {
        return W_H_NODE * scalingFactor;
    }

    public double getZoomedSpaceBetweenNodes() {
        return SPACE_BETWEEN_NODES * scalingFactor;
    }

    public double scaleAccordingly(double toScale) {
        return toScale * scalingFactor;
    }

    public void handleZoomIn() {
        if(scalingFactor >= 5.0) return;
        scalingFactor = Math.min(5.0, scalingFactor + 0.2);
    }

    public void handleZoomOut() {
        if(scalingFactor <= 1.0) return;
        scalingFactor = Math.max(1.0, scalingFactor - 0.2);
    }

    public void resetZoom() {
        this.scalingFactor = 1.0D;
    }

    //ensures that the cursor pos never gets too close to a border. (X)
    //scaled or not, widthToBorder and heightToBorder are defined by the real GUI size!
    public double adjustX(double centerX) {
        if((centerX + widthToBorder) > getTotalWidth()) {
            centerX = getTotalWidth() - widthToBorder;
        }
        if((centerX - widthToBorder) < 0) {
            centerX = widthToBorder;
        }
        return centerX;
    }

    //ensures that the cursor pos never gets too close to a border. (Y)
    public double adjustY(double centerY) {
        if((centerY + heightToBorder) > getTotalHeight()) {
            centerY = getTotalHeight() - heightToBorder;
        }
        if((centerY - heightToBorder) < 0) {
            centerY = heightToBorder;
        }
        return centerY;
    }


    //Translates a renderPos into a gui-valid renderPosition (zoomed)
    public double evRelativePosX(int relativeX) {
        return getMidX() + (relativeX * (getZoomedWHNode() + getZoomedSpaceBetweenNodes()));
    }

    public double evRelativePosY(int relativeY) {
        return getMidY() + (relativeY * (getZoomedWHNode() + getZoomedSpaceBetweenNodes()));
    }

    private double sizeXLocationForRelative(int relativeX) {
        if(relativeX < 0) { //Moving left
            return getMidX() - (Math.abs(relativeX) * (getZoomedWHNode() + getZoomedSpaceBetweenNodes()));
        } else { //Moving right
            return getMidX() + (relativeX * (getZoomedWHNode() + getZoomedSpaceBetweenNodes()));
        }
    }

    private double sizeYLocationForRelative(int relativeY) {
        if(relativeY < 0) { //Moving up
            return getMidY() - (Math.abs(relativeY) * (getZoomedWHNode() + getZoomedSpaceBetweenNodes()));
        } else { //Moving down
            return getMidY() + (relativeY * (getZoomedWHNode() + getZoomedSpaceBetweenNodes()));
        }
    }

}
