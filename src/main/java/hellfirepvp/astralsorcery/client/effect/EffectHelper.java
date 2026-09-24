/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EffectHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EffectHelper {

    public static <T extends EffectTemplate<E>, E extends EntityVisualFX> void refresh(T ctx, E vfx) {
        if (vfx.canRemove()) {
            EffectHandler.getInstance().queueParticle(ctx, vfx);
        }
    }

    public static <T extends EffectTemplate<E>, E extends EntityVisualFX> Builder<T, E> of(T ctx) {
        return new Builder<>(ctx);
    }

    public static <S extends EntitySourceFX> S source(S source) {
        EffectHandler.getInstance().queueSource(source);
        return source;
    }

    public static class Builder<T extends EffectTemplate<E>, E extends EntityVisualFX> {

        private final T ctx;

        public Builder(T ctx) {
            this.ctx = ctx;
        }

        public E spawn(Vector3 pos) {
            E vfx = this.ctx.createParticle(pos);
            EffectHandler.getInstance().queueParticle(this.ctx, vfx);
            return vfx;
        }
    }
}
