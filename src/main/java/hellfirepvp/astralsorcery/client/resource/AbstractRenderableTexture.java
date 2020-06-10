/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractRenderableTexture
 * Created by HellFirePvP
 * Date: 25.02.2018 / 23:24
 */
public abstract class AbstractRenderableTexture {

    private final ResourceLocation key;

    protected AbstractRenderableTexture(ResourceLocation key) {
        this.key = key;
    }

    public final ResourceLocation getKey() {
        return key;
    }

    public abstract void bindTexture();

    public abstract RenderState.TextureState asState();

    public abstract Tuple<Float, Float> getUVOffset();

    public abstract float getUWidth();

    public abstract float getVWidth();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractRenderableTexture that = (AbstractRenderableTexture) o;
        return Objects.equals(this.getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getKey());
    }

    public static abstract class Full extends AbstractRenderableTexture {

        public Full(ResourceLocation key) {
            super(key);
        }

        @Override
        public Tuple<Float, Float> getUVOffset() {
            return new Tuple<>(0F, 0F);
        }

        @Override
        public final float getUWidth() {
            return 1.0F;
        }

        @Override
        public final float getVWidth() {
            return 1.0F;
        }
    }
}
