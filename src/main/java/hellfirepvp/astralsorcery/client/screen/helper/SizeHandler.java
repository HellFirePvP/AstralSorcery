/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.helper;

import hellfirepvp.astralsorcery.client.screen.base.WidthHeightScreen;
import net.minecraft.util.math.MathHelper;

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

    private float widthHeightNodes = W_H_NODE;
    private float spaceBetweenNodes = W_H_NODE;

    private float shiftX;
    private float shiftY;
    private float leftOffset;
    private float topOffset;

    private float totalWidth;
    private float totalHeight;

    private float scalingFactor = 1F;
    private float maxScale = 10F;
    private float minScale = 1F;
    private float scaleSpeed = 0.2F;

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

        shiftX = (leftMost + rightMost) / 2F;
        shiftY = (lowerMost + upperMost) / 2F;

        float width = rightMost - leftMost;
        float height = lowerMost - upperMost;

        leftOffset = leftMost - shiftX;
        topOffset = upperMost - shiftY;

        totalWidth  = width  * widthHeightNodes + Math.max(width  - 1, 0) * spaceBetweenNodes;
        totalHeight = height * widthHeightNodes + Math.max(height - 1, 0) * spaceBetweenNodes;
    }

    @Nullable
    public abstract float[] buildRequiredRectangle();

    public float getTotalWidth() {
        return totalWidth * scalingFactor;
    }

    public float getTotalHeight() {
        return totalHeight * scalingFactor;
    }

    public Point.Float getRelativeCenter() {
        return new Point.Float(this.getTotalWidth() / 2F, this.getTotalHeight() / 2F);
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

    public float clampX(float centerX) {
        return MathHelper.clamp(centerX, 0, this.getTotalWidth());
    }

    public float clampY(float centerY) {
        return MathHelper.clamp(centerY, 0, this.getTotalHeight());
    }

    //Translates a renderPos into a gui-valid renderPosition (zoomed)
    public float evRelativePosX(float relativeX) {
        float shiftedX = relativeX - shiftX;
        float leftShift = shiftedX - leftOffset;

        float offsetX = leftShift * (getZoomedWHNode() + getZoomedSpaceBetweenNodes());
        offsetX += 0.5F * getZoomedWHNode();
        return offsetX;
    }

    public float evRelativePosY(float relativeY) {
        float shiftedY = relativeY - shiftY;
        float topShift = shiftedY - topOffset;

        float offsetY = topShift * (getZoomedWHNode() + getZoomedSpaceBetweenNodes());
        offsetY += 0.5F * getZoomedWHNode();
        return offsetY;
    }

    public Point.Float evRelativePos(Point.Float offset) {
        return new Point.Float(evRelativePosX(offset.x), evRelativePosY(offset.y));
    }

    public Point.Float evRelativePos(Point offset) {
        return new Point.Float(evRelativePosX(offset.x), evRelativePosY(offset.y));
    }

    public float scaledDistanceX(float fromX, float toX) {
        return this.evRelativePosX(toX) - this.evRelativePosX(fromX);
    }

    public float scaledDistanceY(float fromY, float toY) {
        return this.evRelativePosY(toY) - this.evRelativePosY(fromY);
    }

    public Point.Float scalePointToGui(WidthHeightScreen screen, ScalingPoint currentOffset, Point.Float point) {
        Point.Float shifted = this.evRelativePos(point);
        float fX = shifted.x - currentOffset.getScaledPosX() + screen.getGuiLeft() + screen.getGuiWidth() / 2F;
        float fY = shifted.y - currentOffset.getScaledPosY() + screen.getGuiTop()  + screen.getGuiHeight() / 2F;
        return new Point.Float(fX, fY);
    }
}