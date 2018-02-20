/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IComplexEffect
 * Created by HellFirePvP
 * Date: 02.08.2016 / 12:31
 */
public interface IComplexEffect {

    public boolean canRemove();

    public boolean isRemoved();

    public void flagAsRemoved();

    public void clearRemoveFlag();

    public RenderTarget getRenderTarget();

    public void render(float pTicks);

    public void tick();

    //Valid layers: 0, 1, 2
    //Lower layers are rendered first.
    default public int getLayer() {
        return 0;
    }

    public static enum RenderTarget {

        OVERLAY_TEXT,
        RENDERLOOP

    }

    public static interface PreventRemoval {}

}
