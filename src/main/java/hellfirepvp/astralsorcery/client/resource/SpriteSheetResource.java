/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpriteSheetResource
 * Created by HellFirePvP
 * Date: 14.09.2016 / 09:15
 */
public class SpriteSheetResource extends AbstractRenderableTexture {

    protected float uPart, vPart;
    protected int frameCount;
    protected int rows, columns;

    private final AbstractRenderableTexture resource;

    public SpriteSheetResource(AbstractRenderableTexture resource) {
        this(resource, 1, 1);
    }

    public SpriteSheetResource(AbstractRenderableTexture resource, int rows, int columns) {
        super(NameUtil.suffixPath(resource.getKey(), "_sprite"));
        if (rows <= 0 || columns <= 0)
            throw new IllegalArgumentException("Can't instantiate a sprite sheet without any rows or columns!");

        frameCount = rows * columns;
        this.rows = rows;
        this.columns = columns;
        this.resource = resource;

        this.uPart = 1F / ((float) columns);
        this.vPart = 1F / ((float) rows);
    }

    @Override
    public void bindTexture() {
        this.resource.bindTexture();
    }

    @Override
    public RenderState.TextureState asState() {
        return this.resource.asState();
    }

    @Override
    public Tuple<Float, Float> getUVOffset() {
        long timer = ClientScheduler.getClientTick();
        return getUVOffset(timer);
    }

    @Override
    public float getUWidth() {
        return getULength();
    }

    @Override
    public float getVWidth() {
        return getVLength();
    }

    public AbstractRenderableTexture getResource() {
        return resource;
    }

    public float getULength() {
        return uPart;
    }

    public float getVLength() {
        return vPart;
    }

    public Tuple<Float, Float> getUVOffset(long frameTimer) {
        int frame = (int) (frameTimer % frameCount);
        return new Tuple<>((frame % columns) * uPart, (frame / columns) * vPart);
    }

    public Tuple<Float, Float> getUVOffset(EntityComplexFX fx) {
        float perc = ((float) fx.getAge()) / ((float) fx.getMaxAge());
        long timer = MathHelper.floor(this.getFrameCount() * perc);
        return getUVOffset(timer);
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
}
