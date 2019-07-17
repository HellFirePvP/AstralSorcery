/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.handler;

import hellfirepvp.astralsorcery.client.effect.EffectProperties;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.context.BatchRenderContext;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.math.Vec3i;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectHelper
 * Created by HellFirePvP
 * Date: 08.07.2019 / 20:54
 */
public class EffectHelper {

    public static <T extends EntityComplexFX, C extends BatchRenderContext<T>> Builder<T> of(C ctx) {
        return new Builder<>(ctx);
    }

    public static class Builder<T extends EntityComplexFX> {

        private BatchRenderContext<T> context;
        private EffectProperties prop;

        public Builder(BatchRenderContext<T> ctx) {
            this.context = ctx;
            this.prop = this.context.makeProperties();
        }

        public Builder<T> pos(Vec3i position) {
            this.prop.setPosition(position);
            return this;
        }

        public Builder<T> owner(UUID plUUID) {
            this.prop.setOwner(plUUID);
            return this;
        }

        public T spawn(Vector3 spawnPos) {
            return EffectRegistrar.registerFX(this.context.makeParticle(spawnPos), this.prop);
        }

    }

}
