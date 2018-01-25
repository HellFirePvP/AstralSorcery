/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal;

import javax.annotation.Nullable;

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
    private double maxScale = 10.0D;
    private double minScale = 1.0D;
    private double scaleSpeed = 0.2D;

    public SizeHandler(int height, int width) {
        this.heightToBorder = height / 2;
        this.widthToBorder = width / 2;
    }

    public void setMaxScale(double maxScale) {
        this.maxScale = maxScale;
    }

    public void setScaleSpeed(double scaleSpeed) {
        this.scaleSpeed = scaleSpeed;
    }

    public void setMinScale(double minScale) {
        this.minScale = minScale;
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
        if(scalingFactor >= maxScale) return;
        scalingFactor = Math.min(maxScale, scalingFactor + scaleSpeed);
    }

    public void handleZoomOut() {
        if(scalingFactor <= minScale) return;
        scalingFactor = Math.max(minScale, scalingFactor - scaleSpeed);
    }

    public void forceScaleTo(double scale) {
        this.scalingFactor = scale;
    }

    public void resetZoom() {
        this.scalingFactor = 1.0D;
    }

    //ensures that the cursor pos never gets too close to a border. (X)
    //scaled or not, widthToBorder and heightToBorder are defined by the real GUI size!
    public double clampX(double centerX) {
        if((centerX + widthToBorder) > getTotalWidth()) {
            centerX = getTotalWidth() - widthToBorder;
        }
        if((centerX - widthToBorder) < 0) {
            centerX = widthToBorder;
        }
        return centerX;
    }

    //ensures that the cursor pos never gets too close to a border. (Y)
    public double clampY(double centerY) {
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

}
