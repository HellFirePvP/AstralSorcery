/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal;

/**
* This class is part of the Astral Sorcery Mod
* The complete source code for this mod can be found on github.
* Class: ScalingPoint
* Created by HellFirePvP
* Date: 14.08.2016 / 16:39
*/
public class ScalingPoint {

    private double posX, posY;
    private double scaledX, scaledY;

    ScalingPoint() {}

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

    public double getPosY() {
        return posY;
    }

    public double getPosX() {
        return posX;
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
