/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.helper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScalingPoint
 * Created by HellFirePvP
 * Date: 03.08.2019 / 17:05
 */
public class ScalingPoint {

    private float posX, posY;
    private float scaledX, scaledY;

    private ScalingPoint() {}

    public static ScalingPoint createPoint(float posX, float posY, float scale, boolean arePositionsScaled) {
        ScalingPoint sp = new ScalingPoint();
        if (arePositionsScaled) {
            sp.updateScaledPos(posX, posY, scale);
        } else {
            sp.updatePos(posX, posY, scale);
        }
        return sp;
    }

    public void updatePos(float posX, float posY, float scale) {
        this.posX = posX;
        this.posY = posY;
        this.scaledX = scale * this.getPosX();
        this.scaledY = scale * this.getPosY();
    }

    public void updateScaledPos(float scaledX, float scaledY, float scale) {
        this.scaledX = scaledX;
        this.scaledY = scaledY;
        this.posX = this.scaledX / scale;
        this.posY = this.scaledY / scale;
    }

    public float getPosY() {
        return posY;
    }

    public float getPosX() {
        return posX;
    }

    public float getScaledPosX() {
        return scaledX;
    }

    public float getScaledPosY() {
        return scaledY;
    }

    public void rescale(float newScale) {
        this.scaledX = this.getPosX() * newScale;
        this.scaledY = this.getPosY() * newScale;
    }
}
