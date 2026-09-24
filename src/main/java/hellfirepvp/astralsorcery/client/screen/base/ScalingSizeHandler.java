/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import hellfirepvp.astralsorcery.common.util.data.FloatPoint;
import hellfirepvp.astralsorcery.common.util.data.FloatRectangle;
import hellfirepvp.astralsorcery.common.util.data.IntPoint;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ScalingSizeHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ScalingSizeHandler {

    private static final int W_H_NODE = 22;

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
        this.resetScale();

        FloatRectangle rect = this.buildRequiredRectangle();
        if (rect == null) rect = FloatRectangle.zero();

        shiftX = rect.x() + (rect.width() / 2F);
        shiftY = rect.y() + (rect.height() / 2F);

        leftOffset = rect.x() - shiftX;
        topOffset = rect.y() - shiftY;

        float width = rect.width();
        float height = rect.height();
        totalWidth  = width  * widthHeightNodes + Math.max(width  - 1, 0) * spaceBetweenNodes;
        totalHeight = height * widthHeightNodes + Math.max(height - 1, 0) * spaceBetweenNodes;
    }

    @Nullable
    public abstract FloatRectangle buildRequiredRectangle();

    public float getScalingFactor() {
        return this.scalingFactor;
    }

    public float getScaledWidth() {
        return this.totalWidth * this.getScalingFactor();
    }

    public float getScaledHeight() {
        return this.totalHeight * this.getScalingFactor();
    }

    public FloatPoint getRelativeCenter() {
        return new FloatPoint(this.getScaledWidth() / 2F, this.getScaledHeight() / 2F);
    }

    public float getScaledNodeSize() {
        return this.widthHeightNodes * this.getScalingFactor();
    }

    public float getScaledSpaceBetweenNodes() {
        return this.spaceBetweenNodes * this.getScalingFactor();
    }

    public float scale(float toScale) {
        return toScale * this.getScalingFactor();
    }

    public void handleZoomIn() {
        if (scalingFactor >= maxScale) return;
        float zoomSpeed = this.scaleSpeed * Minecraft.getInstance().options.mouseWheelSensitivity().get().floatValue();
        scalingFactor = Math.min(maxScale, scalingFactor + zoomSpeed);
    }

    public void handleZoomOut() {
        if (scalingFactor <= minScale) return;
        float zoomSpeed = this.scaleSpeed * Minecraft.getInstance().options.mouseWheelSensitivity().get().floatValue();
        scalingFactor = Math.max(minScale, scalingFactor - zoomSpeed);
    }

    public void setScale(float scale) {
        this.scalingFactor = scale;
    }

    public void resetScale() {
        this.scalingFactor = 1F;
    }

    public float clampX(float centerX) {
        return Mth.clamp(centerX, 0, this.getScaledWidth());
    }

    public float clampY(float centerY) {
        return Mth.clamp(centerY, 0, this.getScaledHeight());
    }

    //Translates a renderPos into a gui-valid renderPosition (zoomed)
    public float evRelativePosX(float relativeX) {
        float shiftedX = relativeX - shiftX;
        float leftShift = shiftedX - leftOffset;

        float offsetX = leftShift * (getScaledNodeSize() + getScaledSpaceBetweenNodes());
        offsetX += 0.5F * getScaledNodeSize();
        return offsetX;
    }

    public float evRelativePosY(float relativeY) {
        float shiftedY = relativeY - shiftY;
        float topShift = shiftedY - topOffset;

        float offsetY = topShift * (getScaledNodeSize() + getScaledSpaceBetweenNodes());
        offsetY += 0.5F * getScaledNodeSize();
        return offsetY;
    }

    public FloatPoint evRelativePos(IntPoint offset) {
        return this.evRelativePos(offset.toFloat());
    }

    public FloatPoint evRelativePos(FloatPoint offset) {
        return new FloatPoint(evRelativePosX(offset.x()), evRelativePosY(offset.y()));
    }

    public float scaledDistanceX(float fromX, float toX) {
        return this.evRelativePosX(toX) - this.evRelativePosX(fromX);
    }

    public float scaledDistanceY(float fromY, float toY) {
        return this.evRelativePosY(toY) - this.evRelativePosY(fromY);
    }

    public FloatPoint scalePointToGui(FixedSizeScreen screen, ScalingPoint currentOffset, IntPoint point) {
        return scalePointToGui(screen, currentOffset, point.toFloat());
    }

    public FloatPoint scalePointToGui(FixedSizeScreen screen, ScalingPoint currentOffset, FloatPoint point) {
        FloatPoint shifted = this.evRelativePos(point);
        float fX = shifted.x() - currentOffset.getScaledPosX() + screen.getScreenLeft() + screen.getScreenWidth() / 2F;
        float fY = shifted.y() - currentOffset.getScaledPosY() + screen.getScreenTop()  + screen.getScreenHeight() / 2F;
        return new FloatPoint(fX, fY);
    }
}
