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
 * Class: ScreenRenderBoundingBox
 * Created by HellFirePvP
 * Date: 03.08.2019 / 17:05
 */
public final class ScreenRenderBoundingBox {

    private double lx, ly, hx, hy;

    public ScreenRenderBoundingBox(double lx, double ly, double hx, double hy) {
        this.lx = lx;
        this.ly = ly;
        this.hx = hx;
        this.hy = hy;
    }

    public boolean isInBox(double currX, double currY) {
        return !(currX < lx || currX > hx) && !(currY < ly || currY > hy);
    }

    public double getMidX() {
        return getWidth() / 2;
    }

    public double getMidY() {
        return getHeight() / 2;
    }

    public double getLowerX() {
        return lx;
    }

    public double getLowerY() {
        return ly;
    }

    public double getWidth() {
        return hx - lx;
    }

    public double getHeight() {
        return hy - ly;
    }

    @Override
    public String toString() {
        return "ScreenRenderBoundingBox{" + lx + "," + ly + " to " + hx + "," + hy + "}";
    }

}
