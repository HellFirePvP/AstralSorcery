package hellfirepvp.astralsorcery.client.gui.journal;

import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import net.minecraft.client.Minecraft;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SideSizeHandler
 * Created by HellFirePvP
 * Date: 12.08.2016 / 17:41
 */
public final class BGSizeHandler {

    public static final int W_H_NODE = 18;
    public static final int SPACE_BETWEEN_NODES = W_H_NODE;

    //Space between outermost nodes and border.
    public final int heightToBorder;
    public final int widthToBorder;

    private int midX;
    private int midY;

    private int totalWidth;
    private int totalHeight;

    private int leftMost, rightMost, upperMost, lowerMost;

    private int totalAddedWidth, totalAddedHeight;

    public BGSizeHandler(int height, int width) {
        this.heightToBorder = height / 2;
        this.widthToBorder = width / 2;
    }

    public void updateSize() {
        leftMost = 0;
        rightMost = 0;
        upperMost = 0;
        lowerMost = 0;

        for(ResearchProgression progress : ResearchManager.clientProgress.getResearchProgression()) {
            for (ResearchNode node : progress.getResearchNodes()) {
                int x = node.renderPosX;
                int y = node.renderPosZ;

                if(x < leftMost) leftMost = x;
                if(x > rightMost) rightMost = x;
                if(y > lowerMost) lowerMost = y;
                if(y < upperMost) upperMost = y;
            }
        }
        int x = 1;
        int y = 1;

        if(x < leftMost) leftMost = x;
        if(x > rightMost) rightMost = x;
        if(y > lowerMost) lowerMost = y;
        if(y < upperMost) upperMost = y;

        leftMost = Math.abs(leftMost);
        rightMost = Math.abs(rightMost);
        upperMost = Math.abs(upperMost);
        lowerMost = Math.abs(lowerMost);

        int leftAdded = (leftMost * W_H_NODE + leftMost * SPACE_BETWEEN_NODES);
        int rightAdded = (rightMost * W_H_NODE + rightMost * SPACE_BETWEEN_NODES);
        int upperAdded = (upperMost * W_H_NODE + upperMost * SPACE_BETWEEN_NODES);
        int lowerAdded = (lowerMost * W_H_NODE + lowerMost * SPACE_BETWEEN_NODES);

        totalAddedWidth = leftAdded + rightAdded;
        totalAddedHeight = upperAdded + lowerAdded;

        midX = widthToBorder + leftAdded;
        totalWidth = widthToBorder + rightAdded + midX;
        midY = heightToBorder + upperAdded;
        totalHeight = heightToBorder + lowerAdded + midY;
    }

    public int getMidX() {
        return midX;
    }

    public int getMidY() {
        return midY;
    }

    public int getTotalWidth() {
        return totalWidth;
    }

    public int getTotalHeight() {
        return totalHeight;
    }

    public int getTotalAddedWidth() {
        return totalAddedWidth;
    }

    public int getTotalAddedHeight() {
        return totalAddedHeight;
    }

    public int adjustX(int centerX) {
        if((centerX + widthToBorder) > totalWidth) {
            centerX = getTotalWidth() - widthToBorder;
        }
        if((centerX - widthToBorder) < 0) {
            centerX = widthToBorder;
        }
        return centerX;
    }

    public int adjustY(int centerY) {
        if((centerY + heightToBorder) > totalHeight) {
            centerY = getTotalHeight() - heightToBorder;
        }
        if((centerY - heightToBorder) < 0) {
            centerY = heightToBorder;
        }
        return centerY;
    }


    public int evRelativePosX(int relativeX, boolean wideNodeWidth) {
        int ret = sizeXLocationForRelative(relativeX);
        return wideNodeWidth ? ret + W_H_NODE : ret;
    }

    public int evRelativePosY(int relativeY, boolean wideNodeHeight) {
        int ret = sizeYLocationForRelative(relativeY);
        return wideNodeHeight ? ret + W_H_NODE : ret;
    }

    private int sizeXLocationForRelative(int relativeX) {
        if(relativeX < 0) { //Moving left
            return getMidX() - (Math.abs(relativeX) * (W_H_NODE + SPACE_BETWEEN_NODES));
        } else { //Moving right
            return getMidX() + (relativeX * (W_H_NODE + SPACE_BETWEEN_NODES));
        }
    }

    private int sizeYLocationForRelative(int relativeY) {
        if(relativeY < 0) { //Moving up
            return getMidY() - (Math.abs(relativeY) * (W_H_NODE + SPACE_BETWEEN_NODES));
        } else { //Moving down
            return getMidY() + (relativeY * (W_H_NODE + SPACE_BETWEEN_NODES));
        }
    }

}
