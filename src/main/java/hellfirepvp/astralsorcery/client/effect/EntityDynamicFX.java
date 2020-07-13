package hellfirepvp.astralsorcery.client.effect;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityDynamicFX
 * Created by HellFirePvP
 * Date: 05.06.2020 / 20:59
 */
public interface EntityDynamicFX {

    public abstract <T extends EntityVisualFX & EntityDynamicFX> void renderNow(BatchRenderContext<T> ctx, MatrixStack renderStack, IDrawRenderTypeBuffer drawBuffer, float pTicks);

}
