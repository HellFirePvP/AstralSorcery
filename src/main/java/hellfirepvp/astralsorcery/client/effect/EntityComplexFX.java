/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityComplexFX
 * Created by HellFirePvP
 * Date: 17.09.2016 / 22:55
 */
public abstract class EntityComplexFX implements IComplexEffect {

    protected int age = 0;
    protected int maxAge = 40;
    protected boolean removeRequested = false;

    private boolean flagRemoved = true;

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public boolean canRemove() {
        return age >= maxAge || removeRequested;
    }

    @Override
    public RenderTarget getRenderTarget() {
        return RenderTarget.RENDERLOOP;
    }

    @Override
    public void tick() {
        age++;
    }

    public void requestRemoval() {
        this.removeRequested = true;
    }

    public boolean isRemoved() {
        return flagRemoved;
    }

    public void flagAsRemoved() {
        flagRemoved = true;
        removeRequested = false;
    }

    public void clearRemoveFlag() {
        flagRemoved = false;
    }

    public static enum AlphaFunction {

        CONSTANT,
        FADE_OUT,
        PYRAMID;

        AlphaFunction() {}

        public float getAlpha(int age, int maxAge) {
            switch (this) {
                case CONSTANT:
                    return 1F;
                case FADE_OUT:
                    return 1F - (((float) age) / ((float) maxAge));
                case PYRAMID:
                    float halfAge = maxAge / 2F;
                    return 1F - (Math.abs(halfAge - age) / halfAge);
            }
            return 1F;
        }

    }

}
