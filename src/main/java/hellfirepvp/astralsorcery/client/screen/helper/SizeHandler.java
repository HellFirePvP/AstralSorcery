/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.helper;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SizeHandler
 * Created by HellFirePvP
 * Date: 03.08.2019 / 17:03
 */
public abstract class SizeHandler {

    private static final int W_H_NODE = 18;

    //Space between outermost nodes and border.
    public final float heightToBorder;
    public final float widthToBorder;

    private float widthHeightNodes = W_H_NODE;
    private float spaceBetweenNodes = W_H_NODE;

    private float midX;
    private float midY;

    private float totalWidth;
    private float totalHeight;

    private float scalingFactor = 1F;
    private float maxScale = 10F;
    private float minScale = 1F;
    private float scaleSpeed = 0.2F;

    public SizeHandler(int height, int width) {
        this.heightToBorder = height / 2;
        this.widthToBorder = width / 2;
    }

    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }

    public void setScaleSpeed(float scaleSpeed) {
        this.scaleSpeed = scaleSpeed;
    }

    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    public void setWidthHeightNodes(float widthHeightNodes) {
        this.widthHeightNodes = widthHeightNodes;
    }

    public void setSpaceBetweenNodes(float spaceBetweenNodes) {
        this.spaceBetweenNodes = spaceBetweenNodes;
    }

    public void updateSize() {
        resetZoom();

        float leftMost = 0;
        float rightMost = 0;
        float upperMost = 0;
        float lowerMost = 0;

        float[] requiredRect = buildRequiredRectangle();
        if (requiredRect != null) {
            leftMost =  requiredRect[0];
            rightMost = requiredRect[1];
            upperMost = requiredRect[2];
            lowerMost = requiredRect[3];
        }

        leftMost = Math.abs(leftMost);
        rightMost = Math.abs(rightMost);

        upperMost = Math.abs(upperMost);
        lowerMost = Math.abs(lowerMost);

        float leftAdded  = (leftMost  * this.widthHeightNodes + leftMost  * this.spaceBetweenNodes);
        float rightAdded = (rightMost * this.widthHeightNodes + rightMost * this.spaceBetweenNodes);

        float upperAdded = (upperMost * this.widthHeightNodes + upperMost * this.spaceBetweenNodes);
        float lowerAdded = (lowerMost * this.widthHeightNodes + lowerMost * this.spaceBetweenNodes);

        midX = widthToBorder + leftAdded;
        totalWidth = widthToBorder + rightAdded + midX;
        midY = heightToBorder + upperAdded;
        totalHeight = heightToBorder + lowerAdded + midY;
    }

    @Nullable
    public abstract float[] buildRequiredRectangle();

    public float getMidX() {
        return midX * scalingFactor;
    }

    public float getMidY() {
        return midY * scalingFactor;
    }

    public float getTotalWidth() {
        return totalWidth * scalingFactor;
    }

    public float getTotalHeight() {
        return totalHeight * scalingFactor;
    }

    public float getScalingFactor() {
        return scalingFactor;
    }

    public float getZoomedWHNode() {
        return this.widthHeightNodes * scalingFactor;
    }

    public float getZoomedSpaceBetweenNodes() {
        return this.spaceBetweenNodes * scalingFactor;
    }

    public float scaleAccordingly(float toScale) {
        return toScale * scalingFactor;
    }

    public void handleZoomIn() {
        if (scalingFactor >= maxScale) return;
        scalingFactor = Math.min(maxScale, scalingFactor + scaleSpeed);
    }

    public void handleZoomOut() {
        if (scalingFactor <= minScale) return;
        scalingFactor = Math.max(minScale, scalingFactor - scaleSpeed);
    }

    public void forceScaleTo(float scale) {
        this.scalingFactor = scale;
    }

    public void resetZoom() {
        this.scalingFactor = 1F;
    }

    //ensures that the cursor pos never gets too close to a border. (X)
    //scaled or not, widthToBorder and heightToBorder are defined by the real GUI size!
    public float clampX(float centerX) {
        if ((centerX + widthToBorder) > getTotalWidth()) {
            centerX = getTotalWidth() - widthToBorder;
        }
        if ((centerX - widthToBorder) < 0) {
            centerX = widthToBorder;
        }
        return centerX;
    }

    //ensures that the cursor pos never gets too close to a border. (Y)
    public float clampY(float centerY) {
        if ((centerY + heightToBorder) > getTotalHeight()) {
            centerY = getTotalHeight() - heightToBorder;
        }
        if ((centerY - heightToBorder) < 0) {
            centerY = heightToBorder;
        }
        return centerY;
    }

    //Translates a renderPos into a gui-valid renderPosition (zoomed)
    public float evRelativePosX(float relativeX) {
        return getMidX() + (relativeX * (getZoomedWHNode() + getZoomedSpaceBetweenNodes()));
    }

    public float evRelativePosY(float relativeY) {
        return getMidY() + (relativeY * (getZoomedWHNode() + getZoomedSpaceBetweenNodes()));
    }

    public Point.Float evRelativePos(Point.Float offset) {
        return new Point.Float(evRelativePosX(offset.x), evRelativePosY(offset.y));
    }

    public Point.Float evRelativePos(Point offset) {
        return new Point.Float(evRelativePosX(offset.x), evRelativePosY(offset.y));
    }
}