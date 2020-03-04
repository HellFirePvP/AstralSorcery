/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.Tuple;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractRenderableTexture
 * Created by HellFirePvP
 * Date: 25.02.2018 / 23:24
 */
public abstract class AbstractRenderableTexture {

    public abstract void bindTexture();

    public abstract Tuple<Float, Float> getUVOffset();

    public abstract float getUWidth();

    public abstract float getVWidth();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    public static Full wrap(ITextureObject obj) {
        return new Full() {
            @Override
            public void bindTexture() {
                GlStateManager.bindTexture(obj.getGlTextureId());
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }

            @Override
            public int hashCode() {
                return 0;
            }
        };
    }

    public static abstract class Full extends AbstractRenderableTexture {

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
