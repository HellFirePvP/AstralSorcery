/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AbstractRenderTexture
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class AbstractRenderTexture implements ReloadableResource {

    private final ResourceLocation key;

    protected AbstractRenderTexture(ResourceLocation key) {
        this.key = key;
    }

    public final ResourceLocation getKey() {
        return key;
    }

    public SpriteSheet asSpriteSheet() {
        return this.asSpriteSheet(1, 1);
    }

    public SpriteSheet asSpriteSheet(int rows, int columns) {
        return new SpriteSheet(this, rows, columns);
    }

    public abstract void bindTexture();

    public abstract RenderStateShard.TextureStateShard asState();

    public abstract UVFrame getUV();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractRenderTexture that = (AbstractRenderTexture) o;
        return Objects.equals(this.getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getKey());
    }

    public static abstract class Full extends AbstractRenderTexture {

        public Full(ResourceLocation key) {
            super(key);
        }

        @Override
        public UVFrame getUV() {
            return UVFrame.FULL;
        }
    }
}
