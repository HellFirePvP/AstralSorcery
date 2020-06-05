/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.source;

import hellfirepvp.astralsorcery.client.effect.EffectProperties;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXSource
 * Created by HellFirePvP
 * Date: 18.07.2019 / 19:48
 */
public abstract class FXSource<E extends EntityVisualFX, T extends BatchRenderContext<E>> extends EntityComplexFX {

    private final T ctx;

    public FXSource(Vector3 pos, T template) {
        super(pos);
        this.ctx = template;
    }

    public abstract void tickSpawnFX(Function<Vector3, E> effectRegistrar);

    public abstract void populateProperties(EffectProperties<E> properties);

    public final EffectHelper.Builder<E> generateFX() {
        EffectHelper.Builder<E> propBuilder = EffectHelper.of(ctx);
        this.populateProperties(propBuilder);
        return propBuilder;
    }

}
