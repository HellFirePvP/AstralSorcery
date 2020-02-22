/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.handler;

import hellfirepvp.astralsorcery.client.effect.EffectProperties;
import hellfirepvp.astralsorcery.client.effect.EffectType;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.source.FXSource;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectHelper
 * Created by HellFirePvP
 * Date: 08.07.2019 / 20:54
 */
public final class EffectHelper {

    public static <T extends EntityVisualFX, C extends BatchRenderContext<T>> void refresh(T vfx, C context) {
        refresh(vfx, of(context));
    }

    public static <T extends EntityVisualFX> void refresh(T vfx, EffectProperties<T> properties) {
        if (vfx.isRemoved()) {
            EffectRegistrar.registerFX(vfx, properties);
        }
    }

    public static <T extends EntityVisualFX, C extends BatchRenderContext<T>> Builder<T> of(C ctx) {
        return new Builder<>(ctx);
    }

    public static <E extends EntityVisualFX, T extends BatchRenderContext<E>, S extends FXSource<E, T>> S spawnSource(S src) {
        return EffectRegistrar.registerSource(src);
    }

    public static class Builder<T extends EntityVisualFX> extends EffectProperties<T> {

        public Builder(BatchRenderContext<T> ctx) {
            super(ctx);
        }

        @Override
        public Builder<T> setOwner(@Nullable UUID owner) {
            return super.setOwner(owner);
        }

        @Override
        public Builder<T> setType(@Nullable EffectType type) {
            return super.setType(type);
        }

        @Override
        public Builder<T> setPosition(@Nonnull Vec3i position) {
            return super.setPosition(position);
        }

        @Override
        public Builder<T> setIgnoreLimit(boolean ignoreLimit) {
            return super.setIgnoreLimit(ignoreLimit);
        }

        public T spawn(@Nonnull Vector3 spawnPos) {
            this.setPosition(spawnPos.toBlockPos());
            return EffectRegistrar.registerFX(this.getContext().makeParticle(spawnPos), this);
        }

    }

}
