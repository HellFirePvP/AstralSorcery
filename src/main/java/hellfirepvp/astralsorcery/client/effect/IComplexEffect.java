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

    public RenderTarget getRenderTarget();

    public void render(float pTicks);

    public void tick();

    public static enum RenderTarget {

        OVERLAY_TEXT,
        RENDERLOOP

    }

}
