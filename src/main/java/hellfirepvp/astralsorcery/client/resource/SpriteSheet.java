/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import hellfirepvp.astralsorcery.common.util.NameUtil;
import hellfirepvp.observerlib.client.util.ClientTickHelper;
import net.minecraft.client.renderer.RenderStateShard;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SpriteSheet
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SpriteSheet extends AbstractRenderTexture {

    protected float uWidthPart, vHeightPart;
    protected int frameCount;
    protected int rows, columns;

    private final AbstractRenderTexture texture;

    public SpriteSheet(AbstractRenderTexture texture, int rows, int columns) {
        super(NameUtil.suffixPath(texture.getKey(), "_sprite"));
        if (rows <= 0 || columns <= 0)
            throw new IllegalArgumentException("Can't instantiate a sprite sheet without any rows or columns!");

        this.frameCount = rows * columns;
        this.rows = rows;
        this.columns = columns;
        this.texture = texture;

        UVFrame textureUV = texture.getUV();
        this.uWidthPart = (1F / ((float) columns)) * textureUV.uWidth();
        this.vHeightPart = (1F / ((float) rows)) * textureUV.vHeight();
    }

    public AbstractRenderTexture getTexture() {
        return this.texture;
    }

    @Override
    public void bindTexture() {
        this.getTexture().bindTexture();
    }

    @Override
    public RenderStateShard.TextureStateShard asState() {
        return this.getTexture().asState();
    }

    @Override
    public UVFrame getUV() {
        long timer = ClientTickHelper.getClientTick();
        return getUV(timer);
    }

    public UVFrame getUV(long frameTimer) {
        UVFrame textureUV = this.texture.getUV();
        int frame = (int) (frameTimer % this.frameCount);
        return new UVFrame(
                textureUV.u() + (frame % this.columns) * this.uWidthPart,
                textureUV.v() + (frame / this.columns) * this.vHeightPart,
                this.uWidthPart,
                this.vHeightPart);
    }

    public int getFrameCount() {
        return frameCount;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    @Override
    public void invalidateAndReload() {
        // No-op, this is just a wrapper, the actual texture implements its unloading
    }
}
